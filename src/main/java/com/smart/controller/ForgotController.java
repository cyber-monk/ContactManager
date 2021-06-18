package com.smart.controller;

import java.text.DecimalFormat;
import java.util.Random;

import javax.mail.Session;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.service.EmailService;

@Controller
public class ForgotController {

	
	@Autowired
	private EmailService emailservice;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder; 
	
	//Email id form open handler
	@RequestMapping("/forgot")
	public String openEmailForm() {
		
		return "forgot_email_form";
	}

	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email,HttpSession session) {
		
		
		User user = this.userRepository.getUserByUsername(email);
		
		if(user == null) {
			//send error
			session.setAttribute("message", new Message("User does not exist with this email!! ", "alert-danger"));
			return "forgot_email_form";
		}else{
			//Generate OTP of 6 digit
			String otp= new DecimalFormat("000000").format(new Random().nextInt(999999));
			System.out.println(otp);
			
			String subject = "OTP from smart contact manager";
			String message = ""
					+ "<div style='border:1px solid #e2e2e2; padding:20px'>"
					+ "<h1>"
					+ "OTP : "
					+ "<b>"+ otp 
					+ "</b>"
					+ "</h1>"
					+ "</div>" ;
			
			//Sending email of otp
			boolean flag = this.emailservice.sendEmail(message, subject, email);
			
			if(flag) {
				//OTP SENT
				session.setAttribute("myotp", otp);
				session.setAttribute("email", email);
				session.setAttribute("message", new Message("Mail sent to your email address!! ", "alert-success"));		
				return "verify_otp";
			}else {
				//OTP NOT SENT
				session.setAttribute("message", new Message("Please enter valid email addreess!! ", "alert-danger"));
				return "forgot_email_form";
			}	
		}
	}
	
	//Verify OTP
	@PostMapping("/verify-otp")
	public String verifyOTP(@RequestParam("otp") String otp, HttpSession session) {
		String myOtp = (String)session.getAttribute("myotp"); 
		
		System.out.println("myotp : "+ myOtp);
		System.out.println("otp : "+ otp);
		
		if(myOtp.equals(otp)) {
			//show change password form
			return "password_change_form";
		}else {
			session.setAttribute("message", new Message("You have entered wrong OTP!! ", "alert-danger"));
			return "verify_otp";
		}
		
	}
	
	
	//Change Password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newPassword") String newPassword, HttpSession session) {
		String email = (String)session.getAttribute("email");
		User user = this.userRepository.getUserByUsername(email);
		
		if(user != null) {
			user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(user);
			session.setAttribute("message", new Message("Password change successfully!! ", "alert-success"));
		}else {
			session.setAttribute("message", new Message("Error while changing password!! ", "alert-danger"));
		}
		
		
		return "login";
	}
}
