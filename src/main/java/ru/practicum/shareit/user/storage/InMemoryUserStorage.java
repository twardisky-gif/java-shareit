package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Long> userIdsByEmail = new HashMap<>();
    private long lastGeneratedId;

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(++lastGeneratedId);
        } else {
            User stored = users.get(user.getId());
            if (stored != null) {
                userIdsByEmail.remove(stored.getEmail());
            }
        }
        users.put(user.getId(), user);
        userIdsByEmail.put(user.getEmail(), user.getId());
        return user;
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userIdsByEmail.get(email)).map(users::get);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(Long userId) {
        User removed = users.remove(userId);
        if (removed != null) {
            userIdsByEmail.remove(removed.getEmail());
        }
    }
}
