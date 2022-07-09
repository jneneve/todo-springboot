package com.jneneve.todo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jneneve.todo.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query(value = "SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE u.email = ?1")
	Boolean findByEmail(String email);
}
