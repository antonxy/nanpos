package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.BasicTextImage;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.graphics.TextImage;

/**
 * @author Anton Schirg
 */
public class AsciiArt extends Component {
    String[] asciiArt = (
            "                                   #####\n" +
            "                                   #####\n" +
            "                                   #####\n" +
            " #####    ######         ######    #####\n" +
            " ######   ######         #######   #####\n" +
            " #######  ######  #####  ########  #####\n" +
            " ######## ###### ####### ######### #####\n" +
            " ################### ###################\n" +
            " ##################   ##################\n" +
            " #######################################\n" +
            " ##### ##########       ################\n" +
            " #####  ########         ###### ########\n" +
            " #####   ######           #####  #######\n" +
            " #####    ####             ####   ######\n" +
            " #####    ### \n" +
            " #####   ### \n" +
            " #####  ###      N24 Bar\n" +
                    "\n" +
                    "\"At least better than Counter Solutions!\"").split("\n");

    @Override
    protected void layout(TerminalRectangle position) {
        super.layout(position);
    }

    @Override
    void redraw() {
        super.redraw();
        TextGraphics textGraphics = getScreen().newTextGraphics();
        textGraphics.setForegroundColor(TextColor.ANSI.YELLOW);
        for (int i = 0; i < position.getSize().getRows(); i++) {
            textGraphics.putString(position.getPosition().withRelative(new TerminalPosition(1, i)), asciiArt[i]);
        }
    }

    @Override
    TerminalSize getPreferredSize() {
        return new TerminalSize(asciiArt[0].length() + 1, asciiArt.length);
    }
}
