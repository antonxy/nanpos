package de.unikarlsruhe.nan.pos.tui;

import de.unikarlsruhe.nan.pos.objects.User;

import javax.jws.soap.SOAPBinding;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author Anton Schirg
 */
public class CreateUserWindow extends Window {

    boolean secondEnter = false;
    String firstEntered;

    public CreateUserWindow(final CreateUserResultHandler resultHandler) {
        CenterLayout centerLayout = new CenterLayout();
        setCentralComponent(centerLayout);
        VerticalLayout verticalLayout = new VerticalLayout();
        centerLayout.addChild(verticalLayout);

        final Numpad numpad = new Numpad(new Numpad.NumpadResultHandler() {
            @Override
            public void handle(String enteredText, Numpad caller) {
                caller.clear();
                if (CreateUserWindow.this.secondEnter) {
                    if (Objects.equals(enteredText, firstEntered)) {
                        resultHandler.handle(true, CreateUserWindow.this, "Created user (well no, not implemented yet)");
                    } else {
                        resultHandler.handle(false, CreateUserWindow.this, "Entered ids do not match");
                    }
                } else {
                    CreateUserWindow.this.secondEnter = true;
                    firstEntered = enteredText;
                    caller.setMessage("Enter new user id (confirm)");
                    caller.clear();
                }

            }
        }, true, "Enter new user id");
        verticalLayout.addChild(numpad);
    }

    public interface CreateUserResultHandler {
        public void handle(boolean success, CreateUserWindow caller, String detailMessage);
    }
}
