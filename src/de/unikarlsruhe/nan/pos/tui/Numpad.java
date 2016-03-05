package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;
import de.unikarlsruhe.nan.pos.objects.User;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * @author Anton Schirg
 */
public class Numpad extends Component {

    private final VerticalLayout verticalLayout;
    private final GridLayout gridLayout;
    private final Label label;
    private final boolean secure;
    private String enteredText = "";

    public Numpad(final NumpadResultHandler resultHandler, boolean secure) {
        this.secure = secure;
        verticalLayout = new VerticalLayout();
        verticalLayout.setParent(this);

        AsciiArt asciiArt = new AsciiArt();
        verticalLayout.addChild(asciiArt);

        label = new Label("");
        verticalLayout.addChild(label);

        gridLayout = new GridLayout(3);
        verticalLayout.addChild(gridLayout);

        for (int i = 1; i <= 9; i++) {
            final int buttonNumber = i;
            gridLayout.addChild(new Button(Integer.toString(buttonNumber), new Runnable() {
                @Override
                public void run() {
                    enteredText += Integer.toString(buttonNumber);
                    updateLabel();
                }
            }));
        }
        gridLayout.addChild(new Button("C", new Runnable() {
            @Override
            public void run() {
                if (enteredText.length() > 0) {
                    enteredText = enteredText.substring(0, enteredText.length() - 1);
                    updateLabel();
                }
            }
        }));
        gridLayout.addChild(new Button("0", new Runnable() {
            @Override
            public void run() {
                enteredText += "0";
                updateLabel();
            }
        }));
        gridLayout.addChild(new Button("OK", new Runnable() {
            @Override
            public void run() {
                try {
                    User userByPIN = User.getUserByPIN(enteredText);
                    String detailMessage = userByPIN == null ? "Unknown user" : "Success";
                    resultHandler.handle(userByPIN, Numpad.this, detailMessage);
                } catch (SQLException e) {
                    e.printStackTrace();
                    resultHandler.handle(null, Numpad.this, "SQL Exception");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    resultHandler.handle(null, Numpad.this, "NoSuchAlgorithmException - WAT?");
                }
            }
        }));
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
        if (secure) {
            String stars = "";
            for (int i = 0; i < enteredText.length(); i++) {
                stars += "*";
            }
            label.setText(stars);
        } else {
            label.setText(enteredText);
        }
    }

    public void clear() {
        enteredText = "";
        updateLabel();
    }

    @Override
    protected void onClick(TerminalPosition position) {
        verticalLayout.onClick(position);
    }

    public interface NumpadResultHandler {
        public void handle(User user, Numpad caller, String detailMessage);
    }
}
