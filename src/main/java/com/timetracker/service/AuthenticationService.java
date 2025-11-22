package com.timetracker.service;

import com.timetracker.dao.UserDAO;
import com.timetracker.model.User;
import com.timetracker.util.SessionManager;

public class AuthenticationService {
    private final UserDAO userDAO;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
    }

    public boolean login(String username, String password) {
        User user = userDAO.authenticate(username, password);
        if (user != null) {
            SessionManager.getInstance().setCurrentUser(user);
            return true;
        }
        return false;
    }

    public void logout() {
        SessionManager.getInstance().logout();
    }

    public User getCurrentUser() {
        return SessionManager.getInstance().getCurrentUser();
    }
}