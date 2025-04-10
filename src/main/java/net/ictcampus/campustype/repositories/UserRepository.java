package net.ictcampus.campustype.repositories;

import net.ictcampus.campustype.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:username)")
    Optional<User> findByUsername(String username);

    @Query("SELECT new net.ictcampus.campustype.models.User(u.id, u.username, u.email, u.bio, u.keyboard) " +
            "FROM User u WHERE u.id = :id")
    Optional<User> findByIdNoPassword(Long id);
}