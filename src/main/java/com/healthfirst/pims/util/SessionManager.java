package com.healthfirst.pims.util;

import com.healthfirst.pims.model.User;

/**
 * Ugeshan Naicker Student No.402313594
 * Holds the currently logged-in user for the lifetime of the application session.
 */
public final class SessionManager {

    private static User currentUser;

    private SessionManager() {}

    public static void login(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isAdmin() {
        return currentUser != null && "Admin".equalsIgnoreCase(currentUser.getRole());
    }
}
