package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;

import de.unikarlsruhe.nan.pos.PS2BarcodeScanner;

/**
 * @author Janis Streib
 */
public class ScanEanWindow extends Window {

	private CenterLayout centerLayout;

	public ScanEanWindow() {
		centerLayout = new CenterLayout();
		setCentralComponent(centerLayout);
	}

	public void setBarCodeListener(
			final PS2BarcodeScanner.BarcodeListener listener) {
		VerticalLayout verticalLayout = new VerticalLayout();
		centerLayout.addChild(verticalLayout);

		verticalLayout.addChild(new Label("Scan EAN..."));
		verticalLayout.addChild(new Button("Cancel", new Runnable() {
			@Override
			public void run() {
				listener.barcodeScanned(-1);
				PS2BarcodeScanner.getInstance().removeBarcodeListener();
			}
		}));

		PS2BarcodeScanner.getInstance().setBarcodeListener(
				new PS2BarcodeScanner.BarcodeListener() {
					@Override
					public void barcodeScanned(long barcode) {
						if (ScanEanWindow.this.isVisible()) {
							listener.barcodeScanned(barcode);
						}
					}
				});
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
