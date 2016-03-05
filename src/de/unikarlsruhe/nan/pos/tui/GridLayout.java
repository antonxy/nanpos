package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;

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
            int posY = row * rowHeight + row;
            int posX = col * colWidth + col * 2;
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
        int rows = children.size();
        return new TerminalSize(columns * colWidth + columns, ((int) Math.ceil(((double) rows) / columns)) * rowHeight + rows);
    }
}
