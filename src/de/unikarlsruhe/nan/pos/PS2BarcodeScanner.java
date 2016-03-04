package de.unikarlsruhe.nan.pos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PS2BarcodeScanner {
	private File source;
	private BarcodeListener listener;

	public PS2BarcodeScanner(String sourceDevice) throws IOException {
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
}
