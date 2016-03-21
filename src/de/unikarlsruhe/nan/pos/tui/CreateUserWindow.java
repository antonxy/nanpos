package de.unikarlsruhe.nan.pos.tui;

import de.unikarlsruhe.nan.pos.CardReader;
import de.unikarlsruhe.nan.pos.objects.User;

import javax.jws.soap.SOAPBinding;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author Anton Schirg
 */
public class CreateUserWindow extends Window {

    String nameEntered;

    public CreateUserWindow(final CreateUserResultHandler resultHandler, final User operator) {
        CenterLayout centerLayout = new CenterLayout();
        setCentralComponent(centerLayout);
        VerticalLayout verticalLayout = new VerticalLayout();
        centerLayout.addChild(verticalLayout);

        final Keyboard keyboard = new Keyboard(new Keyboard.KeyboardResultHandler() {
            @Override
            public void handle(String enteredText, Keyboard caller) {
                caller.clear();
                final ScanCardWindow scanCardWindow = new ScanCardWindow(new CardReader.CardReaderListener() {
                    @Override
                    public void onCardDetected(String cardnr, String uid) {
                        try {
                            if (cardnr != null) {
                                User.createUser(operator, nameEntered, cardnr);
                                resultHandler.handle(true, CreateUserWindow.this, "Created user");
                            } else {
                                resultHandler.handle(false, CreateUserWindow.this, "No card scanned");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                            resultHandler.handle(false, CreateUserWindow.this, "SQL Exception");
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                            resultHandler.handle(false, CreateUserWindow.this, "NoSuchAlgorithmException - WUT?");
                        }
                    }
                });
                getTui().openWindow(scanCardWindow);
            }
        }, "Enter new user name");
        verticalLayout.addChild(keyboard);
    }

    public interface CreateUserResultHandler {
        public void handle(boolean success, CreateUserWindow caller, String detailMessage);
    }
}
