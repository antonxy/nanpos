package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Anton Schirg
 */
public class HorizontalLayout extends Container {
    LinkedList<Component> children = new LinkedList<>();
    private final int colMargin = 1;

    @Override
    public void addChild(Component child) {
        super.addChild(child);
        children.add(child);
    }

    @Override
    public List<Component> getChildren() {
        return children;
    }

    @Override
    protected void layout(TerminalRectangle position) {
        super.layout(position);
        TerminalSize preferredSize = getPreferredSize();
        TerminalRectangle centeredRect = new TerminalRectangle(
                position.getPosition().withRelative(new TerminalPosition(
                        position.getSize().getColumns() / 2 - preferredSize.getColumns() / 2,
                        0)),
                new TerminalSize(preferredSize.getColumns(), position.getSize().getRows()));

        int x = 0;
        for (Component child : children) {
            TerminalSize childPreferredSize = child.getPreferredSize();
            child.layout(new TerminalRectangle(
                    centeredRect.getPosition().withRelative(new TerminalPosition(x, 0)),
                    new TerminalSize(childPreferredSize.getColumns(), centeredRect.getSize().getRows())
            ));
            x += childPreferredSize.getColumns() + 1;
        }

    }

    @Override
    void redraw() {
        super.redraw();
    }

    @Override
    TerminalSize getPreferredSize() {
        int maxHeight = 0;
        int width = 0;
        for (Component child : children) {
            maxHeight = Math.max(maxHeight, child.getPreferredSize().getRows());
            width += child.getPreferredSize().getColumns();
        }
        width += (children.size() - 1) * (colMargin + 1);
        return new TerminalSize(width, maxHeight);
    }
}
