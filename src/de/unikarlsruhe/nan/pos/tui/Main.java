package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.ResizeListener;
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

        final TUI tui = new TUI(screen);
        terminal.addResizeListener(new ResizeListener() {
            @Override
            public void onResized(Terminal terminal, TerminalSize newSize) {
                tui.layout();
                tui.redraw();
            }
        });

        terminal.addClickListener(tui);

        Button button = new Button("Hello World", new Runnable() {
            @Override
            public void run() {
                System.err.println("Clicked");
            }
        });
        tui.addChild(button);

        tui.onClick(new TerminalPosition(5, 5));

        screen.readInput();

        screen.stopScreen();
    }
}
