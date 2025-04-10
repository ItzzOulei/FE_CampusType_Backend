package net.ictcampus.campustype.services;

import net.ictcampus.campustype.models.User;
import net.ictcampus.campustype.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder; // Expected to be BCryptPasswordEncoder
    }

    // Methode zum Abrufen eines Users anhand der ID
    public User getUserById(Long id) {
        logger.info("Attempting to fetch user with ID: {}", id);
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            logger.info("User found with ID: {}", id);
            return userOptional.get();
        } else {
            logger.error("User with ID {} not found", id);
            throw new RuntimeException("User with ID " + id + " not found");
        }
    }

    // Methode zum Aktualisieren eines Users
    public User updateUser(Long id, User updatedUser) {
        logger.info("Attempting to update user with ID: {}", id);
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            if (updatedUser.getUsername() != null) {
                existingUser.setUsername(updatedUser.getUsername());
                logger.debug("Updated username to: {}", updatedUser.getUsername());
            }
            if (updatedUser.getEmail() != null) {
                existingUser.setEmail(updatedUser.getEmail());
                logger.debug("Updated email to: {}", updatedUser.getEmail());
            }
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(updatedUser.getPassword());
                existingUser.setPassword(encodedPassword);
                logger.debug("Updated password to bcrypt-encoded value");
            }
            if (updatedUser.getBio() != null) {
                existingUser.setBio(updatedUser.getBio());
                logger.debug("Updated bio to: {}", updatedUser.getBio());
            }
            if (updatedUser.getKeyboard() != null) {
                existingUser.setKeyboard(updatedUser.getKeyboard());
                logger.debug("Updated keyboard to: {}", updatedUser.getKeyboard());
            }
            User savedUser = userRepository.save(existingUser);
            logger.info("User with ID {} successfully updated", id);
            return savedUser;
        } else {
            logger.error("User with ID {} not found for update", id);
            throw new RuntimeException("User with ID " + id + " not found");
        }
    }

    public void deleteUser(Long id) {
        logger.info("Attempting to delete user with ID: {}", id);
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userRepository.deleteById(id);
            logger.info("User with ID {} successfully deleted", id);
        } else {
            logger.error("User with ID {} not found for deletion", id);
            throw new RuntimeException("User with ID " + id + " not found");
        }
    }
}