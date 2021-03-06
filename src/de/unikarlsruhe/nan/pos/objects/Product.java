package de.unikarlsruhe.nan.pos.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

import de.unikarlsruhe.nan.pos.DatabaseConnection;

public class Product {
	private String name;
	private int price;
	private int id;
	private long ean;

	public static List<Product> getAllProducts() throws SQLException {
		LinkedList<Product> res = new LinkedList<Product>();
		PreparedStatement prep = DatabaseConnection.getInstance().prepare(
				"SELECT * FROM products WHERE visible=TRUE ORDER BY name ASC");
		try (ResultSet resSet = prep.executeQuery()) {
			while (resSet.next()) {
				res.add(new Product(resSet));
			}
		}
		return res;
	}

	public static Product getByEAN(long ean) throws SQLException {
		PreparedStatement prep = DatabaseConnection.getInstance().prepare(
				"SELECT * FROM products WHERE ean=? and visible=TRUE");
		prep.setLong(1, ean);
		try (ResultSet res = prep.executeQuery()) {
			if (res.next()) {
				return new Product(res);
			}
		}
		return null;
	}

	public Product(ResultSet resSet) throws SQLException {
		this.name = resSet.getString("name");
		this.price = resSet.getInt("price");
		this.id = resSet.getInt("id");
		this.ean = resSet.getLong("ean");
	}

	public String getName() {
		return name;
	}

	public int getPrice() {
		return price;
	}

	public int getId() {
		return id;
	}

	public long getEan() {
		return ean;
	}

	public static void create(String name, long ean, int price)
			throws SQLException {
		PreparedStatement prep = DatabaseConnection.getInstance().prepare(
				"INSERT INTO products (name, ean, price) VALUES (?, ?, ?)");
		prep.setString(1, name);
		if (ean == 0) {
			prep.setNull(2, Types.BIGINT);
		} else {
			prep.setLong(2, ean);
		}
		prep.setInt(3, price);
		prep.execute();
	}
}
