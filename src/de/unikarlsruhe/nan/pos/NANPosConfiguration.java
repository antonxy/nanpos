package de.unikarlsruhe.nan.pos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class NANPosConfiguration {
	private Properties p;
	private static NANPosConfiguration instance;

	protected NANPosConfiguration(InputStream in) throws IOException,
			SQLException {
		this.p = new Properties();
		p.load(in);
		instance = this;
	}

	protected String getDB() {
		return p.getProperty("pos.db");
	}

	protected String getDBUser() {
		return p.getProperty("pos.db.user");
	}

	protected String getDBPW() {
		return p.getProperty("pos.db.pw");
	}

	protected String getJDBCDriver() {
		return p.getProperty("pos.db.driver");
	}

	public String barcodeScannerSource() {
		return p.getProperty("pos.barcodescanner.source");
	}

	public static NANPosConfiguration getInstance() {
		return instance;
	}

	private void store() {
		File f = new File("conf/");
		if (!f.exists()) {
			f.mkdir();
		}
		f = new File("conf/nanpos.properties");
		try {
			p.store(new FileOutputStream(f), "");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
