package dao;

import db.DBUtil;
import entity.Product;
import entity.User;

import java.sql.*;
import java.util.*;

public class OrderProcessor implements OrderManagementRepository {

    @Override
    public void createUser(User user) throws Exception {
        Connection conn = DBUtil.getDBConn();
        String query = "INSERT INTO users (userId, username, password, role) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, user.getUserId());
        stmt.setString(2, user.getUsername());
        stmt.setString(3, user.getPassword());
        stmt.setString(4, user.getRole());
        stmt.executeUpdate();
        conn.close();
    }

    @Override
    public void createProduct(User user, Product product) throws Exception {
        if (!"Admin".equalsIgnoreCase(user.getRole())) {
            throw new Exception("Only admin can create products");
        }
        Connection conn = DBUtil.getDBConn();
        String query = "INSERT INTO products (productId, productName, description, price, quantityInStock, type) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, product.getProductId());
        stmt.setString(2, product.getProductName());
        stmt.setString(3, product.getDescription());
        stmt.setDouble(4, product.getPrice());
        stmt.setInt(5, product.getQuantityInStock());
        stmt.setString(6, product.getType());
        stmt.executeUpdate();
        conn.close();
    }

    @Override
    public void createOrder(User user, List<Product> products) throws Exception {
        Connection conn = DBUtil.getDBConn();

        String checkUserQuery = "SELECT * FROM users WHERE userId = ?";
        PreparedStatement checkUserStmt = conn.prepareStatement(checkUserQuery);
        checkUserStmt.setInt(1, user.getUserId());
        ResultSet rs = checkUserStmt.executeQuery();

        if (!rs.next()) {
            createUser(user);
        }

        String orderInsert = "INSERT INTO orders (userId) VALUES (?)";
        PreparedStatement orderStmt = conn.prepareStatement(orderInsert, Statement.RETURN_GENERATED_KEYS);
        orderStmt.setInt(1, user.getUserId());
        orderStmt.executeUpdate();
        ResultSet generatedKeys = orderStmt.getGeneratedKeys();
        int orderId = 0;
        if (generatedKeys.next()) {
            orderId = generatedKeys.getInt(1);
        }

        String orderProductInsert = "INSERT INTO orderproducts (orderId, productId) VALUES (?, ?)";
        for (Product p : products) {
            PreparedStatement orderProductStmt = conn.prepareStatement(orderProductInsert);
            orderProductStmt.setInt(1, orderId);
            orderProductStmt.setInt(2, p.getProductId());
            orderProductStmt.executeUpdate();
        }

        conn.close();
    }

    @Override
    public void cancelOrder(int userId, int orderId) throws Exception {
        Connection conn = DBUtil.getDBConn();

        String checkUser = "SELECT * FROM users WHERE userId = ?";
        PreparedStatement stmtUser = conn.prepareStatement(checkUser);
        stmtUser.setInt(1, userId);
        ResultSet rsUser = stmtUser.executeQuery();
        if (!rsUser.next()) {
            throw new Exception("UserNotFound");
        }

        String checkOrder = "SELECT * FROM orders WHERE orderId = ? AND userId = ?";
        PreparedStatement stmtOrder = conn.prepareStatement(checkOrder);
        stmtOrder.setInt(1, orderId);
        stmtOrder.setInt(2, userId);
        ResultSet rsOrder = stmtOrder.executeQuery();
        if (!rsOrder.next()) {
            throw new Exception("OrderNotFound");
        }

        String deleteOrderProducts = "DELETE FROM orderproducts WHERE orderId = ?";
        PreparedStatement stmt1 = conn.prepareStatement(deleteOrderProducts);
        stmt1.setInt(1, orderId);
        stmt1.executeUpdate();

        String deleteOrder = "DELETE FROM orders WHERE orderId = ?";
        PreparedStatement stmt2 = conn.prepareStatement(deleteOrder);
        stmt2.setInt(1, orderId);
        stmt2.executeUpdate();

        conn.close();
    }

    @Override
    public List<Product> getAllProducts() throws Exception {
        List<Product> products = new ArrayList<>();
        Connection conn = DBUtil.getDBConn();
        String query = "SELECT * FROM products";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        while (rs.next()) {
            Product p = new Product(
                    rs.getInt("productId"),
                    rs.getString("productName"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getInt("quantityInStock"),
                    rs.getString("type")
            );
            products.add(p);
        }
        conn.close();
        return products;
    }

    @Override
    public List<Product> getOrderByUser(User user) throws Exception {
        List<Product> products = new ArrayList<>();
        Connection conn = DBUtil.getDBConn();
        String query = "SELECT p.* FROM products p JOIN orderproducts op ON p.productId = op.productId JOIN orders o ON op.orderId = o.orderId WHERE o.userId = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, user.getUserId());
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Product p = new Product(
                    rs.getInt("productId"),
                    rs.getString("productName"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getInt("quantityInStock"),
                    rs.getString("type")
            );
            products.add(p);
        }
        conn.close();
        return products;
    }

    @Override
    public double getTotalAmountSpentByUser(User user) throws Exception {
        double total = 0;
        Connection conn = DBUtil.getDBConn();
        String query = "SELECT SUM(p.price) as totalSpent FROM products p JOIN orderproducts op ON p.productId = op.productId JOIN orders o ON op.orderId = o.orderId WHERE o.userId = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, user.getUserId());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            total = rs.getDouble("totalSpent");
        }
        conn.close();
        return total;
    }
}