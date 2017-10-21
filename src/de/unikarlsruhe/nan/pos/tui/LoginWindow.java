package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import de.unikarlsruhe.nan.pos.CardReader;
import de.unikarlsruhe.nan.pos.objects.User;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * @author Anton Schirg
 */
public class LoginWindow extends Window {

    private final CardReader.CardReaderListener cardReaderListener;
    private final LoginResultHandler resultHandler;
    private Button cancelButton = null;
    private boolean check_pin;

    public LoginWindow(final LoginResultHandler resultHandler, boolean withAsciiArt, String action, boolean cancelable, boolean check_pin) {
        this.check_pin = check_pin;
        CenterLayout centerLayout = new CenterLayout();
        setCentralComponent(centerLayout);
        VerticalLayout verticalLayout = new VerticalLayout();
        centerLayout.addChild(verticalLayout);

        if (withAsciiArt) {
            AsciiArt asciiArt = new AsciiArt();
            verticalLayout.addChild(asciiArt);
        }

        if (action != null) {
            Label messageLabel = new Label("Scan card to " + action);
            verticalLayout.addChild(messageLabel);
            Label messageLabel2 = new Label("or");
            verticalLayout.addChild(messageLabel2);
            Label messageLabel3 = new Label("Touch to " + action + " using PIN");
            verticalLayout.addChild(messageLabel3);
        }

        final CardReader cardReader = CardReader.getInstance();
        cardReaderListener = new CardReader.CardReaderListener() {
            @Override
            public boolean onCardDetected(String cardnr, String uid) {
                if (LoginWindow.this.isVisible()) {
                    try {
                        User userByCardnr = User.getUserByCardnr(cardnr);
                        if (userByCardnr != null) {
                            resultHandler.handle(userByCardnr, LoginWindow.this, "Success");
                            cardReader.successAnimation();
                            return true;
                        } else {
                            resultHandler.handle(null, LoginWindow.this, "Unknown card");
                            //cardReader.failAnimation();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        resultHandler.handle(null, LoginWindow.this, "SQL Exception");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        resultHandler.handle(null, LoginWindow.this, "NoSuchAlgorithmException - WAT?");
                    }
                    return false;
                } else {
                    return false;
                }
            }
        };
        cardReader.setListener(cardReaderListener);

        if (cancelable) {
            cancelButton = new Button("Cancel", new Runnable() {
                @Override
                public void run() {
                    resultHandler.handle(null, LoginWindow.this, "Canceled");
                }
            });
            verticalLayout.addChild(cancelButton);
        }
        this.resultHandler = resultHandler;
    }



    @Override
    protected void onClick(TerminalPosition position) {
        //Can click anywhere on screen but button
        if (cancelButton != null && cancelButton.getPosition().isInside(position)) {
            super.onClick(position);
            return;
        }

        CardReader.getInstance().disableListener();

        final Window kbdWindow = new Window();
        CenterLayout centerLayout = new CenterLayout();
        Keyboard userKbd = new Keyboard(new Keyboard.KeyboardResultHandler() {
            @Override
            public void handle(final String userText, Keyboard caller) {
                kbdWindow.close();
                if (userText != null) {
                    if (check_pin) {
                        final Window pinWindow = new Window();
                        CenterLayout centerLayout = new CenterLayout();
                        Numpad pinNpd = new Numpad(new Numpad.NumpadResultHandler() {
                            @Override
                            public void handle(String pinText, Numpad caller) {
                                pinWindow.close();
                                if (pinText != null) {
                                    try {
                                        User userByName = User.getUserByNameAndPin(userText, pinText);
                                        if (userByName != null) {
                                            resultHandler.handle(userByName, LoginWindow.this, "Success");
                                        } else {
                                            resultHandler.handle(null, LoginWindow.this, "Unknown user or wrong PIN");
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
                        }, true, "PIN");
                        centerLayout.addChild(pinNpd);
                        pinWindow.setCentralComponent(centerLayout);
                        getTui().openWindow(pinWindow);
                    } else {
                        try {
                            User userByName = User.getUserByName(userText);
                            if (userByName != null) {
                                resultHandler.handle(userByName, LoginWindow.this, "Success");
                            } else {
                                resultHandler.handle(null, LoginWindow.this, "Unknown user");
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
            }
        }, "Username");
        centerLayout.addChild(userKbd);
        kbdWindow.setCentralComponent(centerLayout);
        getTui().openWindow(kbdWindow);
    }

    @Override
    public void close() {
        CardReader.getInstance().disableListener();
        super.close();
    }

    public interface LoginResultHandler {
        public void handle(User user, LoginWindow caller, String detailMessage);
    }

    @Override
    public void onVisible() {
        CardReader.getInstance().setListener(cardReaderListener);
    }
}
