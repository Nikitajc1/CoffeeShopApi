package com.coffeeshop.helpers;

public class DataBaseHelper {
    private final String deleteUser = "DELETE FROM users WHERE email = ?;";

    private final String deleteOrder = "DELETE FROM orders " +
            "WHERE user_id IN (SELECT id FROM users WHERE email = ?)";

    public String getDeleteUser() {
        return deleteUser;
    }

    public String getDeleteOrder() {
        return deleteOrder;
    }
}
