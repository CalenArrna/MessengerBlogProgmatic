package edu.progmatic.messenger.controllers;

import edu.progmatic.messenger.model.AppUser;
import edu.progmatic.messenger.model.RegDTOSimple;
import edu.progmatic.messenger.services.UserService;
import edu.progmatic.messenger.session.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Controller
public class UserController {
    private UserInfo userInfo;
    private UserService service;
    private PasswordEncoder encoder;

    @Autowired
    public UserController(UserInfo userInfo, UserService service, PasswordEncoder enc) {
        this.userInfo = userInfo;
        this.service = service;
        this.encoder = enc;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showLogin() {
        return "login";
    }

//    @PostMapping(value = "/login")
//    public String loginUser(@RequestParam(name = "userName") String username,
//                            @RequestParam(name = "password") String password,
//                            BindingResult bindingResult) {
//        if (service.isUsernameValid(username)) {
//            if (!service.isPasswordValid(username, password)) {
//                bindingResult.addError(new FieldError("password", "password", "Hibás jelszó!"));
//            }
//        } else {
//            bindingResult.addError(new FieldError("userName", "username", "Hibás felhasználónév!"));
//        }
//        if (bindingResult.hasErrors()) {
//            return "login";
//        } else {
//            service.loadUserByUsername(username);
//            return "home";
//        }
//    }


    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegister(@ModelAttribute("newUser") RegDTOSimple newUser) {
        return "register";
    }


    @PostMapping(path = "/register")
    public String registerUser(@Valid @ModelAttribute("newUser") RegDTOSimple newUser, BindingResult bindingResult) {
        if (!newUser.getPassword().equals(newUser.getPassConf())) {
            bindingResult.addError(new FieldError("newUser", "password", "A jelszavak nem egyeznek!"));
            bindingResult.addError(new FieldError("newUser", "passConf", "A jelszavak nem egyeznek!"));
        }
        if (service.usernameOccupied(newUser.getUsername()))
            bindingResult.addError(new FieldError("newUser", "username", "Van már ilyen felhasználó!"));
        if (bindingResult.hasErrors()) {
            return "register";
        } else {
            AppUser appUser = new AppUser(newUser.getUsername(), encoder.encode(newUser.getPassword()));
            appUser.setAuthorities(service.getUserAuthority());
            service.createUser(appUser);
            //manager.createUser(User.withUsername(newUser.getUsername()).password(newUser.getPassword()).roles("USER").build());
            return "redirect:/login";
        }
    }
}
