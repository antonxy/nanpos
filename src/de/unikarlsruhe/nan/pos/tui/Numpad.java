package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;

/**
 * @author Anton Schirg
 */
public class Numpad extends Component {

    private final GridLayout gridLayout;
    private String enteredText = "";

    public Numpad(final NumpadResultHandler resultHandler) {
        gridLayout = new GridLayout(3);
        gridLayout.setParent(this);
        for (int i = 1; i <= 9; i++) {
            final int buttonNumber = i;
            gridLayout.addChild(new Button(Integer.toString(buttonNumber), new Runnable() {
                @Override
                public void run() {
                    enteredText += Integer.toString(buttonNumber);
                }
            }));
        }
        gridLayout.addChild(new Button("C", new Runnable() {
            @Override
            public void run() {
                if (enteredText.length() > 0) {
                    enteredText = enteredText.substring(0, enteredText.length() - 1);
                }
            }
        }));
        gridLayout.addChild(new Button("0", new Runnable() {
            @Override
            public void run() {
                enteredText += "0";
            }
        }));
        gridLayout.addChild(new Button("OK", new Runnable() {
            @Override
            public void run() {
                resultHandler.handle(enteredText);
            }
        }));
    }

    @Override
    protected void layout(TerminalRectangle position) {
        super.layout(position);
        gridLayout.layout(position);
    }

    @Override
    void redraw() {
        gridLayout.redraw();
    }

    @Override
    TerminalSize getPreferredSize() {
        return gridLayout.getPreferredSize();
    }

    public interface NumpadResultHandler {
        public void handle(String result);
    }
}
