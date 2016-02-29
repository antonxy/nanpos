package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.MouseAction;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.MouseCaptureMode;
import com.googlecode.lanterna.terminal.ResizeListener;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.ansi.ANSITerminal;

import java.io.IOException;

/**
 * @author Anton Schirg
 */
public class Main {

    public static void main(String[] args) throws IOException {
        final Terminal terminal = new DefaultTerminalFactory().createTerminal();

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

        Thread mouseThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (terminal instanceof ANSITerminal) {
                    try {
                        ((ANSITerminal) terminal).setMouseCaptureMode(MouseCaptureMode.CLICK);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                while (true) {
                    try {
                        KeyStroke keyStroke = terminal.readInput();
                        if (keyStroke.getKeyType() == KeyType.MouseEvent) {
                            TerminalPosition position = ((MouseAction) keyStroke).getPosition();
                            tui.clicked(position);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mouseThread.start();

        terminal.addClickListener(tui);

        Button button = new Button("Hello World", new Runnable() {
            @Override
            public void run() {
                System.err.println("Clicked");
                System.exit(123);
            }
        });
        tui.addChild(button);

//        tui.onClick(new TerminalPosition(5, 5));

        screen.readInput();

        screen.stopScreen();
    }
}
