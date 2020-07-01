package edu.progmatic.messenger.controllers;

import edu.progmatic.messenger.model.AppUser;
import edu.progmatic.messenger.model.Message;
import edu.progmatic.messenger.model.RegDTOSimple;
import edu.progmatic.messenger.model.UserData;
import edu.progmatic.messenger.services.UserService;
import edu.progmatic.messenger.session.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.HashSet;

@Controller
public class UserController {
    private UserInfo userInfo;
    private UserService service;
    private UserDetailsManager manager;
    private HashSet<String> usernameList;

    @Autowired
    public UserController(UserInfo userInfo, UserService service, UserDetailsService userDetailsService) {
        this.userInfo = userInfo;
        this.service = service;
        this.manager = (UserDetailsManager) userDetailsService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showLogin() {
        return "login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegister(@ModelAttribute("newUser") RegDTOSimple newUser) {
        return "register";
    }
    

    @PostMapping(path = "/register")
    public String registerUser(@Valid @ModelAttribute("newUser") RegDTOSimple newUser, BindingResult bindingResult) {
        if (!newUser.getPassword().equals(newUser.getPassConf())){
            bindingResult.addError(new FieldError("newUser","password","A jelszavak nem egyeznek!"));
            bindingResult.addError(new FieldError("newUser","passConf","A jelszavak nem egyeznek!"));
        }
//        if(manager.userExists(newUser.getUsername()))
//            bindingResult.addError(new FieldError("newUser","username","Van már ilyen felhasználó!"));
        if(usernameList.contains(newUser.getUsername()))
            bindingResult.addError(new FieldError("newUser","username","Van már ilyen felhasználó!"));
        if (bindingResult.hasErrors()) {
            return "register";
        } else {
            AppUser appUser = new AppUser(newUser.getUsername(),newUser.getPassword());
            service.createUser(appUser);
            //manager.createUser(User.withUsername(newUser.getUsername()).password(newUser.getPassword()).roles("USER").build());
            return "redirect:/login";
        }
    }
}
