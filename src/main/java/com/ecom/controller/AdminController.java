package com.ecom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.service.CategoryService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private CategoryService categoryService;
	
	@GetMapping("/")
	public String index()
	{
		return "admin/index";
	}
	@GetMapping("/loadAddProduct")
	public String loadAddProduct()
	{
		return "admin/add_product";
	}
	@GetMapping("/category")
	public String category()
	{
		return "admin/category";
	}
	
	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category,@RequestParam("file")MultipartFile file, HttpSession session )
	{
		
		String imageName =file!=null ? file.getOriginalFilename() : "default.jpg";
		category.setImageName(imageName);
		
		Boolean existCategory = categoryService.existCategory(category.getName());
		
		/* Boolean existCategory = categoryService.existCategory(category.getName()); */
	
	if(existCategory)
	{
		session.setAttribute("errorMsg","Category Name already exists");
	}else
	{
		Category saveCategory = categoryService.saveCategory(category);
		
		if(ObjectUtils.isEmpty(saveCategory))
		{
			session.setAttribute("error", "Not saved ! interna server error");
		}
		else
		{
			session.setAttribute("succMsg", "Saved successfully");
		}
	}
		
		return "redirect:/admin/category";
	}
}
