package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.ClickListener;

public class TUI extends Component implements ClickListener {
    private Screen screen;
    private Component window;

    public TUI(Screen screen) {
        this.screen = screen;
    }

    public void setWindow(Component child) {
        child.setParent(this);
        window = child;
        layout();
        redraw();
    }

    public void layout() {
        layout(new TerminalRectangle(TerminalPosition.TOP_LEFT_CORNER, screen.getTerminalSize()));
    }

    @Override
    protected void layout(TerminalRectangle position) {
        window.layout(position);
    }

    @Override
    public void redraw() {
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

    @Override
    protected void onClick(TerminalPosition position) {
        window.onClick(position);
    }

    @Override
    public void clicked(TerminalPosition position) {
        onClick(position);
    }
}
