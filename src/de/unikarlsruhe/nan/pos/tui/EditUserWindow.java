package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;
import de.unikarlsruhe.nan.pos.CardReader;
import de.unikarlsruhe.nan.pos.objects.User;
import de.unikarlsruhe.nan.pos.objects.Utils;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * @author Anton Schirg
 */
public class EditUserWindow extends Window {
    private User user;
    private final VerticalLayout verticalLayout;
    private final Label balanceLabel;
    private final CenterLayout centerLayout;
    private static final long TIMEOUT = 30 * 1000;
    private Runnable done;

    public EditUserWindow(final User user) {
        this.user = user;
        centerLayout = new CenterLayout();
        setCentralComponent(centerLayout);
        verticalLayout = new VerticalLayout();
        centerLayout.addChild(verticalLayout);

        balanceLabel = new Label("User: " + user.getName() + " | " + "Balance: " + Utils.formatPrice(user.getBalance()));
        verticalLayout.addChild(balanceLabel);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        verticalLayout.addChild(horizontalLayout);

        horizontalLayout.addChild(new Button("Back", new Runnable() {
            @Override
            public void run() {
                close();
                if (done != null) {
                    done.run();
                }
            }
        }));

        horizontalLayout.addChild(new Button("Recharge", new Runnable() {
            @Override
            public void run() {
                        getTui().openWindow(
                                new RechargeWindow(
                                        user,
                                        new Runnable() {
                                            @Override
                                            public void run() {

                                            }
                                        }, false));
            }
        }));

        horizontalLayout.addChild(new Button("Discharge", new Runnable() {
            @Override
            public void run() {
                getTui().openWindow(
                        new RechargeWindow(
                                user,
                                new Runnable() {
                                    @Override
                                    public void run() {
                                    }
                                }, true));
            }
        }));

        horizontalLayout.addChild(new Button("Set Card", new Runnable() {
            @Override
            public void run() {
                final ScanCardWindow scanCardWindow = new ScanCardWindow(new CardReader.CardReaderListener() {
                    @Override
                    public boolean onCardDetected(String cardnr, String uid) {
                        try {
                            if (cardnr != null) {
                                user.setCard(cardnr);
                                ResultScreen rs = new ResultScreen("Updated Card", true);
                                rs.setDoneCallback(new Runnable() {
                                    @Override
                                    public void run() {
                                        getTui().closeWindowsAbove(EditUserWindow.this);
                                    }
                                });
                                getTui().openWindow(rs);
                                return true;
                            } else {
                                getTui().closeWindowsAbove(EditUserWindow.this);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            ResultScreen rs = new ResultScreen("SQL Exception", false);
                            rs.setDoneCallback(new Runnable() {
                                @Override
                                public void run() {
                                    getTui().closeWindowsAbove(EditUserWindow.this);
                                }
                            });
                            getTui().openWindow(rs);
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                        return false;
                    }
                });
                getTui().openWindow(scanCardWindow);
            }
        }));
        horizontalLayout.addChild(new Button("Unset Card", new Runnable() {
            @Override
            public void run() {
                    try {
                        user.setCard(null);
                        ResultScreen rs = new ResultScreen("Unset Card", true);
                        rs.setDoneCallback(new Runnable() {
                            @Override
                            public void run() {
                                getTui().closeWindowsAbove(EditUserWindow.this);
                            }
                        });
                        getTui().openWindow(rs);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        ResultScreen rs = new ResultScreen("SQL Exception", false);
                        rs.setDoneCallback(new Runnable() {
                            @Override
                            public void run() {
                                getTui().closeWindowsAbove(EditUserWindow.this);
                            }
                        });
                        getTui().openWindow(rs);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        ));

        horizontalLayout.addChild(new Button("Set PIN", new Runnable() {
            @Override
            public void run() {
                Window numWin = new Window();
                CenterLayout cl = new CenterLayout();
                Numpad pad = new Numpad(new Numpad.NumpadResultHandler() {
                    @Override
                    public void handle(String enteredText, Numpad caller) {
                        if (enteredText != null) {
                            try {
                                user.setPin(enteredText);
                                ResultScreen rs = new ResultScreen("Set PIN", true);
                                rs.setDoneCallback(new Runnable() {
                                    @Override
                                    public void run() {
                                        getTui().closeWindowsAbove(EditUserWindow.this);
                                    }
                                });
                                getTui().openWindow(rs);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                ResultScreen rs = new ResultScreen("SQL Exception", false);
                                rs.setDoneCallback(new Runnable() {
                                    @Override
                                    public void run() {
                                        getTui().closeWindowsAbove(EditUserWindow.this);
                                    }
                                });
                                getTui().openWindow(rs);
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                                System.exit(1);
                            }
                        }
                    }
                }, true, "PIN");
                cl.addChild(pad);
                numWin.setCentralComponent(cl);
                getTui().openWindow(numWin);
            }
        }));

        horizontalLayout.addChild(new Button("Unset PIN", new Runnable() {
            @Override
            public void run() {
                try {
                    user.setPin(null);
                    ResultScreen rs = new ResultScreen("Unset PIN", true);
                    rs.setDoneCallback(new Runnable() {
                        @Override
                        public void run() {
                            getTui().closeWindowsAbove(EditUserWindow.this);
                        }
                    });
                    getTui().openWindow(rs);
                } catch (SQLException e) {
                    e.printStackTrace();
                    ResultScreen rs = new ResultScreen("SQL Exception", false);
                    rs.setDoneCallback(new Runnable() {
                        @Override
                        public void run() {
                            getTui().closeWindowsAbove(EditUserWindow.this);
                        }
                    });
                    getTui().openWindow(rs);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }));
    }

    void setDoneCallback(Runnable done) {
        this.done = done;
    }

    @Override
    TerminalSize getPreferredSize() {
        return centerLayout.getPreferredSize();
    }

    @Override
    protected void layout(TerminalRectangle position) {
        centerLayout.layout(position);
    }

    @Override
    void redraw() {
        centerLayout.redraw();
    }

    @Override
    protected void onClick(TerminalPosition position) {
        centerLayout.onClick(position);
    }
}