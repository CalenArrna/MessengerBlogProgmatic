package edu.progmatic.messenger.controllers;

import edu.progmatic.messenger.model.AppUser;
import edu.progmatic.messenger.model.Message;
import edu.progmatic.messenger.model.RegDTOSimple;
import edu.progmatic.messenger.services.MessageService;
import edu.progmatic.messenger.services.UserService;
import edu.progmatic.messenger.session.UserInfo;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {
    private MessageService messageService;
    private UserInfo userInfo;
    private UserService service;
    private PasswordEncoder encoder;
    private static String username;
    private DozerBeanMapper mapper;
    private static String authorizationRequestBaseUri
            = "oauth2/authorization";
    Map<String, String> oauth2AuthenticationUrls
            = new HashMap<>();

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    public UserController(UserInfo userInfo, UserService service, PasswordEncoder enc, MessageService messageService, DozerBeanMapper mapper) {
        this.userInfo = userInfo;
        this.service = service;
        this.encoder = enc;
        this.messageService = messageService;
        this.mapper = mapper;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showLogin(Model model) {
        Iterable<ClientRegistration> clientRegistrations = null;
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
                .as(Iterable.class);
        if (type != ResolvableType.NONE &&
                ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        }

        clientRegistrations.forEach(registration ->
                oauth2AuthenticationUrls.put(registration.getClientName(),
                        authorizationRequestBaseUri + "/" + registration.getRegistrationId()));
        model.addAttribute("urls", oauth2AuthenticationUrls);
        return "login";
    }

    @RequestMapping(value = "/loginsuccess", method = RequestMethod.GET)
    public String redirectHome (Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof OidcUser) {
            OidcUser principal = ((OidcUser) authentication.getPrincipal());
            username =principal.getFullName();
        }else {
            username = authentication.getName();
        }
        Message lastMessage = messageService.getLastMessage();
        model.addAttribute("message",lastMessage);
        model.addAttribute("username", username);
        return "/home";
    }

    public static String getUsername() {
        return username;
    }

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
            newUser.setPassword(encoder.encode(newUser.getPassword()));
            AppUser appUser = mapper.map(newUser, AppUser.class);
            appUser.setAuthorities(service.getUserAuthority());
            service.createUser(appUser);
            //manager.createUser(User.withUsername(newUser.getUsername()).password(newUser.getPassword()).roles("USER").build());
            return "redirect:/login";
        }
    }
}
