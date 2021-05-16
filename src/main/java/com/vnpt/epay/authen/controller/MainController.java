package com.vnpt.epay.authen.controller;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vnpt.epay.authen.entity.User;
import com.vnpt.epay.authen.service.IUserService;
import com.vnpt.epay.authen.service.UserDetailsServiceImpl;
import com.vnpt.epay.authen.service.UserSevice;

@Controller
public class MainController {
	@Autowired
	UserDetailsServiceImpl userDetailsServiceImpl;
	
	@Autowired
	UserSevice userSevice;
	
	@Autowired
	IUserService iUserService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/signin") 
    public String admin() {
        return "admin";
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "403";
    }

    @GetMapping("/login") 
    public String getLogin() {
        return "login";
    }
    
    @GetMapping("/registrationConfirm")
    public String confirmRegistration(final HttpServletRequest request,final ModelMap model,@RequestParam("token") String token) throws UnsupportedEncodingException {
    	Locale locale = request.getLocale();
        String result = iUserService.validateVerificationToken(token);
        if(result.equals("valid")) {
            User user = userSevice.findBySecret(token);
            if (user.isAdditionalSecurity()) {
                model.addAttribute("qr", userSevice.generateQRUrl(user));
                return "redirect:/qrcode.html?lang=" + locale.getLanguage();
            }
            
            model.addAttribute(
              "message", "save");
            return "redirect:/login?lang=" + locale.getLanguage();
        }
		return result;
        
        
       
    }
}