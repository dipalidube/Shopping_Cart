package com.ecom.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Product;
import com.ecom.repository.ProductRepository;
import com.ecom.service.Productservice;


@Service
public class ProductServiceImpl implements Productservice {

	@Autowired
	private ProductRepository productRepository;
	
	@Override
	public Product saveProduct(Product product) {
	
		return productRepository.save(product);
	}

	@Override
	public List<Product> getAllProducts() {
		
		return productRepository.findAll();
	}

	@Override
	public Boolean deleteProduct(Integer id) {
		Product product=productRepository.findById(id).orElse(null);
		if(!ObjectUtils.isEmpty(product))
		{
			productRepository.delete(product);
			return true;
		}
		return false;
	}

	@Override
	public Product getProductById(Integer id) {
	Product product = productRepository.findById(id).orElse(null);
		return product;
	}

	@Override
	public Product updateProduct(Product product, MultipartFile file) {
		Product dbProduct =getProductById(product.getId());
		String imageName =file.isEmpty() ? dbProduct.getImage() : file.getOriginalFilename();
		
		dbProduct.setTitle(product.getTitle());
		dbProduct.setDescription(product.getDescription());
		dbProduct.setCategory(product.getCategory());
		dbProduct.setPrice(product.getPrice());
		dbProduct.setStock(product.getStock());
		dbProduct.setImage(imageName);

		dbProduct.setDiscount(product.getDiscount());
		//100*(5/100; 100-5=95)
		Double discount =product.getPrice()*(product.getDiscount()/100.0);
		Double discountPrice= product.getPrice()-discount;
		
		dbProduct.setDiscountPrice(discountPrice);
		
		Product updateProduct = productRepository.save(dbProduct);
		if(!ObjectUtils.isEmpty(updateProduct))
		{
			if(!file.isEmpty())
			{
				try {
				File saveFile = new ClassPathResource("static/img").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+ "product_img" 
				+File.separator+file.getOriginalFilename());
				
				System.out.println(path);
				Files.copy(file.getInputStream(), path ,StandardCopyOption.REPLACE_EXISTING);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

			}
			return product;
		}
		
		return null;
	}
	
	
}
