package com.project.springProject.onlineshop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.springProject.onlineshop.model.Order;

public interface OrderRepository extends JpaRepository<Order, String> {
	
}
