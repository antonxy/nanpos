package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import de.unikarlsruhe.nan.pos.CardReader;

/**
 * @author Anton Schirg
 */
public class ResultScreen extends Window {

    private Runnable callback;
    private boolean done = false;

    public ResultScreen(String result, boolean success) {
        CenterLayout centerLayout = new CenterLayout();
        centerLayout.setBackgroundColor(success ? TextColor.ANSI.GREEN : TextColor.ANSI.RED);
        if (success) {
            CardReader.getInstance().successAnimation();
        } else {
            CardReader.getInstance().failAnimation();
        }
        setCentralComponent(centerLayout);
        Button label = new Button(result, new Runnable() {
            @Override
            public void run() {
                doDone();
            }
        });
        centerLayout.addChild(label);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    doDone();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void doDone() {
        if (!done) {
            done = true;
            if (callback != null) {
                callback.run();
            }
        }
    }

    public void setDoneCallback(Runnable callback) {
        this.callback = callback;
    }
}
