package com.vnpt.epay.authen.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vnpt.epay.authen.entity.User;
import com.vnpt.epay.authen.repository.UserRepository;

@Service
public class UserSevice {
	@Autowired
	UserRepository userRepository;
	public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";

	public String generateQRUrl(User user) throws UnsupportedEncodingException {
		return QR_PREFIX + URLEncoder.encode(String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", "AuthenWeb",
				user.getEmail(), user.getSecret(), "AuthenWeb"), "UTF-8");
	}

	public User findBySecret(String secret) {
		User user = userRepository.findBySecret(secret);
		return user;
	}
	public void createUser(User user) {
		userRepository.save(user);
	};
	
}
