package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Anton Schirg
 */
public class VerticalLayout extends Container {
    LinkedList<Component> children = new LinkedList<>();

    @Override
    public void addChild(Component child) {
        super.addChild(child);
        children.add(child);
    }

    @Override
    protected void layout(TerminalRectangle position) {
        super.layout(position);
        int rowOffset = 0;
        int colWidth = getPreferredSize().getColumns();

        for (Component child : children) {
            TerminalSize childPreferredSize = child.getPreferredSize();
            child.layout(new TerminalRectangle(position.getPosition().withRelative(new TerminalPosition(0, rowOffset)), new TerminalSize(colWidth, childPreferredSize.getRows())));
            rowOffset += childPreferredSize.getRows() + 1;
        }
    }

    @Override
    public List<Component> getChildren() {
        return children;
    }

    @Override
    TerminalSize getPreferredSize() {
        int cols = 0;
        int rows = 0;
        for (Component child : children) {
            TerminalSize preferredSize = child.getPreferredSize();
            if (preferredSize.getColumns() > cols) {
                cols = preferredSize.getColumns();
            }
            rows += preferredSize.getRows() + 1;
        }
        return new TerminalSize(cols, rows);
    }
}
