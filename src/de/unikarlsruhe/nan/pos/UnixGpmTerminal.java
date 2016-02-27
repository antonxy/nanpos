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
	LinkedBlockingQueue<KeyStroke> lastKeyEvent = new LinkedBlockingQueue<KeyStroke>(
			10);
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
							synchronized (UnixGpmTerminal.this) {
								try {
									if (type == 20) {
										lastKeyEvent.put(new MouseAction(
												MouseActionType.CLICK_DOWN, 1,
												new TerminalPosition(x, y)));
									} else if (type == 24) {
										lastKeyEvent.put(new MouseAction(
												MouseActionType.CLICK_RELEASE,
												1, new TerminalPosition(x, y)));
									}
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					});
				} catch (GPMException e) {
					e.printStackTrace();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public KeyStroke pollInput() throws IOException {
		synchronized (this) {
			if (!lastKeyEvent.isEmpty()) {
				return lastKeyEvent.poll();
			}
		}

		return super.pollInput();
	}

	public void stopGpm() {
		GPM.stop();
	}
}
