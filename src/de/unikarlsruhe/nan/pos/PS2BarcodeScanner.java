package de.unikarlsruhe.nan.pos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PS2BarcodeScanner {
	private PS2BarcodeScanner instance = new PS2BarcodeScanner(
			NANPosConfiguration.getInstance().barcodeScannerSource());
	private File source;
	private BarcodeListener listener = null;

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

	public static interface BarcodeListener {
		public void barcodeScanned(int barCode);
	}

	public PS2BarcodeScanner getInstance() {
		return instance;
	}
}
