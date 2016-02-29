package de.unikarlsruhe.nan.pos;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.LinkedBlockingQueue;

import nl.marcelweb.gpm.GPM;
import nl.marcelweb.gpm.GPMEventListener;
import nl.marcelweb.gpm.GPMException;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.MouseAction;
import com.googlecode.lanterna.input.MouseActionType;
import com.googlecode.lanterna.terminal.ansi.UnixTerminal;

/**
 * @author Anton Schirg
 */
public class UnixGpmTerminal extends UnixTerminal {
	private final GPM instance;

	public UnixGpmTerminal(InputStream terminalInput,
			OutputStream terminalOutput, Charset terminalCharset)
			throws IOException {
		super(terminalInput, terminalOutput, terminalCharset);
		instance = GPM.INSTANCE;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1500);
					GPM.listen(new GPMEventListener() {
						@Override
						public void eventReceived(char x, char y, byte buttons,
								byte mod, byte type) {
							if (type == 20) {
								sendClick(new TerminalPosition(x, y));
							}
						}
					});
				} catch (GPMException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void stopGpm() {
		GPM.stop();
	}
}
