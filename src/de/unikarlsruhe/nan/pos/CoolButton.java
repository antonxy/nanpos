package de.unikarlsruhe.nan.pos;

import com.googlecode.lanterna.gui2.Button;

/**
 * @author Anton Schirg
 */
public class CoolButton extends Button {
    public CoolButton(String label, Runnable action) {
        super(label, action);
    }
}
