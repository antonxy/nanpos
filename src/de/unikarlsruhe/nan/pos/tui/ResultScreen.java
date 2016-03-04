package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;

/**
 * @author Anton Schirg
 */
public class ResultScreen extends Component {

    private final CenterLayout centerLayout;
    private Runnable callback;

    public ResultScreen(String result) {
        centerLayout = new CenterLayout();
        centerLayout.setParent(this);
        Button label = new Button(result, new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.run();
                }
            }
        });
        centerLayout.addChild(label);
    }

    public void setDoneCallback(Runnable callback) {
        this.callback = callback;
    }

    @Override
    TerminalSize getPreferredSize() {
        return centerLayout.getPreferredSize();
    }

    @Override
    protected void layout(TerminalRectangle position) {
        centerLayout.layout(position);
    }

    @Override
    void redraw() {
        centerLayout.redraw();
    }

    @Override
    protected void onClick(TerminalPosition position) {
        centerLayout.onClick(position);
    }
}
