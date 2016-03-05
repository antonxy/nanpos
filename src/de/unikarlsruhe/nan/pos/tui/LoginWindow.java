package de.unikarlsruhe.nan.pos.tui;

import de.unikarlsruhe.nan.pos.objects.User;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * @author Anton Schirg
 */
public class LoginWindow extends Window {

    public LoginWindow(final LoginResultHandler resultHandler, boolean withAsciiArt, String message) {
        CenterLayout centerLayout = new CenterLayout();
        setCentralComponent(centerLayout);
        VerticalLayout verticalLayout = new VerticalLayout();
        centerLayout.addChild(verticalLayout);

        if (withAsciiArt) {
            AsciiArt asciiArt = new AsciiArt();
            verticalLayout.addChild(asciiArt);
        }

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
        }, true, message == null?"Login to account":message);
        verticalLayout.addChild(numpad);
    }

    public interface LoginResultHandler {
        public void handle(User user, LoginWindow caller, String detailMessage);
    }
}
