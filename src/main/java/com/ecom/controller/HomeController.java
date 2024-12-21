package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.repository.ProductRepository;
import com.ecom.service.CategoryService;
import com.ecom.service.Productservice;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private UserService userService;

	@Autowired
	private Productservice productservice;

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/signin")
	public String login() {
		return "login";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@GetMapping("/products")
	public String products(Model m, @RequestParam(value = "category", defaultValue = "") String category) {
//	System.out.println("category="+category);
		List<Category> categories = categoryService.getAllActiveCategory();
		List<Product> products = productservice.getAllActiveProducts(category);
		m.addAttribute("categories", categories);
		m.addAttribute("products", products);
		m.addAttribute("paramValue", category);
		return "product";
	}

	@GetMapping("/product/{id}")
	public String product(@PathVariable int id, Model m) {
		Product productById = productservice.getProductById(id);
		m.addAttribute("product", productById);
		return "view_product";
	}

	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
			throws IOException {

		Boolean existsEmail = userService.existsEmail(user.getEmail());

		if (existsEmail) {
			session.setAttribute("errorMsg", "Email already exist");
		} else {
			String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
			user.setProfileImage(imageName);
			UserDtls saveUser = userService.saveUser(user);

			if (!ObjectUtils.isEmpty(saveUser)) {
				if (!file.isEmpty()) {
					File saveFile = new ClassPathResource("static/img").getFile();

					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
							+ file.getOriginalFilename());

//					System.out.println(path);
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				}
				session.setAttribute("succMsg", "Register successfully");
			} else {
				session.setAttribute("errorMsg", "something wrong on server");
			}
		}

		return "redirect:/register";
	}

	// Forgot Password logic code

	@GetMapping("/forgot-password")
	public String showForgotPassword() {
		return "forgot_password.html";
	}

	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException
	{

		UserDtls userByEmail = userService.getUserByEmail(email);

		if (ObjectUtils.isEmpty(userByEmail)) {
			session.setAttribute("errorMsg", "Invalid email");
		} else {

			String resetToken = UUID.randomUUID().toString();
			userService.updateUserResetToken(email, resetToken);

			// Generate URL :
			// http://localhost:8080/reset-password?token=sfgdbgfswegfbdgfewgvsrg

			String url = CommonUtil.generateUrl(request) + "/reset-password?token=" + resetToken;

			Boolean sendMail = CommonUtil.sendMail(url, email);

			if (sendMail) {
				session.setAttribute("succMsg", "Please check your email..Password Reset link sent");
			} else {
				session.setAttribute("errorMsg", "Somethong wrong on server ! Email not send");
			}
		}

		return "redirect:/forgot-password";
	}

	@GetMapping("/reset-password")
	public String showResetPassword() {
		return "reset.html";
	}

}
