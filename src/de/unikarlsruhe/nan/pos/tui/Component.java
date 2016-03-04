package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

/**
 * @author Anton Schirg
 */
public abstract class Component {
    protected TerminalRectangle position;
    protected Component parent;

    protected void layout(TerminalRectangle position) {
        this.position = position;
    }

    void redraw() {
        TextGraphics textGraphics = getScreen().newTextGraphics();
        textGraphics.fillRectangle(position.getPosition(), position.getSize(), ' ');
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
