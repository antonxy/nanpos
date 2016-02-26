package de.unikarlsruhe.nan.pos;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TerminalTextUtils;
import com.googlecode.lanterna.gui2.Border;
import com.googlecode.lanterna.gui2.Borders;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.TextGUIGraphics;

/**
 * @author Anton Schirg
 */
public class CoolButton extends Button {
    public CoolButton(String label, Runnable action) {
        super(label, action);
    }

    @Override
    protected ButtonRenderer createDefaultRenderer() {
        return new BigButtonRenderer();
    }

    public static class BigButtonRenderer implements ButtonRenderer {
        @Override
        public TerminalPosition getCursorLocation(Button component) {
            return null;
        }

        @Override
        public TerminalSize getPreferredSize(Button component) {
            return new TerminalSize(TerminalTextUtils.getColumnWidth(component.getLabel()) + 4, 5);
        }

        @Override
        public void drawComponent(TextGUIGraphics graphics, Button button) {
            if(button.isFocused()) {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getActive());
            }
            else {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getInsensitive());
            }
            graphics.fill(' ');
            if(button.isFocused()) {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getSelected());
            }
            else {
                graphics.applyThemeStyle(getThemeDefinition(graphics).getNormal());
            }
//            Border border = Borders.singleLine();
//            border.setComponent(button);
//            border.draw(graphics);
            graphics.putString(2, 2, button.getLabel());
        }
    }
}
