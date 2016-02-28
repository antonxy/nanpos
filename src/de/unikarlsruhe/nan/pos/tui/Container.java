package de.unikarlsruhe.nan.pos.tui;

/**
 * @author Anton Schirg
 */
public abstract class Container extends Component {

    public void addChild(Component child) {
        child.setParent(this);
    }

}
