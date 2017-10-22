package de.unikarlsruhe.nan.pos.tui;

import java.sql.SQLException;

import de.unikarlsruhe.nan.pos.PS2BarcodeScanner;
import de.unikarlsruhe.nan.pos.objects.Product;
import de.unikarlsruhe.nan.pos.objects.User;
import de.unikarlsruhe.nan.pos.tui.Numpad.NumpadResultHandler;

/**
 * @author Janis Streib
 */
public class CreateProductWindow extends Window {

	public CreateProductWindow(final CreateProductResultHandler resultHandler,
			final User operator) {
		CenterLayout centerLayout = new CenterLayout();
		setCentralComponent(centerLayout);
		VerticalLayout verticalLayout = new VerticalLayout();
		centerLayout.addChild(verticalLayout);

		final Keyboard keyboard = new Keyboard(
				new Keyboard.KeyboardResultHandler() {
					@Override
					public void handle(final String enteredText, Keyboard caller) {
						final String name = enteredText;
						if (name != null && name.trim().isEmpty()) {
							return;
						}
						if (enteredText != null) {
							caller.clear();
							final ScanEanWindow scanEanWindow = new ScanEanWindow();
							scanEanWindow
									.setBarCodeListener(new PS2BarcodeScanner.BarcodeListener() {
										@Override
										public void barcodeScanned(long barCode) {
											final long ean = barCode;
											if (ean == -1) {
												scanEanWindow.close();
											}
											final Window numPadWin = new Window();
											CenterLayout cent = new CenterLayout();
											cent.addChild(new Numpad(
													new NumpadResultHandler() {

														@Override
														public void handle(
																String enteredText,
																Numpad caller) {
															if (enteredText
																	.isEmpty()) {
																numPadWin
																		.close();
																return;
															}
															int val = Integer
																	.parseInt(enteredText);
															try {
																Product.create(
																		name,
																		ean,
																		val);
																resultHandler
																		.handle(true,
																				CreateProductWindow.this,
																				"Product created");
															} catch (SQLException e) {
																e.printStackTrace();
																resultHandler
																		.handle(false,
																				CreateProductWindow.this,
																				"SQL Exception");
																e.printStackTrace();
															}

														}
													}, false,
													"Price (in cents)"));
											numPadWin.setCentralComponent(cent);
											getTui().openWindow(numPadWin);
										}
									});
							getTui().openWindow(scanEanWindow);
						} else {
							resultHandler.handle(false,
									CreateProductWindow.this, "Canceled");
						}
					}
				}, "Enter new product name");
		verticalLayout.addChild(keyboard);
	}

	public interface CreateProductResultHandler {
		public void handle(boolean success, CreateProductWindow caller,
				String detailMessage);
	}
}
