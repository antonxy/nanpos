package de.unikarlsruhe.nan.pos;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.LinkedList;

/**
 * @author Anton Schirg
 */
public class CardReader {
    private static CardReader instance = null;

    private Writer scanner_writer = null;

    public static CardReader getInstance() {
        if (instance == null) {
            instance = new CardReader();
        }
        return instance;
    }


    private CardReaderListener listener = null;
    public CardReader() {
        try {
            scanner_writer = new FileWriter("/dev/ttyUSB0");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try(BufferedReader br = new BufferedReader(new FileReader("/dev/ttyUSB0"))) {
                    String line;
                    while((line = br.readLine()) != null) {
                        handleLine(line);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void handleLine(String line) {
        String cardnr, uid;
        try {
            JSONObject jsonObject = new JSONObject(line);
            JSONObject check_result = jsonObject.getJSONObject("check_result");
            cardnr = check_result.getString("cardnr");
            uid = check_result.getString("uid");
        } catch (JSONException ex) {
            ex.printStackTrace();
            System.err.println("Received: " + line);
            return;
        }
        callListeners(cardnr, uid);
    }

    private void callListeners(String cardnr, String uid) {
        if (listener != null) {
            if (listener.onCardDetected(cardnr, uid)) {
                disableListener();
            }
        }
    }

    private void sendState(char ch) {
        try {
            if (scanner_writer != null) {
                scanner_writer.write(ch);
                scanner_writer.flush();
                System.err.println("Sent " + ch + " to card reader");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void successAnimation() {
        sendState('s');
    }

    public void failAnimation() {
        sendState('f');
    }

    public void disableListener() {
        listener = null;
        sendState('N');
    }

    public void setListener(CardReaderListener listener) {
        this.listener = listener;
        sendState('L');
    }

    public interface CardReaderListener {
        /**
         * @return valid scan - if true listener will be unregistered
         */
        public boolean onCardDetected(String cardnr, String uid);
    }

    public static void main(String[] args) {
        CardReader cardReader = new CardReader();
        cardReader.setListener(new CardReaderListener() {
            @Override
            public boolean onCardDetected(String cardnr, String uid) {
                System.out.println("Card nr:" + cardnr + " uid:" + uid);
                return true;
            }
        });
    }
}
