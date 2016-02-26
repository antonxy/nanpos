package de.unikarlsruhe.nan.pos;

import com.googlecode.lanterna.gui2.*;

/**
 * @author Anton Schirg
 */
public class MainWindow extends AbstractWindow {
    public MainWindow() {
        super("NaN POS - Main Window");
        LinearLayout layout = new LinearLayout(Direction.VERTICAL);
        Panel panel = new Panel();
        panel.setLayoutManager(layout);
        panel.addComponent(new CoolButton("EXIT", new Runnable() {
            @Override
            public void run() {
                MainWindow.this.close();
            }
        }));
        setComponent(panel);
    }
}
