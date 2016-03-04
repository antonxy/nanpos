package de.unikarlsruhe.nan.pos.objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import de.unikarlsruhe.nan.pos.DatabaseConnection;

public class Product {
	private String name;
	private double price;
	private int id;

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

	public Product(ResultSet resSet) throws SQLException {
		this.name = resSet.getString("name");
		this.price = resSet.getDouble("price");
		this.id = resSet.getInt("id");
	}

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}

	public int getId() {
		return id;
	}

}
