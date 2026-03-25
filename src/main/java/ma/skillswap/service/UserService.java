package ma.skillswap.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import ma.skillswap.dao.UserDao;
import ma.skillswap.model.entity.User;
import ma.skillswap.util.PasswordUtil;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserService {

    @Inject
    private UserDao userDao;

    public User register(String nom, String email, String password, String ville) {
        if (userDao.existsByEmail(email)) {
            throw new IllegalArgumentException("Un compte avec cet email existe déjà.");
        }
        User user = new User(nom, email, PasswordUtil.hashPassword(password), ville);
        return userDao.save(user);
    }

    public Optional<User> authenticate(String email, String password) {
        Optional<User> userOpt = userDao.findByEmail(email);
        if (userOpt.isPresent() && PasswordUtil.verifyPassword(password, userOpt.get().getPassword())) {
            return userOpt;
        }
        return Optional.empty();
    }

    public User findById(Long id) {
        return userDao.findById(id);
    }

    public User updateProfile(User user) {
        return userDao.update(user);
    }

    public List<User> findAll() {
        return userDao.findAll();
    }

    public Optional<User> findByEmail(String email) {
        return userDao.findByEmail(email);
    }
}