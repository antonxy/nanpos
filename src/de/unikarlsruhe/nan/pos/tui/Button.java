package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;

import java.io.IOException;

/**
 * @author Anton Schirg
 */
public class Button extends Component {

    private String text;
    private Runnable action;

    public Button(String text) {
        this(text, new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    public Button(String text, Runnable action) {
        this.text = text;
        this.action = action;
    }

    @Override
    void redraw() {
        TextGraphics graphics = getScreen().newTextGraphics();
        graphics.drawRectangle(position.getPosition(), position.getSize(), '*');
        graphics.putString(position.getPosition().withRelative(position.getSize().getColumns() / 2 - text.length() / 2, position.getSize().getRows() / 2), text);
        try {
            getScreen().refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(TerminalPosition position) {
        action.run();
    }

    @Override
    TerminalSize getPreferredSize() {
        return null;
    }
}
