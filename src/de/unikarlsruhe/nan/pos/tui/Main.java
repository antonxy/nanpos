package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

/**
 * @author Anton Schirg
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();

        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        screen.clear();

        TUI tui = new TUI(screen);
        Button button = new Button("Hello World");
        tui.addChild(button);

        screen.readInput();

        screen.stopScreen();
    }
}
