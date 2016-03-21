package de.unikarlsruhe.nan.pos.tui;

import de.unikarlsruhe.nan.pos.CardReader;
import de.unikarlsruhe.nan.pos.objects.User;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * @author Anton Schirg
 */
public class LoginWindow extends Window {

    private final CardReader.CardReaderListener cardReaderListener;

    public LoginWindow(final LoginResultHandler resultHandler, boolean withAsciiArt, String message, boolean cancelable) {
        CenterLayout centerLayout = new CenterLayout();
        setCentralComponent(centerLayout);
        VerticalLayout verticalLayout = new VerticalLayout();
        centerLayout.addChild(verticalLayout);

        if (withAsciiArt) {
            AsciiArt asciiArt = new AsciiArt();
            verticalLayout.addChild(asciiArt);
        }

        if (message != null) {
            Label messageLabel = new Label(message);
            verticalLayout.addChild(messageLabel);
        }

        final CardReader cardReader = CardReader.getInstance();
        cardReaderListener = new CardReader.CardReaderListener() {
            @Override
            public void onCardDetected(String cardnr, String uid) {
                if (LoginWindow.this.isVisible()) {
                    try {
                        User userByCardnr = User.getUserByCardnr(cardnr);
                        if (userByCardnr != null) {
                            resultHandler.handle(userByCardnr, LoginWindow.this, "Success");
                        } else {
                            resultHandler.handle(null, LoginWindow.this, "Unknown card");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        resultHandler.handle(null, LoginWindow.this, "SQL Exception");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        resultHandler.handle(null, LoginWindow.this, "NoSuchAlgorithmException - WAT?");
                    }
                }
            }
        };
        cardReader.setListener(cardReaderListener);

        if (cancelable) {
            verticalLayout.addChild(new Button("Cancel", new Runnable() {
                @Override
                public void run() {
                    resultHandler.handle(null, LoginWindow.this, "Canceled");
                }
            }));
        }
    }

    public interface LoginResultHandler {
        public void handle(User user, LoginWindow caller, String detailMessage);
    }

    @Override
    public void onVisible() {
        CardReader.getInstance().setListener(cardReaderListener);
    }
}
