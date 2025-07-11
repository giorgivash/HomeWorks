package service;

import model.User;
import java.util.*;

public class UserManager {
    private final Set<User> users = new HashSet<>();

    public boolean addUser(User user) {
        return users.add(user);
    }

    public boolean removeUser(int userId) {
        return users.removeIf(u -> u.getID() == userId);
    }

    public User getUserById(int userId) {
        return users.stream()
                .filter(u -> u.getID() == userId)
                .findFirst()
                .orElse(null);
    }

    public boolean userExists(int userId) {
        return users.stream().anyMatch(u -> u.getID() == userId);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}