package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Anton Schirg
 */
public class CenterLayout extends Container {
    Component child;

    @Override
    public void addChild(Component child) {
        super.addChild(child);
        this.child = child;
    }

    @Override
    public List<Component> getChildren() {
        LinkedList<Component> children = new LinkedList<>();
        children.add(child);
        return children;
    }

    @Override
    protected void layout(TerminalRectangle position) {
        super.layout(position);
        if (child != null) {
            TerminalSize childPreferredSize = child.getPreferredSize();
            TerminalSize childSize = new TerminalSize(Math.min(childPreferredSize.getColumns(), position.getSize().getColumns()),
                    Math.min(childPreferredSize.getRows(), position.getSize().getRows()));
            TerminalPosition childPosition = position.getPosition().withRelative(new TerminalPosition(
                    (position.getSize().getColumns() - childSize.getColumns()) / 2,
                    (position.getSize().getRows() - childSize.getRows()) / 2));
            child.layout(new TerminalRectangle(childPosition, childSize));
        }
    }

    @Override
    TerminalSize getPreferredSize() {
        return new TerminalSize(9999, 9999);
    }
}
