#include <Streaming.h>
#include <SPI.h>
#include <MFRC522.h>

#define PARAMS_MAX_LENGTH 128
#define LED_R 2 // 8
#define LED_G 3 // 4
#define BUZZ 5  // 6
#define DOOR 4 // 5
#define RXMODE_PIN 6 //7

#define SS_PIN 10 // PB6
#define RST_PIN 9 // PB5
// MOSI 11
// MISO 12
// SCK 13


//#define DEBUG

// Fitnessraum = 2, Kellertuer = 1
uint8_t nid = 1;

// set to Serial1 on Leonardo, Serial on Uno etc.
#define SerialC Serial


/* define operating mode
 *
 * 0: idle
 * 1: receive node id
 * 2: receive command
 * 3: recevie parameters
 * 4: parse
 */
uint8_t mode = 0;

char inByte = 0;         // incoming serial byte

bool receiving = 0;
bool receiveDone = 0;
bool sendFlag = 0;
bool receiveOverflow = 0;

String readBuffer;
String command;


// Definitions for RFID



MFRC522 mfrc522(SS_PIN, RST_PIN);	// Create MFRC522 instance.

// Prepare key - all keys are set to FFFFFFFFFFFFh at chip delivery from the factory.
MFRC522::MIFARE_Key wallet_key = { 0xA0, 0xA1, 0xA2, 0xA3, 0xA4, 0xA5   }; // WALLET_KEY (KEY_MIFARE_APPLICATION_DIRECTORY)
MFRC522::MIFARE_Key default_key = { 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF   }; // DEFAULT_KEY


// RFID data

bool hasData = 0;
byte uid[4] = {0,0,0,0};
byte lastUid[4] = {0,0,0,0};
char cardnr[13] = {0,0,0,0, 0,0,0,0, 0,0,0,0 ,0}; // 13 due to string converion cutoff

void setup(){
    // start serial port at 9600 bps and wait for port to open:
    Serial.begin(115200);
    SerialC.begin(115200);
    delay(50);
    SPI.begin();			// Init SPI bus
	mfrc522.PCD_Init();	// Init MFRC522 card
	Serial.println("Scan Card to see UID and Card Nr");

    pinMode(LED_R, OUTPUT);   //
    pinMode(LED_G, OUTPUT);   //
    pinMode(BUZZ, OUTPUT);   //
    pinMode(DOOR, OUTPUT);   //
    pinMode(RXMODE_PIN, OUTPUT);
    //establishContact();  // send a byte to establish contact until receiver responds
}

uint8_t b = 0;
uint32_t readTime = 0;

bool checkId = 0;

byte blinkmask_r = 33; // LED red
byte blinkmask_g = 18; // LED green
byte blinkmask_b = 0; // Buzzer
byte blinkmask_d = 0; // Door openener
uint8_t blinkmask_speed = 1;

unsigned long last_check = 0;
unsigned long blinkmask_override = 0;

/* 
 * SerialCommHelper: 
 * @return bool: true if data received, false if not.
 */
bool SerialComHelper(){
    bool isReceivingData = false;
    if(SerialC.available() > 0) {
        // get incoming byte:
        inByte = SerialC.read();
        //SerialC << inByte;

        if(receiving){
            bool timeout = (readTime+500) < millis();
            timeout = 0;
            if(readBuffer.length() > 128 || inByte == '\n' || inByte == '\r' || inByte == '|' || inByte == 13 || timeout){
                // abort
                receiving = 0;
                receiveOverflow = (readBuffer.length() > 128) ? 1:0;
                receiveDone = 1;
                readTime = 0;
                if(timeout){
                    //Serial << "receive timed out "<< (millis() - readTime) << " "<<millis()<<endl;
                    // clear
                    receiveDone = 0;
                }
                //SerialC<<endl;
                if(mode == 2) mode = check_command(); // needed here if trailing slash is forgotten

                //Serial << "end receiving "<<timeout<<endl;
            }else{

                // check command (before node-id check to avoid early termination
                if(mode == 2){
                    if(inByte == '/'){
                        readBuffer = "";
                        mode = check_command();

                    }else command += inByte;
                }

                // check if node id
                if(checkId && inByte == '/'){
                    //Serial << "UID is: "<<readBuffer.toInt()<<"="<<nid<<endl;
                    uint8_t called_nid = readBuffer.toInt();
                    checkId = 0;
                    readBuffer = "";
                    mode = 2;
                    if(called_nid != nid){
                        //Serial << "UID is: "<<called_nid<<"="<<nid<<endl;
                        receiving = 0;
                        receiveDone = 0;
                        return false; // data is not for us, ignore.
                    }
                }

                readBuffer += inByte;
            }
        }else{ // not receiving, check for command start '$'
            if(inByte != '$') return false;
            // start receiving command;
            mode = 1;
            receiving = 1;
            readBuffer = "";
            receiveDone = 0;
            receiveOverflow = 0;
            readTime = millis();
            checkId = 1;
            command = "";
            //Serial << "Start capturing data "<<readTime<<endl;
        }
        isReceivingData = true;
    }
    if(receiveDone){
        // parse

        // set RS485 driver to send
        digitalWrite(RXMODE_PIN,1);
        delay(2);
        if(!execute_command()){ // execute command prints it's own result
            SerialC << "{\"error\":1}"<<endl;
        }
        // set RS485 driver back to receive
        delay(10);
        digitalWrite(RXMODE_PIN, 0);

        //Serial << "Received command: "<<command << " with params "<<readBuffer <<endl;

        receiveDone = 0;
    }
    return isReceivingData;
}

void loop(){
    // Check for Serial input data, if not receiving check card.
    if(!SerialComHelper()) check_card();

    // led blinks
    blinkLedsHelper();
}

void sendResult(){
  SerialC << "{\"check_result\":{\"node\":"<<nid<<",\"hasData\":"<<(int)hasData;
  if(hasData){
    SerialC <<",\"cardnr\":\""<<cardnr<<"\",\"uid\":\"";
    for(byte i=0;i < 4; i++){
      SerialC.print(uid[i] < 0x10 ? "0" : "");
      SerialC.print(uid[i],HEX);
    }
    SerialC << "\"";
    //hasData = 0;
  }
  SerialC << "}}"<<endl;
}

bool check_card(){
    // Look for new cards
	if ( ! mfrc522.PICC_IsNewCardPresent()) {
		return 0;
	}

	// Select one of the cards
	if ( ! mfrc522.PICC_ReadCardSerial()) {
		return 0;
	}
    // Dump debug info about the card. PICC_HaltA() is automatically called.
    //mfrc522.PICC_DumpToSerial(&(mfrc522.uid));
#ifdef DEBUG
    Serial << endl <<"UID: ";
#endif
    uint8_t size = (mfrc522.uid.size > 4) ? 4 : mfrc522.uid.size;
    for(byte i=0;i < 4; i++){
#ifdef DEBUG
        Serial.print(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " ");
        Serial.print(mfrc522.uid.uidByte[i],HEX);
#endif
        uid[i] = mfrc522.uid.uidByte[i];
    }

    //byte piccType = mfrc522.PICC_GetType(mfrc522.uid.sak);
    // try reading
#ifdef DEBUG
    Serial  << " Trying to read card number ";
#endif
    readKitCardNumber();

    // check if new card, else timeout

    hasData = 1;

    if(hasData){
        blinkmask_override = millis()+10000;
        blinkmask_r = 33;
        blinkmask_g = 18;
        blinkmask_b = 0;
        blinkmask_speed = 1;
        
        digitalWrite(BUZZ,1);
        sendResult();
        delay(50);
        digitalWrite(BUZZ,0);
    }

    // Halt PICC
    mfrc522.PICC_HaltA();
    // Stop encryption on PCD
    mfrc522.PCD_StopCrypto1();
}



boolean readKitCardNumber(){
    char kitCardNumber[13] = {0,0,0,0, 0,0,0,0, 0,0,0,0 ,0};
    MFRC522::MIFARE_Key card_number_key = {
        0x56, 0x38, 0x9F, 0x80, 0xA5, 0xCF   }; // CARD_NUMBER_KEY
    uint8_t cardNumberBlock = 44;
    uint8_t status;

    status = mfrc522.PCD_Authenticate(MFRC522::PICC_CMD_MF_AUTH_KEY_A, cardNumberBlock, &card_number_key, &(mfrc522.uid));
    
    uint8_t buffer[18];
    uint8_t bufferSize[1] = {18};
    
    if(status != MFRC522::STATUS_OK) {
#ifdef DEBUG
        Serial.print("PCD_Authenticate() failed: ");
        Serial.println(mfrc522.GetStatusCodeName(status));
        delay(1000);
#endif
        // only pass uid, convert to 
        uint32_t temp = 0;
        uint32_t helper = uid[0] & 0xff; // cast to helper and mask for correct bit operations
        temp += helper << 24;
        helper = uid[1] & 0xff;
        temp += helper << 16;
        helper = uid[2] & 0xff;
        temp += helper << 8;
        helper = uid[3] & 0xff;
        temp += helper;
        String kitCardNrStr = "UI"+ String(temp,DEC);
        kitCardNrStr.toCharArray(kitCardNumber,13);
        kitCardNrStr.toCharArray(cardnr,13);
    }else{
        uint8_t readRes = mfrc522.MIFARE_Read(44,buffer,bufferSize);
        //char cardNumber[12] = {0,0,0,0, 0,0,0,0, 0,0,0,0};
        for(uint8_t i=0;i<12;i++){
            kitCardNumber[i] = buffer[i];
            cardnr[i] = buffer[i];
        }
    }
    
#ifdef DEBUG
    Serial << "Card Number: "<<kitCardNumber << " " << sizeof(kitCardNumber) << endl;
#endif    
    return 1;
}


/*
 * Serial Comm Funtions
 */

uint8_t check_command(){
    if(command == "check") return 4; // no params
    if(command == "setMode") return 3; // params
    if(command == "confirm") return 4;
    if(command == "deny") return 4;
    if(command == "clear") return 4;
    return 0;
}
void reset_input(){
    receiving = 0;
    sendFlag = 0;
    hasData = 0;
    //params_overflow = 0;
    readBuffer = "";
    command = "";
}



bool execute_command(){
    if(command == "check"){
        sendResult();
        last_check = millis();
        return 1;
    }
    if(command == "confirm"){
        // set blinking
        SerialC << "{\"check_result\":{\"node\":"<<nid<<",\"hasData\":"<<(int)hasData<<"}}"<<endl;
        hasData = 0;
        //blink leds
        blinkmask_override = millis()+1200;
        blinkmask_g = 0xff;
        blinkmask_r = 0;
        blinkmask_speed = 1;
        // activate door opener
        blinkmask_d = 0xff;
        blinkmask_b = 0xff;
        return 1;
    }
    if(command == "deny"){
        // set blinking
        SerialC << "{\"check_result\":{\"node\":"<<nid<<",\"hasData\":"<<(int)hasData<<"}}"<<endl;
        hasData = 0;
        //blink leds
        blinkmask_override = millis()+2000;
        blinkmask_r = 0x1f;
        blinkmask_g = 0;
        blinkmask_b = 0x0f;
        blinkmask_speed = 1;
        return 1;
    }
    if(command == "clear"){
        // set blinking
        SerialC << "{\"check_result\":{\"node\":"<<nid<<",\"hasData\":"<<(int)hasData<<"}}"<<endl;
        hasData = 0;
        //blink leds
        blinkmask_override = millis()+2;
        blinkmask_r = 0x1f;
        blinkmask_g = 0;
        blinkmask_b = 0x00;
        blinkmask_speed = 1;
        return 1;
    }
    return 0;
}


void blinkLedsHelper(){
    byte blinkmask = 1<<((millis()/(100*blinkmask_speed))%8);

    digitalWrite(LED_R,(blinkmask & blinkmask_r));
    digitalWrite(LED_G,(blinkmask & blinkmask_g));
    digitalWrite(BUZZ,(blinkmask & blinkmask_b));
    digitalWrite(DOOR,(blinkmask & blinkmask_d));
    
    // set blink mode
    if(blinkmask_override < millis()){
        if(blinkmask_r == 33 && blinkmask_g == 18){ // timeout wait for check
            hasData = 0;
        }
        // reset data
        blinkmask_override = 0;
        blinkmask_d = 0x00;
        blinkmask_b = 0x00;

        // status blink
        if((millis() - last_check) > 8000){
            blinkmask_g = 0;
            blinkmask_r = 1;
            blinkmask_speed = 5;
        }else{
            blinkmask_g = 1;
            blinkmask_r = 0;
            blinkmask_speed = 5;
        }
    }
}
