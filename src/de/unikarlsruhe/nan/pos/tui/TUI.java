package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.ClickListener;

import java.util.LinkedList;
import java.util.List;

public class TUI extends Container implements ClickListener {
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
    }

    @Override
    public List<Component> getChildren() {
        LinkedList<Component> children = new LinkedList<>();
        children.add(window);
        return children;
    }

    void layout() {
        layout(new TerminalRectangle(TerminalPosition.TOP_LEFT_CORNER, screen.getTerminalSize()));
    }

    @Override
    protected void layout(TerminalRectangle position) {
        window.layout(position);
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

    @Override
    public void clicked(TerminalPosition position) {
        onClick(position);
    }
}
