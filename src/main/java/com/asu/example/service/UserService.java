package com.asu.example.service;

import com.asu.annotations.Autowired;
import com.asu.annotations.Component;
import com.asu.tx.annotation.Transactional;
import com.asu.example.entity.User;
import com.asu.example.repository.UserRepository;
import com.asu.orm.Session;

import java.util.List;
@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Session session;

    // =============================
    // CREATE (INSERT)
    // =============================
    @Transactional
    public void createUser() {

        User user = new User(1L, "John", "john@mail.com");

        session.persist(user);

        // flush happens on TX commit (if wired)
    }

    // =============================
    // READ (LAZY LOADING)
    // =============================
    @Transactional
    public User getUser(Long id) {

        User user = session.find(User.class, id);

        // proxy triggers DB load when accessed
        System.out.println(user.getName());

        return user;
    }

    // =============================
    // UPDATE (DIRTY CHECKING)
    // =============================
    @Transactional
    public void updateUser(Long id) {

        User user = session.find(User.class, id);

        user.setName("Updated Name");

        // 🔥 No explicit save
        // dirty checking will trigger update
    }

    // =============================
    // DELETE
    // =============================
    @Transactional
    public void deleteUser(Long id) {

        User user = session.find(User.class, id);

        session.remove(user);
    }

    // =============================
    // REPOSITORY QUERY
    // =============================
    @Transactional
    public List<User> findByName(String name) {
        return userRepository.findByName(name);
    }
}