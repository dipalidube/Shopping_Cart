package com.ecom.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;



@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override 
	public UserDtls saveUser(UserDtls user) {
		user.setRole("ROLE_USER");
		user.setIsEnable(true);
		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		UserDtls saveUser = userRepository.save(user);
		return saveUser;
	}
	
	@Override
	public Boolean updateUserAccountStatus(Integer id, Boolean status) {
		Optional<UserDtls> findyByuser=userRepository.findById(id);
		
		if(findyByuser.isPresent())
		{
			UserDtls userDtls=findyByuser.get();
			userDtls.setIsEnable(status);
			userRepository.save(userDtls);
			return true;
			
		}
		
		return false;
	}

	@Override
	public UserDtls getUserByEmail(String email) {
		
		return userRepository.findByEmail(email);
	}

	@Override
	public Boolean existsEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public List<UserDtls> getUsers(String role) {
		return userRepository.findByRole(role);
		
	}

}
