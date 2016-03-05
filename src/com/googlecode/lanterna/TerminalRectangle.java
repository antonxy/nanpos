package com.googlecode.lanterna;

/**
 * @author Anton Schirg
 */
public class TerminalRectangle {
    private TerminalPosition position;
    private TerminalSize size;

    public TerminalRectangle(TerminalPosition position, TerminalSize size) {
        this.position = position;
        this.size = size;
    }

    public TerminalPosition getPosition() {
        return position;
    }

    public TerminalSize getSize() {
        return size;
    }

    public boolean isInside(TerminalPosition position) {
        return position.getRow() >= getPosition().getRow() &&
                position.getColumn() >= getPosition().getColumn() &&
                position.getRow() <= getPosition().getRow() + getSize().getRows() &&
                position.getColumn() <= getPosition().getColumn() + getSize().getColumns();
    }
}
