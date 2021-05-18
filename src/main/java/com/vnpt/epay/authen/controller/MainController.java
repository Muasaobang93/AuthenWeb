package com.vnpt.epay.authen.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.vnpt.epay.authen.entity.User;
import com.vnpt.epay.authen.service.UserDetailsServiceImpl;
import com.vnpt.epay.authen.service.UserSevice;

import dev.samstevens.totp.code.CodeVerifier;
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
	private CodeVerifier verifier;

	@GetMapping("/")
	public String index() {
		return "signin";
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
	public String submitCreateUser(Model model, @RequestParam HashMap<String, String> reqParams,
			RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
		User user = new User();
		String email = reqParams.get("email");
		String password = reqParams.get("password");
		Boolean usingMFA = Boolean.parseBoolean(reqParams.get("ismfa"));

		user.setEmail(email);
		user.setPassword(password);
		if (usingMFA) {
			String secret = secretGenerator.generate();
			user.setSecret(secret);
			userSevice.createUser(user);
			String qrUrl = userSevice.generateQRUrl(user);
			model.addAttribute("qrurl", qrUrl);

			return "qr";
		} else {
			userSevice.createUser(user);
			model.addAttribute("error", "Tạo tài khoản thành công vui lòng đăng nhập");
			return "signin";
		}

	}

	@GetMapping("/403")
	public String accessDenied() {
		return "403";
	}

	@GetMapping("/login")
	public String getLogin() {
		return "login";
	}

	@GetMapping("/signin")
	public String getSignin(Model model) {
		model.addAttribute("error", "Nhập thông tin đăng nhập");
		return "signin";
	}
	
	@GetMapping("/changeotp")
	public String changeTotp() {
		return "changeotp";
	}
	
	@RequestMapping(value = "/signinmfa", method = RequestMethod.POST)
	public String submitSignIn(Model model, @RequestParam HashMap<String, String> reqParams,
			RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {

		String email = reqParams.get("email");
		String password = reqParams.get("password");
		User user = userSevice.findByEmail(email);
		if (user != null) {
			if (user.getPassword().equalsIgnoreCase(password)) {
				if (user.isAdditionalSecurity()) {
					model.addAttribute("error", "Vui lòng nhập mã xác thực TOTP");
					model.addAttribute("user", user);
					return "confirmmfa";
				} else {
					model.addAttribute("user", user);
					return "admin";
				}
			} else {
				model.addAttribute("error", "Sai mật khẩu");
				return "/signin";
			}

		} else {
			model.addAttribute("error", "Sai tài khoản");
			return "/signin";
		}

	}

	@RequestMapping(value = "/confirmmfa", method = RequestMethod.POST)
	public String confirmMfa(Model model, @RequestParam HashMap<String, String> reqParams,
			@RequestParam(name = "email", required = true) String email, RedirectAttributes redirectAttributes) {
		String authencode = reqParams.get("authencode");
		User user = userSevice.findByEmail(email);
		if (user != null) {
			try {
				if (verifier.isValidCode(user.getSecret(), authencode)) {
					model.addAttribute("user", user);
					return "admin";

				} else {
					model.addAttribute("error", "Mã xác thực google chưa đúng");
					return "/confirmmfa";
				}
			} catch (Exception e) {
				model.addAttribute("error", "Lỗi xác thực");
				return "/confirmmfa";
			}
		} else {
			model.addAttribute("error", "Vui lòng đăng nhập lại");
			return "/signin";
		}
	}

	@RequestMapping(value = "/changemfa", method = RequestMethod.POST)
	public String changeMFA(Model model, @RequestParam HashMap<String, String> reqParams,
			RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
		
		return "signin";
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		String sr = URLEncoder.encode(String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", "AuthenWeb", "quannh",
				"secretKey", "AuthenWeb"), "UTF-8");
		String randomSecret = Base32.random();
		System.out.println("sr = " + sr);
		System.out.println("randomSecret = " + randomSecret);
		Totp totp = new Totp(randomSecret);
		totp.verify("123456");
	}

}