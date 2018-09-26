package de.unikarlsruhe.nan.pos.tui;

import java.util.LinkedList;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.ClickListener;

import de.unikarlsruhe.nan.pos.CardReader;

public class TUI extends Component implements ClickListener {
	private Screen screen;

	private long lastInputTime = System.currentTimeMillis();

	private LinkedList<Window> windowStack = new LinkedList<>();

	public TUI(Screen screen) {
		this.screen = screen;
	}

	public void openWindow(Window child) {
		System.err.println("Open window " + child.getClass());
		child.setParent(this);
		child.setTui(this);
		windowStack.add(child);
		child.onVisible();
		layout();
		redraw();
	}

	public void closeWindow(Window window) {
		System.err.println("Closed window " + window.getClass());
		CardReader.getInstance().disableListener();
		int i = windowStack.indexOf(window);
		if (i != -1) {
			if (i == 0) {
				System.exit(0); // TODO
			} else {
				while (windowStack.size() > i) {
					windowStack.removeLast();
				}
			}
		}
		windowStack.getLast().onVisible();
		layout();
		redraw();
	}

	public void closeWindowsAbove(Window window) {
		System.err.println("Closed windows above " + window.getClass());
		CardReader.getInstance().disableListener();
		int i = windowStack.indexOf(window);
		if (i != -1) {
			while (windowStack.size() > i + 1) {
				windowStack.removeLast();
			}
		}
		windowStack.getLast().onVisible();
		layout();
		redraw();
	}

	public Window getTopWindow() {
		return windowStack.getLast();
	}

	public void layout() {
		layout(new TerminalRectangle(TerminalPosition.TOP_LEFT_CORNER,
				screen.getTerminalSize()));
	}

	@Override
	protected void layout(TerminalRectangle position) {
		windowStack.getLast().layout(position);
	}

	@Override
	public void redraw() {
		windowStack.getLast().redraw();
		this.lastInputTime = System.currentTimeMillis();
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
		fireGenericInputEvent();
	}

	public void fireGenericInputEvent() {
		this.lastInputTime = System.currentTimeMillis();
	}

	public long getLastInputTime() {
		return lastInputTime;
	}

	@Override
	public void clicked(TerminalPosition position) {
		onClick(position);
	}
}
