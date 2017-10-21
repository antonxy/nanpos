package de.unikarlsruhe.nan.pos.objects;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.unikarlsruhe.nan.pos.DatabaseConnection;

public class User {
    private int id;
    private String name;
    private int balance;
    private boolean operator;

    public static User getUserByCardnr(String cardnr) throws SQLException,
            NoSuchAlgorithmException {
        PreparedStatement prep = DatabaseConnection.getInstance().prepare(
                "SELECT * FROM users WHERE card=?");
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(cardnr.getBytes());
        prep.setString(1, byteArrayToHexString(messageDigest.digest()));
        try (ResultSet res = prep.executeQuery()) {
            if (res.next()) {
                return new User(res);
            }
        }
        return null;

    }

    public static User getUserByNameAndPin(String name, String pin) throws SQLException,
            NoSuchAlgorithmException {
        PreparedStatement prep = DatabaseConnection.getInstance().prepare(
                "SELECT * FROM users WHERE name=? AND pin=?");
        prep.setString(1, name);
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(pin.getBytes());
        prep.setString(2, byteArrayToHexString(messageDigest.digest()));
        try (ResultSet res = prep.executeQuery()) {
            if (res.next()) {
                return new User(res);
            }
        }
        return null;

    }

    public static void createUser(User operator, String name, String cardnr)
            throws SQLException, NoSuchAlgorithmException {
        PreparedStatement prep = DatabaseConnection.getInstance().prepare(
                "INSERT INTO users (name, card) VALUES(?, ?)");
        prep.setString(1, name);
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(cardnr.getBytes());
        prep.setString(2, byteArrayToHexString(messageDigest.digest()));
        prep.execute();
    }

    public User(ResultSet resSet) throws SQLException {
        this.id = resSet.getInt("id");
        this.operator = resSet.getBoolean("isop");
        this.name = resSet.getString("name");
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

    public boolean isOpeartor() {
        return operator;
    }

    public void recharge(int amount) throws SQLException {
        PreparedStatement prep = DatabaseConnection
                .getInstance()
                .prepare(
                        "INSERT INTO revenues (\"user\", amount, product) VALUES(?, ?, NULL)");
        prep.setInt(1, id);
        prep.setInt(2, amount);
        prep.execute();
    }

    public synchronized boolean buy(Product p) throws SQLException {
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

    private static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public String getName() {
        return name;
    }

}
