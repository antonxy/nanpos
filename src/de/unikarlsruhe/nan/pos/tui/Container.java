package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;

import java.util.List;

/**
 * @author Anton Schirg
 */
public abstract class Container extends Component {

    public void addChild(Component child) {
        child.setParent(this);
    }

    public abstract List<Component> getChildren();

    @Override
    protected void onClick(TerminalPosition position) {
        for (Component child : getChildren()) {
            if (child.getPosition().isInside(position)) {
                child.onClick(position);
            }
        }
    }
}
