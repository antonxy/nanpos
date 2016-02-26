package de.unikarlsruhe.nan.pos;

import com.googlecode.lanterna.gui2.*;

/**
 * @author Anton Schirg
 */
public class MainWindow extends AbstractWindow {
    public MainWindow() {
        super("NAN POS - Main Window");
        GridLayout layout = new GridLayout(5);
        Panel panel = new Panel();
        panel.setLayoutManager(layout);
        for (int i = 0; i < 20; i++) {
            Border border = Borders.singleLine();
            border.setComponent(new CoolButton("Button " + i, new Runnable() {
                @Override
                public void run() {
                    MainWindow.this.close();
                }
            }));
            panel.addComponent(border);
        }
        setComponent(panel);
    }
}
