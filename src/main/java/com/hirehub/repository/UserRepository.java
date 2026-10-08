package com.hirehub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hirehub.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);
}