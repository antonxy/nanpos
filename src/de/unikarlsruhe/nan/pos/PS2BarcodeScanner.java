package de.unikarlsruhe.nan.pos;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import java.io.*;

public class PS2BarcodeScanner {
	private static PS2BarcodeScanner instance = null;
	private File source;
	private BarcodeListener listener = null;
	private String buffer = "";

	public static void init(NANPosConfiguration configuration) {
		try {
			instance = new PS2BarcodeScanner(configuration.barcodeScannerSource());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private PS2BarcodeScanner(String sourceDevice) throws IOException {

	}

	public void keyPressedEvent(KeyStroke keyStroke) {
		if (keyStroke.getKeyType() == KeyType.Enter) {
			if (listener != null) {
				try {
					long scanned = Long.parseLong(buffer);
					listener.barcodeScanned(scanned);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
			buffer = "";
		} else {
			Character ch = keyStroke.getCharacter();
			if (ch != null && Character.isDigit(ch)) {
				buffer += ch;
			}
		}
	}

	public synchronized void setBarcodeListener(BarcodeListener list) {
		this.listener = list;
	}

	public synchronized void removeBarcodeListener() {
		this.listener = null;
	}

	public static interface BarcodeListener {
		public void barcodeScanned(long barCode);
	}

	static public PS2BarcodeScanner getInstance() {
		return instance;
	}
}
