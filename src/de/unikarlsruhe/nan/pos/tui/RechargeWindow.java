package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TextColor;
import de.unikarlsruhe.nan.pos.objects.User;
import de.unikarlsruhe.nan.pos.objects.Utils;

import java.sql.SQLException;

/**
 * @author Anton Schirg
 */
public class RechargeWindow extends Window {
    public RechargeWindow(final User user, final Runnable doneCallback, final boolean discharge) {
        CenterLayout centerLayout = new CenterLayout();
        setCentralComponent(centerLayout);

        String message = discharge? "Enter amount to discharge (EUR)" : "Enter amount to recharge (EUR)";

        Numpad numpad = new Numpad(new Numpad.NumpadResultHandler() {
            @Override
            public void handle(String enteredText, Numpad caller) {
                try {
                    int amount = Integer.parseInt(enteredText) * 100;
                    user.recharge(discharge ? -amount : amount);
                    String chargeMsg = discharge ? "Discharged " : "Recharged ";
                    ResultScreen resultScreen = new ResultScreen(chargeMsg + Utils.formatPrice(amount) + " to account", true);
                    resultScreen.setDoneCallback(new Runnable() {
                        @Override
                        public void run() {
                            doneCallback.run();
                        }
                    });
                    getTui().openWindow(resultScreen);
                } catch (NumberFormatException | SQLException e) {
                    e.printStackTrace();
                    ResultScreen resultScreen = new ResultScreen("Error", false);
                    resultScreen.setDoneCallback(new Runnable() {
                        @Override
                        public void run() {
                            doneCallback.run();
                        }
                    });
                    getTui().openWindow(resultScreen);
                }
            }
        }, false, message);
        centerLayout.addChild(numpad);
    }
}
