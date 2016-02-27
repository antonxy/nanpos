package de.unikarlsruhe.nan.pos;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * @author Anton Schirg
 */
public class CardReader {
    private LinkedList<CardReaderListener> listeners = new LinkedList<>();
    public CardReader() {
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
                    System.exit(100);
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
            return;
        }
        callListeners(cardnr, uid);
    }

    private void callListeners(String cardnr, String uid) {
        for (CardReaderListener listener : listeners) {
            listener.onCardDetected(cardnr, uid);
        }
    }

    public void addListener(CardReaderListener listener) {
        listeners.add(listener);
    }

    public interface CardReaderListener {
        public void onCardDetected(String cardnr, String uid);
    }

    public static void main(String[] args) {
        CardReader cardReader = new CardReader();
        cardReader.addListener(new CardReaderListener() {
            @Override
            public void onCardDetected(String cardnr, String uid) {
                System.out.println("Card nr:" + cardnr + " uid:" + uid);
            }
        });
    }
}
