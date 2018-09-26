package de.unikarlsruhe.nan.pos.tui;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import com.googlecode.lanterna.TerminalPosition;

import de.unikarlsruhe.nan.pos.CardReader;
import de.unikarlsruhe.nan.pos.NoteValidator;
import de.unikarlsruhe.nan.pos.objects.User;

/**
 * @author Anton Schirg
 */
public class LoginWindow extends Window {

    private final CardReader.CardReaderListener cardReaderListener;
    private final LoginResultHandler resultHandler;
    private Button cancelButton = null;
    private boolean check_pin;
    private NoteValidator.NoteListener noteListener;

    public LoginWindow(final LoginResultHandler resultHandler,
                       boolean withAsciiArt, String action, boolean cancelable,
                       boolean check_pin) {
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
            if(withAsciiArt) {
                Label messageLabel4 = new Label("or");
                verticalLayout.addChild(messageLabel4);
                Label messageLabel5 = new Label("Enter bank note to validate");
                verticalLayout.addChild(messageLabel5);
            }
        }

        final CardReader cardReader = CardReader.getInstance();
        cardReaderListener = new CardReader.CardReaderListener() {
            @Override
            public boolean onCardDetected(String cardnr, String uid) {
                LoginWindow.this.getTui().fireGenericInputEvent();
                if (LoginWindow.this.isVisible()) {
                    try {
                        User userByCardnr = User.getUserByCardnr(cardnr);
                        if (userByCardnr != null) {
                            cardReader.successAnimation();
                            cardReader.disableListener();
                            resultHandler.handle(userByCardnr,
                                    LoginWindow.this, "Success");
                            return true;
                        } else {
                            cardReader.disableListener();
                            resultHandler.handle(null, LoginWindow.this,
                                    "Unknown card");
                            // cardReader.failAnimation();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        resultHandler.handle(null, LoginWindow.this,
                                "SQL Exception");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        resultHandler.handle(null, LoginWindow.this,
                                "NoSuchAlgorithmException - WAT?");
                    }
                    return false;
                } else {
                    return false;
                }
            }
        };

        noteListener = new NoteValidator.NoteListener() {
            boolean wasSucc = false;

            @Override
            public boolean onNoteRead(int channel, int valueInEurCt) {
                final ResultScreen suc = new ResultScreen("Valid " + valueInEurCt / 100 + " EUR note :)", true);
                suc.setDoneCallback(new Runnable() {
                    @Override
                    public void run() {
                        suc.close();
                    }
                });
                getTui().openWindow(suc);
                wasSucc = true;
                return false;
            }

            @Override
            public void onNoteCredited(int channel, int valueInEurCt) {

            }

            @Override
            public void onNoteRejected() {
                if (wasSucc) {
                    wasSucc = false;
                    return;
                }
                final ResultScreen suc = new ResultScreen("Invalid note /o\\", false);
                suc.setDoneCallback(new Runnable() {
                    @Override
                    public void run() {
                        suc.close();
                    }
                });
                getTui().openWindow(suc);
            }
        };

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
        // Can click anywhere on screen but button
        if (cancelButton != null
                && cancelButton.getPosition().isInside(position)) {
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
                        Numpad pinNpd = new Numpad(
                                new Numpad.NumpadResultHandler() {
                                    @Override
                                    public void handle(String pinText,
                                                       Numpad caller) {
                                        pinWindow.close();
                                        if (pinText != null) {
                                            try {
                                                User userByName = User.getUserByNameAndPin(
                                                        userText, pinText);
                                                if (userByName != null) {
                                                    resultHandler.handle(
                                                            userByName,
                                                            LoginWindow.this,
                                                            "Success");
                                                } else {
                                                    resultHandler.handle(null,
                                                            LoginWindow.this,
                                                            "Unknown user or wrong PIN");
                                                }
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                                resultHandler.handle(null,
                                                        LoginWindow.this,
                                                        "SQL Exception");
                                            } catch (NoSuchAlgorithmException e) {
                                                e.printStackTrace();
                                                resultHandler
                                                        .handle(null,
                                                                LoginWindow.this,
                                                                "NoSuchAlgorithmException - WAT?");
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
                                resultHandler.handle(userByName,
                                        LoginWindow.this, "Success");
                            } else {
                                resultHandler.handle(null, LoginWindow.this,
                                        "Unknown user");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            resultHandler.handle(null, LoginWindow.this,
                                    "SQL Exception");
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                            resultHandler.handle(null, LoginWindow.this,
                                    "NoSuchAlgorithmException - WAT?");
                        }
                    }
                }
            }
        }, "Username");
        centerLayout.addChild(userKbd);
        kbdWindow.setCentralComponent(centerLayout);
        getTui().openWindow(kbdWindow);
    }

    public interface LoginResultHandler {
        public void handle(User user, LoginWindow caller, String detailMessage);
    }

    @Override
    public void onVisible() {
        CardReader.getInstance().setListener(cardReaderListener);
        NoteValidator.getInstance().setListener(noteListener);
    }

    @Override
    public void onInvisible() {
        CardReader.getInstance().disableListener();
        NoteValidator.getInstance().disableListener();
    }
}
