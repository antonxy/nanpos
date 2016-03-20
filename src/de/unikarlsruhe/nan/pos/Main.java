package de.unikarlsruhe.nan.pos;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.input.MouseAction;
import com.googlecode.lanterna.input.MouseActionType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.MouseCaptureMode;
import com.googlecode.lanterna.terminal.ResizeListener;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.ansi.ANSITerminal;

import de.unikarlsruhe.nan.pos.objects.User;
import de.unikarlsruhe.nan.pos.tui.*;

/**
 * @author Anton Schirg
 */
public class Main {

	public static void main(String[] args) throws IOException, SQLException {
		NANPosConfiguration nanConf = new NANPosConfiguration(
				new FileInputStream("conf/nanpos.properties"));
		DatabaseConnection.init(nanConf);
		PS2BarcodeScanner.init(nanConf);

		final Terminal terminal = new DefaultTerminalFactory().createTerminal();
		terminal.setCursorVisible(false);
		final Screen screen = new TerminalScreen(terminal);
		screen.startScreen();
		screen.clear();

		final TUI tui = new TUI(screen);

		terminal.addClickListener(tui);

		final LoginWindow loginWindow = new LoginWindow(new LoginWindow.LoginResultHandler() {
			@Override
			public void handle(User user, LoginWindow caller, String detailMessage) {
				if (user != null) {
					final BuyWindow buyWindow = RecyclingBuyWindowFactoryBuilder.build().factorarte(user);
					buyWindow.setResultCallback(new BuyWindow.BuyWindowResultHandler() {
						@Override
						public void handle(String result, TextColor color) {
							if (result != null) {
								ResultScreen resultScreen = new ResultScreen(
										result, color);
								resultScreen.setDoneCallback(new Runnable() {
									@Override
									public void run() {
										buyWindow.close();
									}
								});
								tui.openWindow(resultScreen);
							} else {
								tui.closeWindow(buyWindow);
							}
						}
					});
					tui.openWindow(buyWindow);
				} else {
					final ResultScreen resultScreen = new ResultScreen(detailMessage,
							TextColor.ANSI.RED);
					resultScreen.setDoneCallback(new Runnable() {
						@Override
						public void run() {
							resultScreen.close();
						}
					});
					tui.openWindow(resultScreen);
				}
			}
		}, true, null);
		tui.openWindow(loginWindow);

		terminal.addResizeListener(new ResizeListener() {
			@Override
			public void onResized(Terminal terminal, TerminalSize newSize) {
				tui.layout();
				tui.redraw();
			}
		});
		Thread mouseThread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (terminal instanceof ANSITerminal) {
					try {
						((ANSITerminal) terminal)
								.setMouseCaptureMode(MouseCaptureMode.CLICK_RELEASE);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				while (true) {
					try {
						KeyStroke keyStroke = terminal.readInput();
						if (keyStroke.getKeyType() == KeyType.MouseEvent) {
							MouseAction mouseAction = (MouseAction) keyStroke;
							if (mouseAction.getActionType() == MouseActionType.CLICK_DOWN) {
								TerminalPosition position = mouseAction
										.getPosition();
								tui.clicked(position);
							}
						} else {
							PS2BarcodeScanner.getInstance().keyPressedEvent(
									keyStroke);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		mouseThread.start();
	}
}
