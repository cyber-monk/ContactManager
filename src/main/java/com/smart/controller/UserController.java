package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	// Method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		// to get username
		String username = principal.getName();

		// get the user using username(email)
		User user = this.userRepository.getUserByUsername(username);

		model.addAttribute("user", user);
	}

	// Open Dashboard Home
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	// Open add form Handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {

		model.addAttribute("title", "Add contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	// Processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute("contact") Contact contact, BindingResult result, Model model,
			@RequestParam("profileImage") MultipartFile file, Principal principal, HttpSession session) {

		if(result.hasErrors()) {
			model.addAttribute("title", "Add contact");
			model.addAttribute("contact",contact);
			return "normal/add_contact_form";
		}
		
		try {
			// to get username
			String username = principal.getName();
			// get the user using username(email)
			User user = this.userRepository.getUserByUsername(username);

			// Processing and Uploading file
			if (file.isEmpty()) {
				// if empty then error message
				System.out.println("File is empty..");
				contact.setImage("defaultProfilePic.png");
			} else {
				// save the file to folder and update the name to contact
				File saveFile = new ClassPathResource("static/img/profilePic").getFile();

				Date date = new Date();
				Long time = date.getTime();
				String uniqueFileName = time.toString().concat(file.getOriginalFilename());

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + uniqueFileName);

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Images is uploaded..");

				contact.setImage(uniqueFileName);
			}

			contact.setUser(user);

			user.getContacts().add(contact);

			this.userRepository.save(user);

			System.out.println("Added to database.");

			// Massage success display
			session.setAttribute("message", new Message("Contact Added successfully!!", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();

			// error msg display
			session.setAttribute("message", new Message("Something went wrong!! " + e.getMessage(), "alert-danger"));

		}

		return "normal/add_contact_form";
	}

	// Show contact list
	// per page = 5
	// current page = 0 [page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {

		String username = principal.getName();
		User user = this.userRepository.getUserByUsername(username);

		/*
		 * Add pagenation information Current page : page number of records : 5
		 */
		Pageable pageable = PageRequest.of(page, 5);

		// Return Contact list
		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);

		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		model.addAttribute("contacts", contacts);
		model.addAttribute("title", "Show contacts");

		return "normal/show_contacts";
	}

	// Showing particular contact details.
	@RequestMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal) {

		// Added Security to view contacts of loggedin user only.
		String username = principal.getName();
		User user = this.userRepository.getUserByUsername(username);

		// Get contact
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();

		model.addAttribute("title", "Contact details");

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}

		return "normal/contact_details";
	}

	// Delete Contact Handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, Principal principal, HttpSession session) {

		// Added Security to view contacts of loggedin user only.
		String username = principal.getName();
		User user = this.userRepository.getUserByUsername(username);

		// Get contact
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		Contact contact = contactOptional.get();

		if (user.getId() == contact.getUser().getId()) {
			try {
				// Remove photo from storage before deleting
				if (!contact.getImage().equals("defaultProfilePic.png")) {
					File deleteFile = new ClassPathResource("static/img/profilePic").getFile();

					File file1 = new File(deleteFile, contact.getImage());
					file1.delete();
				}
				this.contactRepository.delete(contact);
				// Massage success display
				session.setAttribute("message", new Message("Contact deleted successfully!!", "alert-success"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "redirect:/user/show-contacts/0";
	}

	// Open update form handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model model, Principal principal) {

		// Added Security to view contacts of loggedin user only.
		String username = principal.getName();
		User user = this.userRepository.getUserByUsername(username);

		// Get contact
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
		Contact contact = contactOptional.get();

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}

		model.addAttribute("title", "Contact details");

		return "normal/update_form";
	}

	// Update Contact
	@PostMapping(value = "/process-update")
	public String updateHandler(@Valid @ModelAttribute("contact") Contact contact, BindingResult result, 
			@RequestParam("profileImage") MultipartFile file,
			Model model, Principal principal, HttpSession session) {

		if(result.hasErrors()) {
			model.addAttribute("title", "Update contact");
			model.addAttribute("contact",contact);
			return "normal/update_form";
		}
		
		// Added Security to view contacts of loggedin user only.
		String username = principal.getName();
		User user = this.userRepository.getUserByUsername(username);

		// Fetching old contact Details for deleting old photo
		Contact oldContactDetails = this.contactRepository.findById(contact.getcId()).get();

		try {
			// image
			if (!file.isEmpty()) {
				// means new file
				// rewrite

				// 1. Delete Old photo.
				File deleteFile = new ClassPathResource("static/img/profilePic").getFile();

				File file1 = new File(deleteFile, oldContactDetails.getImage());
				file1.delete();

				// 2. Update new photo.

				// save the file to folder and update the name to contact
				File saveFile = new ClassPathResource("static/img/profilePic").getFile();

				Date date = new Date();
				Long time = date.getTime();
				String uniqueFileName = time.toString().concat(file.getOriginalFilename());

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + uniqueFileName);

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				contact.setImage(uniqueFileName);
			} else {
				contact.setImage(oldContactDetails.getImage());
			}

			contact.setUser(user);
			this.contactRepository.save(contact);

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Massage success display
		session.setAttribute("message", new Message("Contact updated successfully!!", "alert-success"));

		return "redirect:/user/" + contact.getcId() + "/contact";
	}

	// Your Profile Handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		// Data will retrive from @modelAttribute

		model.addAttribute("title", "profile");
		return "normal/profile";
	}
	
	//Open Settings handler
	@GetMapping("/settings")
	public String openSettings() {
		
		return "normal/settings";
	}
	
	//Change Password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal,
			HttpSession session) {
			
		
		String username = principal.getName();
		User user = this.userRepository.getUserByUsername(username);
		
		if(this.bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
			//true - change password
			
			user.setPassword(bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(user);
			
			// Massage success display
			session.setAttribute("message", new Message("Password change successfully!!", "alert-success"));
		}else {
			// Massage success display
			session.setAttribute("message", new Message("Please enter valid password!!", "alert-danger"));
		}
		
		return "normal/settings";
	}
	
}
