/**
 * 
 */
package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;


/**
 * @author Akshay
 *
 */

@Controller
public class HomeController {
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	//Open Home
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title","Home- Smart Contact Manage");
		return "home";
	}

	//Open About Page
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title","About- Smart Contact Manage");
		return "about";
	}
	
	//Open Sign up Page	
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("Register","About- Smart Contact Manage");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	
	//This handler for register User
	@RequestMapping(value="/do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam(value="agreement", defaultValue = "false") boolean agreement, 
			Model model, HttpSession session) {
		
		try {
			if(!agreement) {
				throw new Exception("You have not agreed the terms and condition.");
			}
			
			if(result.hasErrors()) {
				model.addAttribute("user",user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			User userResult = this.userRepository.save(user);
			//model.addAttribute("user",userResult);
			
			session.setAttribute("message", new Message("Successfully registerd!!","alert-success"));
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("Something went wrong!! "+ e.getMessage(), "alert-danger"));
		}
		return "signup";
	}
	
	
	//Handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model model) {
		
		model.addAttribute("title", "Login Page");
		return "login";
	}
	
	
}
