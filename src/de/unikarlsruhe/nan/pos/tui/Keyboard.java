package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;

/**
 * @author Anton Schirg
 */
public class Keyboard extends Component {

    private final VerticalLayout verticalLayout;
    private final GridLayout gridLayout;
    private final Label label;
    private String message;
    private String enteredText = "";
    private String buttons[] = {"q", "w", "e", "r", "t", "y", "u", "i", "o",
                                "a", "s", "d", "f", "g", "h", "j", "k", "l",
                                "", "z", "x", "c", "v", "b", "n", "m", "p"};

    public Keyboard(final KeyboardResultHandler resultHandler, String message) {
        this.message = message;
        verticalLayout = new VerticalLayout();
        verticalLayout.setParent(this);

        label = new Label(message);
        verticalLayout.addChild(label);

        gridLayout = new GridLayout(9);
        verticalLayout.addChild(gridLayout);

        for (final String text : buttons) {
            gridLayout.addChild(new Button(text, new Runnable() {
                @Override
                public void run() {
                    enteredText += text;
                    updateLabel();
                }
            }, 6, 3));
        }
        gridLayout.addChild(new Button("CL", new Runnable() {
            @Override
            public void run() {
                enteredText = "";
                updateLabel();
            }
        }));
        gridLayout.addChild(new Button("<-", new Runnable() {
            @Override
            public void run() {
                if (enteredText.length() > 0) {
                    enteredText = enteredText.substring(0, enteredText.length() - 1);
                    updateLabel();
                }
            }
        }, 6, 3));
        gridLayout.addChild(new Button("_", new Runnable() {
            @Override
            public void run() {
                enteredText += " ";
                updateLabel();
            }
        }, 6, 3));
        gridLayout.addChild(new Button("OK", new Runnable() {
            @Override
            public void run() {
                resultHandler.handle(enteredText, Keyboard.this);
            }
        }, 6, 3));
        gridLayout.addChild(new Button("CN", new Runnable() {
            @Override
            public void run() {
                resultHandler.handle(null, Keyboard.this);
            }
        }, 6, 3));
    }

    @Override
    protected void layout(TerminalRectangle position) {
        super.layout(position);
        verticalLayout.layout(position);
    }

    @Override
    void redraw() {
        super.redraw();
        verticalLayout.redraw();
    }

    @Override
    TerminalSize getPreferredSize() {
        return verticalLayout.getPreferredSize();
    }

    private void updateLabel() {
        if (enteredText.isEmpty()) {
            label.setText(message);
        } else {
            label.setText(enteredText);
        }
    }

    public void clear() {
        enteredText = "";
        updateLabel();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    protected void onClick(TerminalPosition position) {
        verticalLayout.onClick(position);
    }

    public interface KeyboardResultHandler {
        public void handle(String enteredText, Keyboard caller);
    }
}
