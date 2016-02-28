package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.Screen;

public class TUI extends Container {
    private Screen screen;
    private Component window;

    public TUI(Screen screen) {
        this.screen = screen;
    }

    @Override
    public void addChild(Component child) {
        super.addChild(child);
        window = child;
        layout();
        redraw();
        //Add redraw to queue instead of redrawing here
    }

    void layout() {
        layout(TerminalPosition.TOP_LEFT_CORNER, screen.getTerminalSize());
    }

    @Override
    protected void layout(TerminalPosition position, TerminalSize size) {
        window.layout(position, size);
    }

    @Override
    void redraw() {
        window.redraw();
    }

    @Override
    TerminalSize getPreferredSize() {
        return screen.getTerminalSize();
    }

    @Override
    protected Screen getScreen() {
        return screen;
    }
}
