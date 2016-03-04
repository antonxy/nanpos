package de.unikarlsruhe.nan.pos;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.MouseAction;
import com.googlecode.lanterna.input.MouseActionType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.MouseCaptureMode;
import com.googlecode.lanterna.terminal.ResizeListener;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.ansi.ANSITerminal;
import de.unikarlsruhe.nan.pos.objects.User;
import de.unikarlsruhe.nan.pos.tui.CenterLayout;
import de.unikarlsruhe.nan.pos.tui.Numpad;
import de.unikarlsruhe.nan.pos.tui.TUI;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * @author Anton Schirg
 */
public class Main {

    public static void main(String[] args) throws IOException, SQLException {
        NANPosConfiguration nanConf = new NANPosConfiguration(
                new FileInputStream("conf/nanpos.properties"));
        DatabaseConnection.init(nanConf);
        final Terminal terminal = new DefaultTerminalFactory().createTerminal();

        final Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        screen.clear();

        final TUI tui = new TUI(screen);

        terminal.addClickListener(tui);

        CenterLayout loginLayout = new CenterLayout();

        Numpad numpad = new Numpad(new Numpad.NumpadResultHandler() {
            @Override
            public void handle(String result) {
                User userByPIN;
                try {
                    userByPIN = User.getUserByPIN(result);
                } catch (SQLException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return;
                }
                if (userByPIN != null) {
                    System.err.println("Logged in");
                    System.exit(123);
                }
            }
        }, true);
        loginLayout.addChild(numpad);
        tui.addChild(loginLayout);

        terminal.addResizeListener(new ResizeListener() {
            @Override
            public void onResized(Terminal terminal, TerminalSize newSize) {
                tui.layout();
                tui.redraw();
            }
        });
        Thread mouseThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (terminal instanceof ANSITerminal) {
                    try {
                        ((ANSITerminal) terminal).setMouseCaptureMode(MouseCaptureMode.CLICK_RELEASE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                while (true) {
                    try {
                        KeyStroke keyStroke = terminal.readInput();
                        if (keyStroke.getKeyType() == KeyType.MouseEvent) {
                            MouseAction mouseAction = (MouseAction) keyStroke;
                            if (mouseAction.getActionType() == MouseActionType.CLICK_DOWN) {
                                TerminalPosition position = mouseAction.getPosition();
                                tui.clicked(position);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mouseThread.start();
    }
}
