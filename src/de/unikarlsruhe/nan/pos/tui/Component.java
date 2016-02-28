package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.Screen;

/**
 * @author Anton Schirg
 */
public abstract class Component {
    protected TerminalPosition position;
    protected TerminalSize size;
    protected Container parent;

    protected void layout(TerminalPosition position, TerminalSize size) {
        this.position = position;
        this.size = size;
    }

    abstract void redraw();
    abstract TerminalSize getPreferredSize();

    public void setPosition(TerminalPosition position, TerminalSize size) {
        this.position = position;
        this.size = size;
    }

    protected Screen getScreen() {
        return parent.getScreen();
    }

    protected void setParent(Container parent) {
        this.parent = parent;
    }
}
