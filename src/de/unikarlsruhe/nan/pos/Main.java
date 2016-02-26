package de.unikarlsruhe.nan.pos;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
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

        TextGraphics tGraphics = screen.newTextGraphics();

        screen.startScreen();
        screen.clear();

        tGraphics.drawRectangle(
                new TerminalPosition(3,3), new TerminalSize(10,10), '*');
        screen.refresh();

        screen.readInput();
        screen.stopScreen();
    }
}
