package de.unikarlsruhe.nan.pos;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.ExtendedTerminal;
import com.googlecode.lanterna.terminal.MouseCaptureMode;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

/**
 * @author Anton Schirg
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        System.err.println(terminal.getClass().getName());
        if (terminal instanceof ExtendedTerminal) {
            ((ExtendedTerminal) terminal).setMouseCaptureMode(MouseCaptureMode.CLICK_RELEASE);
        } else {
            System.exit(99);
//            throw new RuntimeException("Terminal does not support mouse input");
        }
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);

        MainWindow window = new MainWindow();

        gui.addWindow(window);
        gui.setActiveWindow(window);

        gui.waitForWindowToClose(window);

        if (terminal instanceof UnixGpmTerminal) {
            ((UnixGpmTerminal) terminal).stopGpm();
        }

//        screen.stopScreen();
    }
}
