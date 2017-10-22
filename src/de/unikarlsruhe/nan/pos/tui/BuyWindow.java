package de.unikarlsruhe.nan.pos.tui;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;

import de.unikarlsruhe.nan.pos.PS2BarcodeScanner;
import de.unikarlsruhe.nan.pos.objects.Product;
import de.unikarlsruhe.nan.pos.objects.User;
import de.unikarlsruhe.nan.pos.objects.Utils;

/**
 * @author Anton Schirg
 */
public class BuyWindow extends Window {
    private User user;
    private final VerticalLayout verticalLayout;
    private final Label balanceLabel;
    private BuyWindowResultHandler resultCallback;
    private final CenterLayout centerLayout;
    private static final long TIMEOUT = 30 * 1000;

    public BuyWindow(final User user) {
        this.user = user;
        centerLayout = new CenterLayout();
        setCentralComponent(centerLayout);
        verticalLayout = new VerticalLayout();
        centerLayout.addChild(verticalLayout);

        balanceLabel = new Label("User: " + user.getName() + " | " + "Balance: " + Utils.formatPrice(user.getBalance()));
        verticalLayout.addChild(balanceLabel);
        GridLayout gridLayout = new GridLayout(4);
        verticalLayout.addChild(gridLayout);

        try {
            List<Product> allProducts = Product.getAllProducts();
            for (final Product product : allProducts) {
                gridLayout.addChild(new Button(product.getName() + "\n"
                        + Utils.formatPrice(product.getPrice()), new Runnable() {
                    @Override
                    public void run() {
                        clickedProduct(product);
                    }
                }));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        verticalLayout.addChild(horizontalLayout);

        horizontalLayout.addChild(new Button("Back", new Runnable() {
            @Override
            public void run() {
                resultCallback.handle(null, true);
            }
        }));

        if (user.isOpeartor()) {
            horizontalLayout.addChild(new Button("Recharge", new Runnable() {
                @Override
                public void run() {
                    getTui().openWindow(
                            new LoginWindow(
                                    new LoginWindow.LoginResultHandler() {
                                        @Override
                                        public void handle(User user,
                                                final LoginWindow caller,
                                                String detailMessage) {
                                            if (user == null) {
                                                final ResultScreen resultScreen = new ResultScreen(
                                                        detailMessage, false);
                                                resultScreen
                                                        .setDoneCallback(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                caller.close();
                                                            }
                                                        });
                                                getTui().openWindow(
                                                        resultScreen);
                                            } else {
                                                getTui().openWindow(
                                                        new RechargeWindow(
                                                                user,
                                                                new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        caller.close();
                                                                    }
                                                                }, false));
                                            }
                                        }
                                    }, false, "recharge", true, false));
                }
            }));

            horizontalLayout.addChild(new Button("Discharge", new Runnable() {
                @Override
                public void run() {
                    getTui().openWindow(
                            new LoginWindow(
                                    new LoginWindow.LoginResultHandler() {
                                        @Override
                                        public void handle(User user,
                                                final LoginWindow caller,
                                                String detailMessage) {
                                            if (user == null) {
                                                final ResultScreen resultScreen = new ResultScreen(
                                                        detailMessage, false);
                                                resultScreen
                                                        .setDoneCallback(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                caller.close();
                                                            }
                                                        });
                                                getTui().openWindow(
                                                        resultScreen);
                                            } else {
                                                getTui().openWindow(
                                                        new RechargeWindow(
                                                                user,
                                                                new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        caller.close();
                                                                    }
                                                                }, true));
                                            }
                                        }
                                    }, false, "discharge", true, false));
                }
            }));

            horizontalLayout.addChild(new Button("Add User", new Runnable() {
                @Override
                public void run() {
                    getTui().openWindow(
                            new CreateUserWindow(
                                    new CreateUserWindow.CreateUserResultHandler() {
                                        @Override
                                        public void handle(boolean success,
                                                final CreateUserWindow caller,
                                                String detailMessage) {
                                            final ResultScreen resultScreen = new ResultScreen(
                                                    detailMessage,
                                                    success);
                                            resultScreen
                                                    .setDoneCallback(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            caller.close();
                                                        }
                                                    });
                                            getTui().openWindow(resultScreen);
                                        }
                                    }, user));
                }
            }));

            horizontalLayout.addChild(new Button("All Users", new Runnable() {
                @Override
                public void run() {
                    getTui().openWindow(
                            new UserOverview());
                }
            }));
        }

        PS2BarcodeScanner.getInstance().setBarcodeListener(
                new PS2BarcodeScanner.BarcodeListener() {
                    @Override
                    public void barcodeScanned(long barCode) {
                        System.err.println("Scanned " + barCode);
                        try {
                            Product byEAN = Product.getByEAN(barCode);
                            if (byEAN != null) {
                                clickedProduct(byEAN);
                            } else {
                                resultCallback.handle("Unknown product",
                                        false);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        if (getTui() != null
                                && System.currentTimeMillis()
                                        - getTui().getLastInputTime() > TIMEOUT) {
                            PS2BarcodeScanner.getInstance().removeBarcodeListener();
                            close();
                            return;
                        }
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void clickedProduct(Product product) {
        PS2BarcodeScanner.getInstance().removeBarcodeListener();
        try {
            if (this.isVisible()) {
                boolean success = false;
                try {
                    success = user.buy(product);
                } catch (SQLException e) {
                    e.printStackTrace();
                    resultCallback.handle("SQL Error - could not buy the product", false);
                    return;
                }
                String balance = "ERROR";
                try {
                    user.reloadBalance();
                    balance = Utils.formatPrice(user.getBalance());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                resultCallback.handle(
                        success ? "Big success - you have bought the product\nNew Balance: " + balance
                                : "Fatal error - could not buy the product",
                        success);
            } else {
                resultCallback.handle("Fatal error - was not logged in. This should never happen.", false);
            }
        } finally {
            user = null;
        }
    }

    public void setResultCallback(BuyWindowResultHandler resultCallback) {
        this.resultCallback = resultCallback;
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

    public interface BuyWindowResultHandler {
        public void handle(String result, boolean success);
    }
}
