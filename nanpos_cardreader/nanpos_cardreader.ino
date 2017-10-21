#include <Streaming.h>
#include <SPI.h>
#include <MFRC522.h>

#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
  #include <avr/power.h>
#endif

#define PARAMS_MAX_LENGTH 128
#define BUZZ 5  // 6
#define RXMODE_PIN 6 //7
#define WS2812_PIN 8 // FIXME!

#define SS_PIN 10 // PB6
#define RST_PIN 9 // PB5
// MOSI 11
// MISO 12
// SCK 13


//#define DEBUG


// WS2812
Adafruit_NeoPixel strip = Adafruit_NeoPixel(8, WS2812_PIN, NEO_GRB + NEO_KHZ800);
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
    delay(50);
    SPI.begin();			// Init SPI bus
	mfrc522.PCD_Init();	// Init MFRC522 card
	Serial.println("Scan Card to see UID and Card Nr");

    pinMode(BUZZ, OUTPUT);   //
    pinMode(RXMODE_PIN, OUTPUT);
    //establishContact();  // send a byte to establish contact until receiver responds
      #if defined (__AVR_ATtiny85__)
    if (F_CPU == 16000000) clock_prescale_set(clock_div_1);
  #endif
  // End of trinket special code
    strip.begin();
    strip.show();
}

uint8_t b = 0;
uint32_t readTime = 0;

bool checkId = 0;

unsigned long last_check = 0;

// WS2812 state
int led_frame = 0;
long last_frame = 0;
bool listening = false;
bool play_animation = false;
int animation = -1; // 1 = Success, 2 = Fail
int anim_start_frame = 0;

void loop(){
    // Check for Serial input data, if not receiving check card.
    if(Serial.available()) {
        execute_command(Serial.read());    
    }
    check_card();
    // fancy ws2812 stuff
    if(millis() - last_frame > 25) {
        if(!play_animation) {
            for(int i=0; i<strip.numPixels(); i++) {
                if(listening) {
                    strip.setPixelColor(i, Wheel((i*10+led_frame) & 255));
                    led_frame += 1;
                } else {
                    strip.setPixelColor(i, Wheel((i+led_frame/2) & 255));
                }
            }
        } else {
            draw_animation();
        }
        strip.show();
        ++led_frame;
        last_frame = millis();
    }
}

void draw_animation() {
    int frame = led_frame - anim_start_frame;
    int num_pix = strip.numPixels();
    if(num_pix%2) {
        num_pix++;
    }
    switch(animation) {
        case 1:
            for(int i = -num_pix/2; i<num_pix/2; ++i) {
                if(i == 0 && strip.numPixels()%2) {
                   continue;
                }
                int real_i = i;
                if(i >= 0 && strip.numPixels()%2) {
                    real_i--;
                }
                strip.setPixelColor(real_i+num_pix/2, strip.Color(0, max(0, min(255,abs(i*127+40)-127*num_pix/2+frame*24)),0));
            }
            if(frame == 60) {
                play_animation = false;
            }     
            return;
        case 2:
            for(int i = -num_pix/2; i<num_pix/2; ++i) {
                if(i == 0 && strip.numPixels()%2) {
                   continue;
                }
                int real_i = i;
                if(i >= 0 && strip.numPixels()%2) {
                    real_i--;
                }
                strip.setPixelColor(real_i+num_pix/2, strip.Color(max(0, min(255,-abs(i*127+40)+frame*24)),0,0));
            }
            if(frame == 60) {
                play_animation = false;
            }     
        default:
            return;
    }
}

// Input a value 0 to 255 to get a color value.
// The colours are a transition r - g - b - back to r.
uint32_t Wheel(byte WheelPos) {
  WheelPos = 255 - WheelPos;
  if(WheelPos < 85) {
    return strip.Color(255 - WheelPos * 3, 0, WheelPos * 3);
  }
  if(WheelPos < 170) {
    WheelPos -= 85;
    return strip.Color(0, WheelPos * 3, 255 - WheelPos * 3);
  }
  WheelPos -= 170;
  return strip.Color(WheelPos * 3, 255 - WheelPos * 3, 0);
}

void sendResult(){
  if(hasData){
    Serial <<"{\"check_result\": {\"cardnr\":\""<<cardnr<<"\",\"uid\":\"";
    for(byte i=0;i < 4; i++){
      Serial.print(uid[i] < 0x10 ? "0" : "");
      Serial.print(uid[i],HEX);
    }
    Serial << "\"";
  }
  Serial << "}}"<<endl;
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
        if(listening) {
            digitalWrite(BUZZ,1);
            sendResult();
            delay(50);
            digitalWrite(BUZZ,0);
        }
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


void reset_input(){
    receiving = 0;
    sendFlag = 0;
    hasData = 0;
    //params_overflow = 0;
}

void start_animation() {
    led_frame = 0;
    play_animation = true;
    anim_start_frame = led_frame;
    strip.clear();
}

void execute_command(char c){
    switch(c) {
        case 'L': // Listening
            listening = true;
            break;
        case 'N': // Not Listening
            listening = false;
            break;
        case 's': // Success
            animation = 1;
            start_animation();
            break;
        case 'f': // fail
            animation = 2;
            start_animation();
            break;
        default:
            return;
   }
}
