package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.ClickListener;
import de.unikarlsruhe.nan.pos.CardReader;

import java.util.LinkedList;

public class TUI extends Component implements ClickListener {
    private Screen screen;

    private LinkedList<Window> windowStack = new LinkedList<>();

    public TUI(Screen screen) {
        this.screen = screen;
    }

    public void openWindow(Window child) {
        child.setParent(this);
        child.setTui(this);
        windowStack.add(child);
        layout();
        redraw();
    }

    public void closeWindow(Window window) {
        int i = windowStack.indexOf(window);
        if (i != -1) {
            if (i == 0) {
                System.exit(0); //TODO
            } else {
                while (windowStack.size() > i) {
                    windowStack.removeLast();
                }
            }
        }
        layout();
        redraw();
    }

    public Window getTopWindow() {
        return windowStack.getLast();
    }

    public void layout() {
        layout(new TerminalRectangle(TerminalPosition.TOP_LEFT_CORNER, screen.getTerminalSize()));
    }

    @Override
    protected void layout(TerminalRectangle position) {
        windowStack.getLast().layout(position);
    }

    @Override
    public void redraw() {
        windowStack.getLast().redraw();
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
        windowStack.getLast().onClick(position);
    }

    @Override
    public void clicked(TerminalPosition position) {
        onClick(position);
    }
}
