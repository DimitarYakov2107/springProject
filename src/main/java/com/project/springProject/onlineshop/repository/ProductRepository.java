package com.project.springProject.onlineshop.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.springProject.onlineshop.model.Product;

public interface ProductRepository extends JpaRepository<Product, String> {
	public Optional<Product> findByCode(String code);
}
