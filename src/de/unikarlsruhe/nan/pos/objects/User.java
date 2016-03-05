package de.unikarlsruhe.nan.pos.objects;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.unikarlsruhe.nan.pos.DatabaseConnection;

public class User {
	private int id;
	private int balance;

	public static User getUserByPIN(String pin) throws SQLException,
			NoSuchAlgorithmException {
		PreparedStatement prep = DatabaseConnection.getInstance().prepare(
				"SELECT * FROM users WHERE pin=?");
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
		messageDigest.update(pin.getBytes());
		prep.setString(1, byteArrayToHexString(messageDigest.digest()));
		try (ResultSet res = prep.executeQuery()) {
			if (res.next()) {
				return new User(res);
			}
		}
		return null;

	}

	public User(ResultSet resSet) throws SQLException {
		this.id = resSet.getInt("id");
		PreparedStatement prep = DatabaseConnection.getInstance().prepare(
				"SELECT sum(amount) as sum FROM revenues WHERE \"user\"=?");
		prep.setInt(1, id);
		try (ResultSet res = prep.executeQuery()) {
			res.next();
			this.balance = res.getInt("sum");
		}
	}

	public int getBalance() {
		return balance;
	}

	public int getId() {
		return id;
	}

	public boolean isAdmin() {
		return true;
	}

	public synchronized boolean buy(Product p) throws SQLException {
		if (balance + 400 >= p.getPrice()) {
			PreparedStatement prep = DatabaseConnection
					.getInstance()
					.prepare(
							"INSERT INTO revenues (\"user\", amount, product) VALUES(?, ?, ?)");
			prep.setInt(1, id);
			prep.setInt(2, -1 * p.getPrice());
			prep.setInt(3, p.getId());
			prep.execute();
			return true;
		}
		return false;
	}

	private static String byteArrayToHexString(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

}
