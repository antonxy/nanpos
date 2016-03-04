package de.unikarlsruhe.nan.pos.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.unikarlsruhe.nan.pos.DatabaseConnection;

public class User {
	private int id;
	private double balance;

	public static User getUserByPIN(String pin) throws SQLException {
		PreparedStatement prep = DatabaseConnection.getInstance().prepare(
				"SELECT * FROM users WHERE pin=?");
		prep.setString(1, pin);
		try (ResultSet res = prep.executeQuery()) {
			res.first();
			return new User(res);
		}

	}

	public User(ResultSet resSet) throws SQLException {
		this.id = resSet.getInt("id");
		PreparedStatement prep = DatabaseConnection.getInstance().prepare(
				"SELECT sum(amount) as sum FROM revenues WHERE user=?");
		prep.setInt(2, id);
		try (ResultSet res = prep.executeQuery()) {
			res.first();
			this.balance = res.getInt("sum");
		}
	}

	public double getBalance() {
		return balance;
	}

	public int getId() {
		return id;
	}

	public synchronized boolean buy(Product p) throws SQLException {
		if (balance + 4 >= p.getPrice()) {
			PreparedStatement prep = DatabaseConnection
					.getInstance()
					.prepare(
							"INSERT INTO revenues ('user', 'amount', 'product') VALUES(?, ?, ?)");
			prep.setInt(1, id);
			prep.setDouble(2, p.getPrice());
			prep.setInt(3, p.getId());
			prep.execute();
			return true;
		}
		return false;
	}

}
