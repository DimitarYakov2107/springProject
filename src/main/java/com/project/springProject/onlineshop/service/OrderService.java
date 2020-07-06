package com.project.springProject.onlineshop.service;

import java.util.List;

import com.project.springProject.onlineshop.model.dto.CartInfo;
import com.project.springProject.onlineshop.model.dto.OrderDetailInfo;
import com.project.springProject.onlineshop.model.dto.OrderInfo;
import com.project.springProject.onlineshop.pagination.PaginationResult;

public interface OrderService {
	public void saveOrder(CartInfo cartInfo);
	
	public PaginationResult<OrderInfo> listOrderInfo(int page, int maxResult, int maxNavigationPage);
	
	public OrderInfo getOrderInfo(String orderId);
	
	public List<OrderDetailInfo> listOrderDetailInfos(String orderId);
}