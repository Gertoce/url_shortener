package com.urlshortener.service;

import com.urlshortener.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserService {
    private Map<UUID, User> users = new HashMap<>();

    public UUID createUser() {
        User user = new User();
        users.put(user.getUserId(), user);
        return user.getUserId();
    }

    public boolean userExists(UUID userId) {
        return users.containsKey(userId);
    }

    public User getUser(UUID userId) {
        return users.get(userId);
    }

    public void updateUser(User user) {
        if (user != null) {
            users.put(user.getUserId(), user);
        }
    }

    public int getTotalUsers() {
        return users.size();
    }
}