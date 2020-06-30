package edu.progmatic.messenger.controllers;

import edu.progmatic.messenger.model.Message;
import edu.progmatic.messenger.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class HomeController {
    MessageService messageService;

    @Autowired
    public HomeController(MessageService messageService) {
        this.messageService = messageService;
    }

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String homePage(Model model){
        Message lastMessage = messageService.getLastMessage();
        model.addAttribute("message",lastMessage);
        return "home";
    }
}
