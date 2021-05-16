package com.vnpt.epay.authen.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.vnpt.epay.authen.entity.User;
import com.vnpt.epay.authen.service.IUserService;
import com.vnpt.epay.authen.service.UserDetailsServiceImpl;
import com.vnpt.epay.authen.service.UserSevice;

import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrDataFactory;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.secret.SecretGenerator;

@Controller
public class MainController {
	@Autowired
	UserDetailsServiceImpl userDetailsServiceImpl;

	@Autowired
	UserSevice userSevice;

	@Autowired
	SecretGenerator secretGenerator;

	@Autowired
	private QrDataFactory qrDataFactory;

	@Autowired
	private QrGenerator qrGenerator;

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/home")
	public String admin() {
		return "admin";
	}

	@GetMapping("/signup")
	public String signup() {
		
		return "/signup";
	}
	
	 @RequestMapping(value = "/qr", method = RequestMethod.POST)
	    public String submitCreateUser(Model model, @RequestParam HashMap<String, String> reqParams, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
	    		User user = new User();
		    	String email = reqParams.get("email");
		        String password = reqParams.get("password");
		        
		        user.setEmail(email);
		        user.setPassword(password);
		        String secret = secretGenerator.generate();
		        user.setSecret(secret);
		        userSevice.createUser(user);
		        String   qrUrl = userSevice.generateQRUrl(user);
		        model.addAttribute("qrurl", qrUrl);
		        
		        return "qr";
	     
	    }
	@GetMapping("/403")
	public String accessDenied() {
		return "403";
	}

	@GetMapping("/login")
	public String getLogin() {
		return "signin";
	}

}