package ru.otus.dao;

import ru.otus.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryUserDao implements UserDao {

    private final Map<Long, User> users;

    public InMemoryUserDao() {
        users = new HashMap<>();
        users.put(1L, new User(1L, "Администратор", "admin", "admin"));
        users.put(7L, new User(7L, "Брэндон Смит", "user7", "11111"));
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return users.values().stream().filter(user -> user.getLogin().equals(login)).findFirst();
    }
}
