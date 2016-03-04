package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;

import java.io.IOException;

/**
 * @author Anton Schirg
 */
public class Label extends Component {
    private String text;

    public Label(String text) {
        this.text = text;
    }

    @Override
    void redraw() {
        super.redraw();
        TextGraphics textGraphics = getScreen().newTextGraphics();
        String drawText = text;
        if (text.length() > position.getSize().getColumns()) {
            drawText = text.substring(drawText.length() - position.getSize().getColumns(), text.length());
        }
        int col = Math.max((position.getSize().getColumns() - drawText.length()) / 2, 0);
        textGraphics.putString(position.getPosition().withRelative(new TerminalPosition(col, 0)), drawText);
        try {
            getScreen().refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    TerminalSize getPreferredSize() {
        return new TerminalSize(text.length(), 1);
    }

    public void setText(String text) {
        this.text = text;
        redraw();
    }
}
