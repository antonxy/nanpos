package de.unikarlsruhe.nan.pos;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.MouseAction;
import com.googlecode.lanterna.input.MouseActionType;
import com.googlecode.lanterna.terminal.ansi.UnixTerminal;
import com.googlecode.lanterna.terminal.ansi.UnixTerminalSizeQuerier;
import nl.marcelweb.gpm.GPM;
import nl.marcelweb.gpm.GPMEventListener;
import nl.marcelweb.gpm.GPMException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * @author Anton Schirg
 */
public class UnixGpmTerminal extends UnixTerminal {
    KeyStroke lastKeyEvent = null;
    private final GPM instance;

    public UnixGpmTerminal(InputStream terminalInput, OutputStream terminalOutput, Charset terminalCharset) throws IOException {
        super(terminalInput, terminalOutput, terminalCharset);
        instance = GPM.INSTANCE;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GPM.listen(new GPMEventListener() {
                        @Override
                        public void eventReceived(char x, char y, byte buttons, byte mod, byte type) {
                            synchronized (UnixGpmTerminal.this) {
                                if (type == 20) {
                                    lastKeyEvent = new MouseAction(MouseActionType.CLICK_DOWN, 1, new TerminalPosition(x, y));
                                } else if (type == 24) {
                                    lastKeyEvent = new MouseAction(MouseActionType.CLICK_RELEASE, 1, new TerminalPosition(x, y));
                                }
                            }
                        }
                    });
                } catch (GPMException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public KeyStroke pollInput() throws IOException {
        synchronized (this) {
            if (lastKeyEvent != null) {
                KeyStroke event = lastKeyEvent;
                lastKeyEvent = null;
                return event;
            }
        }

        return super.pollInput();
    }

    public void stopGpm() {
        GPM.stop();
    }
}
