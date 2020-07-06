package com.project.springProject.onlineshop.service;

import java.util.Optional;
import com.project.springProject.onlineshop.model.Product;
import com.project.springProject.onlineshop.model.dto.ProductInfo;
import com.project.springProject.onlineshop.model.form.ProductForm;
import com.project.springProject.onlineshop.pagination.PaginationResult;

public interface ProductService {
	public Optional<Product> findByCode(String code);
	
	 public PaginationResult<ProductInfo> queryProducts(int page, int maxResult, int maxNavigationPage,
	            String likeName);
	 
	 public PaginationResult<ProductInfo> queryProducts(int page, int maxResult, int maxNavigationPage);
	 
	 public void save(ProductForm productForm);
}
