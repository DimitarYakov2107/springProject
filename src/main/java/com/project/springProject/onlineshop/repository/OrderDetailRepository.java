package com.project.springProject.onlineshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.springProject.onlineshop.model.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {

}
