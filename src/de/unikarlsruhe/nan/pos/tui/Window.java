package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;

/**
 * @author Anton Schirg
 */
public class Window extends Component {
    private Component centralComponent;

    private TUI tui;

    protected void setCentralComponent(Component centralComponent) {
        centralComponent.setParent(this);
        this.centralComponent = centralComponent;
    }

    protected void setTui(TUI tui){
        this.tui = tui;
    }

    public TUI getTui() {
        return tui;
    }

    public void close() {
        tui.closeWindow(this);
    }

    @Override
    protected void layout(TerminalRectangle position) {
        super.layout(position);
        centralComponent.layout(position);
    }

    @Override
    void redraw() {
        super.redraw();
        centralComponent.redraw();
    }

    @Override
    protected void onClick(TerminalPosition position) {
        centralComponent.onClick(position);
    }

    @Override
    TerminalSize getPreferredSize() {
        return centralComponent.getPreferredSize();
    }
}
