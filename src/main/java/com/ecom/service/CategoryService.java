package com.ecom.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ecom.model.Category;

public interface CategoryService {

	
	public Category saveCategory(Category category);
	
	public Boolean existCategory(String name);
	
	public List<Category> getAllCategory();
	
	public Boolean deleteCategory(int id);
	
	public Category getCategoryById(int id);
	
	public List<Category> getAllActiveCategory();

	public Integer getCountCart(Integer id);
	
	public Page<Category> getAllCategorPagination(Integer pageNo, Integer pageSize);
	
}
