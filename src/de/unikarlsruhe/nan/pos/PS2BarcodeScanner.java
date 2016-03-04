package de.unikarlsruhe.nan.pos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PS2BarcodeScanner {
	private static PS2BarcodeScanner instance = null;
	private File source;
	private BarcodeListener listener = null;

	public static void init(NANPosConfiguration configuration) {
		try {
			instance = new PS2BarcodeScanner(configuration.barcodeScannerSource());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private PS2BarcodeScanner(String sourceDevice) throws IOException {
		if (sourceDevice == null) {
			System.err.println("No barcode scanner configured.");
			return;
		}
		source = new File(sourceDevice);
		run();
	}

	private void run() throws IOException {
		try (final BufferedReader inRead = new BufferedReader(new FileReader(
				source))) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					while (true) {
						try {
							String tmp = inRead.readLine().trim();
							int scanned = Integer.parseInt(tmp);
							synchronized (PS2BarcodeScanner.this.listener) {
								if (listener != null) {
									listener.barcodeScanned(scanned);
								}
							}
						} catch (NumberFormatException | IOException e) {
							System.err.println("Ignored garbage from scanner.");
						}
					}
				}
			}, "Barcode scanner thread").start();
		}
	}

	public synchronized void setBarcodeListener(BarcodeListener list) {
		this.listener = list;
	}

	public synchronized void removeBarcodeListener() {
		this.listener = null;
	}

	public static interface BarcodeListener {
		public void barcodeScanned(int barCode);
	}

	static public PS2BarcodeScanner getInstance() {
		return instance;
	}
}
