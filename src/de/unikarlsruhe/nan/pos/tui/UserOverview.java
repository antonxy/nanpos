package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;
import de.unikarlsruhe.nan.pos.PS2BarcodeScanner;
import de.unikarlsruhe.nan.pos.objects.Product;
import de.unikarlsruhe.nan.pos.objects.User;
import de.unikarlsruhe.nan.pos.objects.Utils;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * @author Anton Schirg
 */
public class UserOverview extends Window {
    private final VerticalLayout verticalLayout;
    private final CenterLayout centerLayout;

    public UserOverview() {
        centerLayout = new CenterLayout();
        setCentralComponent(centerLayout);
        verticalLayout = new VerticalLayout();
        centerLayout.addChild(verticalLayout);

        GridLayout gridLayout = new GridLayout(5);
        verticalLayout.addChild(gridLayout);

        try {
            List<User> allUsers = User.getAllUsers();
            for (final User user : allUsers) {
                gridLayout.addChild(new Button(user.getName() + "\n"
                        + Utils.formatPrice(user.getBalance()), new Runnable() {
                    @Override
                    public void run() {
                        getTui().openWindow(new EditUserWindow(user));
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
                close();
            }
        }));

        horizontalLayout.addChild(new Button("Select User", new Runnable() {
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
                                            caller.close();
                                            getTui().openWindow(
                                                    new EditUserWindow(user)
                                            );
                                        }
                                    }
                                }, false, "select", true, false));
            }
        }));

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
