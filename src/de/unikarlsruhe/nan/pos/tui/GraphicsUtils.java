package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.graphics.TextGraphics;

public class GraphicsUtils {
	public static void drawFancyBox(TextGraphics graphics,
			TerminalRectangle position, char up, char low, char left,
			char right, char upLeft, char upRight, char lowLeft, char lowRight) {
		// Upper
		graphics.drawLine(position.getPosition().getColumn(), position
				.getPosition().getRow(), position.getPosition().getColumn()
				+ position.getSize().getColumns(), position.getPosition()
				.getRow(), new TextCharacter(up));
		// lower
		graphics.drawLine(position.getPosition().getColumn(), position
				.getPosition().getRow() + position.getSize().getRows(),
				position.getPosition().getColumn()
						+ position.getSize().getColumns(), position
						.getPosition().getRow() + position.getSize().getRows(),
				new TextCharacter(low));

		// left
		graphics.drawLine(position.getPosition().getColumn(), position
				.getPosition().getRow(), position.getPosition().getColumn(),
				position.getPosition().getRow() + position.getSize().getRows(),
				new TextCharacter(left));
		// right
		graphics.drawLine(position.getPosition().getColumn()
				+ position.getSize().getColumns(), position.getPosition()
				.getRow(), position.getPosition().getColumn()
				+ position.getSize().getColumns(), position.getPosition()
				.getRow() + position.getSize().getRows(), new TextCharacter(
				right));

		// corner left up
		graphics.setCharacter(position.getPosition(), upLeft);

		// corner left low
		graphics.setCharacter(position.getPosition().getColumn(), position
				.getPosition().getRow() + position.getSize().getRows(), lowLeft);
		// corner right up
		graphics.setCharacter(position.getPosition().getColumn()
				+ position.getSize().getColumns(), position.getPosition()
				.getRow(), upRight);
		// corner right low
		graphics.setCharacter(position.getPosition().getColumn()
				+ position.getSize().getColumns(), position.getPosition()
				.getRow() + position.getSize().getRows(), lowRight);
	}

	public static void drawFancyBoxSingle(TextGraphics graphics,
			TerminalRectangle position) {
		drawFancyBox(graphics, position, '\u2500', '\u2500', '\u2502',
				'\u2502', '\u250C', '\u2510', '\u2514', '\u2518');
	}

	public static void drawFancyBoxDouble(TextGraphics graphics,
			TerminalRectangle position) {
		drawFancyBox(graphics, position, '\u2550', '\u2550', '\u2551',
				'\u2551', '\u2554', '\u2557', '\u255A', '\u255D');
	}
}
