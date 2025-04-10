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
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserById(Long id) {
        logger.info("Attempting to fetch user with ID: {}", id);
        Optional<User> userOptional = userRepository.findByIdNoPassword(id);
        if (userOptional.isPresent()) {
            logger.info("User found with ID: {}", id);
            return userOptional.get();
        } else {
            logger.error("User with ID {} not found", id);
            throw new RuntimeException("User with ID " + id + " not found");
        }
    }

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
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().trim().isEmpty()) {
                String encodedPassword = passwordEncoder.encode(updatedUser.getPassword());
                existingUser.setPassword(encodedPassword);
                logger.debug("Updated password to bcrypt-encoded value: {}", encodedPassword);
            } else {
                Optional<User> userByEmailOptional = userRepository.findByEmail(existingUser.getEmail());
                if (userByEmailOptional.isPresent()) {
                    String existingPassword = userByEmailOptional.get().getPassword();
                    existingUser.setPassword(existingPassword);
                    logger.debug("Password not provided or empty; reapplied existing password from DB: {}", existingPassword);
                } else {
                    logger.error("Could not find user by email {} to reapply password", existingUser.getEmail());
                    throw new RuntimeException("User with email " + existingUser.getEmail() + " not found");
                }
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