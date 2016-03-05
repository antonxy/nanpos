package de.unikarlsruhe.nan.pos.tui;

import java.io.IOException;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;

/**
 * @author Anton Schirg
 */
public class Button extends Component {

	private String text;
	private Runnable action;

	public Button(String text) {
		this(text, new Runnable() {
			@Override
			public void run() {

			}
		});
	}

	public Button(String text, Runnable action) {
		this.text = text;
		this.action = action;
	}

	@Override
	void redraw() {
		super.redraw();
		TextGraphics graphics = getScreen().newTextGraphics();
		GraphicsUtils.drawFancyBoxSingle(graphics, position);
        String[] split = text.split("\n");
        for (int i = 0; i < split.length; i++) {
            String currentText = split[i].trim();
            graphics.putString(
                    position.getPosition()
                            .withRelative(
                                    position.getSize().getColumns() / 2
                                            - currentText.length() / 2,
                                    (position.getSize().getRows() - split.length + 1) / 2 + i), currentText);
        }
		try {
			getScreen().refresh();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(TerminalPosition position) {
		action.run();
	}

	@Override
	TerminalSize getPreferredSize() {
        String[] split = text.split("\n");
        int maxlen = 0;
        for (String s : split) {
            maxlen = Math.max(maxlen, s.trim().length());
        }
        return new TerminalSize(maxlen + 10, split.length + 5);
	}
}
