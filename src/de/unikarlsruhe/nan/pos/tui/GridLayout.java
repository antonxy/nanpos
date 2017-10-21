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
public class GridLayout extends Container {
    LinkedList<Component> children = new LinkedList<>();
    private int columns;
    private int colWidth;
    private int rowHeight;

    private final int colMargin = 1;
    private final int rowMargin = 0;

    public GridLayout(int columns) {
        this.columns = columns;
    }

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

        int i = 0;
        for (Component child : children) {
            int col = i % columns;
            int row = i / columns;
            int posY = row * rowHeight + row * (rowMargin + 1);
            int posX = col * colWidth + col * (colMargin + 1);
            child.layout(new TerminalRectangle(position.getPosition().withRelative(new TerminalPosition(posX, posY)), new TerminalSize(colWidth, rowHeight)));
            i++;
        }
    }

    @Override
    TerminalSize getPreferredSize() {
        colWidth = 0;
        rowHeight = 0;
        for (Component child : children) {
            TerminalSize preferredSize = child.getPreferredSize();
            if (preferredSize.getColumns() > colWidth) {
                colWidth = preferredSize.getColumns();
            }
            if (preferredSize.getRows() > rowHeight) {
                rowHeight = preferredSize.getRows();
            }
        }
        int rows = (int) Math.ceil(((double) children.size()) / columns);
        return new TerminalSize(columns * colWidth + (columns - 1) * (colMargin + 1), rows * rowHeight + Math.max(0, (rows - 1) * (rowMargin + 1)));
    }
}
