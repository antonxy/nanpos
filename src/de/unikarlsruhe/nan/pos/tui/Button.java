package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;

import java.io.IOException;

/**
 * @author Anton Schirg
 */
public class Button extends Component {

    private String text;

    public Button(String text) {
        this.text = text;
    }

    @Override
    void redraw() {
        TextGraphics graphics = getScreen().newTextGraphics();
        graphics.drawRectangle(position, size, '*');
        graphics.putString(position.withRelative(size.getColumns() / 2 - text.length() / 2, size.getRows() / 2), text);
        try {
            getScreen().refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    TerminalSize getPreferredSize() {
        return null;
    }
}
