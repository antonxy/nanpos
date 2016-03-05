package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TextColor;
import de.unikarlsruhe.nan.pos.objects.User;

import java.sql.SQLException;

/**
 * @author Anton Schirg
 */
public class RechargeWindow extends Window {
    public RechargeWindow(final User user, final Runnable doneCallback) {
        CenterLayout centerLayout = new CenterLayout();
        setCentralComponent(centerLayout);
        Numpad numpad = new Numpad(new Numpad.NumpadResultHandler() {
            @Override
            public void handle(String enteredText, Numpad caller) {
                try {
                    int amount = Integer.parseInt(enteredText) * 100;
                    user.recharge(amount);
                    ResultScreen resultScreen = new ResultScreen("Recharged " + amount + "ct to account", TextColor.ANSI.GREEN);
                    resultScreen.setDoneCallback(new Runnable() {
                        @Override
                        public void run() {
                            doneCallback.run();
                        }
                    });
                    getTui().openWindow(resultScreen);
                } catch (NumberFormatException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }, false, "Enter amount to recharge");
        centerLayout.addChild(numpad);
    }
}
