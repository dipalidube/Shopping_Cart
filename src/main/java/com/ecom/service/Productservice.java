package com.ecom.service;

import java.util.List;

import com.ecom.model.Product;

public interface Productservice {

	
	public Product saveProduct(Product product);
	
	public List<Product> getAllProducts();
}
