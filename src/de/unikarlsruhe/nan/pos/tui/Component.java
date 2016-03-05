package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

/**
 * @author Anton Schirg
 */
public abstract class Component {
    protected TerminalRectangle position;
    protected Component parent;
    private TextColor backgroundColor = TextColor.ANSI.DEFAULT;

    protected void layout(TerminalRectangle position) {
        this.position = position;
    }

    public void setBackgroundColor(TextColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    void redraw() {
        TextGraphics textGraphics = getScreen().newTextGraphics();
        textGraphics.fillRectangle(position.getPosition(), position.getSize(), new TextCharacter(' ', TextColor.ANSI.DEFAULT, backgroundColor));
    }
    abstract TerminalSize getPreferredSize();

    protected Screen getScreen() {
        return parent.getScreen();
    }

    protected void setParent(Component parent) {
        this.parent = parent;
    }

    protected void onClick(TerminalPosition position) {

    }

    public TerminalRectangle getPosition() {
        return position;
    }
}
