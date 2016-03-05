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
public class LoginWindow extends Component {

    private final VerticalLayout verticalLayout;

    public LoginWindow(final NumpadResultHandler resultHandler) {
        verticalLayout = new VerticalLayout();
        verticalLayout.setParent(this);

        AsciiArt asciiArt = new AsciiArt();
        verticalLayout.addChild(asciiArt);

        final Numpad numpad = new Numpad(new Numpad.NumpadResultHandler() {
            @Override
            public void handle(String enteredText, Numpad caller) {
                caller.clear();
                try {
                    User userByPIN = User.getUserByPIN(enteredText);
                    String detailMessage = userByPIN == null ? "Unknown user" : "Success";
                    resultHandler.handle(userByPIN, LoginWindow.this, detailMessage);
                } catch (SQLException e) {
                    e.printStackTrace();
                    resultHandler.handle(null, LoginWindow.this, "SQL Exception");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    resultHandler.handle(null, LoginWindow.this, "NoSuchAlgorithmException - WAT?");
                }
            }
        }, true);
        verticalLayout.addChild(numpad);
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

    @Override
    protected void onClick(TerminalPosition position) {
        verticalLayout.onClick(position);
    }

    public interface NumpadResultHandler {
        public void handle(User user, LoginWindow caller, String detailMessage);
    }
}
