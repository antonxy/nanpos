package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;

/**
 * @author Anton Schirg
 */
public class ResultScreen extends Window {

    private Runnable callback;

    public ResultScreen(String result, TextColor backgroundColor) {
        CenterLayout centerLayout = new CenterLayout();
        centerLayout.setBackgroundColor(backgroundColor);
        setCentralComponent(centerLayout);
        Button label = new Button(result, new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.run();
                }
            }
        });
        centerLayout.addChild(label);
    }

    public void setDoneCallback(Runnable callback) {
        this.callback = callback;
    }
}
