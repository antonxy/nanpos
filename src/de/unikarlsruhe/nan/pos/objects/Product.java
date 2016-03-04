package de.unikarlsruhe.nan.pos.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import de.unikarlsruhe.nan.pos.DatabaseConnection;

public class Product {
	private String name;
	private int price;
	private int id, ean;

	public static List<Product> getAllProducts() throws SQLException {
		LinkedList<Product> res = new LinkedList<Product>();
		PreparedStatement prep = DatabaseConnection.getInstance().prepare(
				"SELECT * FROM products");
		try (ResultSet resSet = prep.executeQuery()) {
			while (resSet.next()) {
				res.add(new Product(resSet));
			}
		}
		return res;
	}

	public static Product getByEAN(int ean) throws SQLException {
		PreparedStatement prep = DatabaseConnection.getInstance().prepare(
				"SELECT * FROM products WHERE ean=?");
		prep.setInt(1, ean);
		try (ResultSet res = prep.executeQuery()) {
			res.next();
			return new Product(res);
		}
	}

	public Product(ResultSet resSet) throws SQLException {
		this.name = resSet.getString("name");
		this.price = resSet.getInt("price");
		this.id = resSet.getInt("id");
		this.ean = resSet.getInt("ean");
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

	public int getEan() {
		return ean;
	}

}
