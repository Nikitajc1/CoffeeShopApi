package com.coffeeshop.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TestDataCleaner {
    private final DataBaseHelper dbHelper = new DataBaseHelper();
    private final String url =
            "jdbc:postgresql://localhost:5432/coffee_shop";
    private final String sqlDeleteUser = dbHelper.getDeleteUser();
    private final String sqlDeleteOrder = dbHelper.getDeleteOrder();

    public void deleteUserByEmail(String email) throws SQLException {

        try (Connection connection =
                     DriverManager.getConnection(
                url,
                System.getenv("POSTGRES_USER"),
                System.getenv("POSTGRES_PASSWORD")
        ); PreparedStatement statement1 = connection.prepareStatement(sqlDeleteOrder);
        PreparedStatement statement2 = connection.prepareStatement(sqlDeleteUser)) {

            statement1.setString(1, email);
            statement2.setString(1, email);

            connection.setAutoCommit(false);

            try {
                statement1.executeUpdate();
                statement2.executeUpdate();
                connection.commit();

            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            }
        }
    }
}
