package com.jneneve.todo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jneneve.todo.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query(value = "SELECT u.email FROM User u WHERE u.email = ?1")
	Optional<User> findByEmail(String email);
}
