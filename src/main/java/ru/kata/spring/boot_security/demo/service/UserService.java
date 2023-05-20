package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.DAO.UserRepository;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @PersistenceContext
    private EntityManager em;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public User findUserById(Long userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public boolean saveUser(User user) {

        User userFromDB = userRepository.findByUsername(user.getUsername());

        if (userFromDB != null) {
            return false;
        }

        assignRoles(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public void assignRoles(User user) {
        user.setRoles(new HashSet<>());
        user.getRoles().add(new Role(1L, "ROLE_USER"));
        if (user.getRoleId() == 2) {
            user.getRoles().add(new Role(2L, "ROLE_ADMIN"));
        }
    }

    @Transactional
    public boolean isThereAnyRoles() {
        List<Role> roles = em.createNativeQuery("SELECT * FROM t_role").getResultList();
        return (roles.isEmpty());
    }

    @Transactional
    public void createRoles() {
        if (isThereAnyRoles()) {
            em.createNativeQuery("INSERT INTO t_role (id, name) VALUES (1, 'ROLE_USER'), (2, 'ROLE_ADMIN') ").executeUpdate();
        }
    }

    public boolean deleteUser(Long userId) {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    @Transactional
    public void updateUser(Long userId, User updatedUser) {
        User oldUser = userRepository.findById(userId).get();
        assignRoles(updatedUser);
        updatedUser.setUsername(oldUser.getUsername());
        updatedUser.setPassword(oldUser.getPassword());
        userRepository.save(updatedUser);
    }

    @Transactional
    public void createAdminUser() {
        createRoles();
            User admin = new User();
            admin.setUsername("admin");
            admin.setName("admin");
            admin.setSurname("admin");
            admin.setPassword("admin");
            admin.setRoleId(2);
            saveUser(admin);
    }
}
