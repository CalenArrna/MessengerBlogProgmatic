package edu.progmatic.messenger.controllers;

import edu.progmatic.messenger.enums.Ordering;
import edu.progmatic.messenger.model.Message;
import edu.progmatic.messenger.services.EntityNotFoundException;
import edu.progmatic.messenger.services.MessageService;
import edu.progmatic.messenger.session.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Controller
public class MessageController {
    private MessageService messageService;
    private UserInfo userInfo;

    @Autowired
    public MessageController(MessageService messageService, UserInfo userInfo) {
        this.messageService = messageService;
        this.userInfo = userInfo;
    }


    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    public String messages(@RequestParam(name = "limit", required = false, defaultValue = "-1") Integer limit,
                           @RequestParam(name = "orderBy", required = false) String orderBy,
                           @RequestParam(name = "ordering", required = false) Ordering ordering,
                           Model model) {
        List<Message> list = messageService.getMessageListBy(limit, orderBy, ordering);
        model.addAttribute("messages", list);
        return "messages";
    }

    @RequestMapping(value = "/messageDetails/{id}", method = RequestMethod.GET)
    public String showOneMessage(
            @PathVariable("id") int msgID, Model model) throws EntityNotFoundException {
        model.addAttribute("message", messageService.getMessageOf(msgID));
        return "messageDetails";
    }


    @RequestMapping(value = "/newMessage", method = RequestMethod.GET)
    public String showNewMessage(Message message) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userInfo.setName(user.getUsername());
        message.setFrom(userInfo.getName());
        return "newMessage";
    }


    @PostMapping(path = "/newMessage")
    public String createAMessage(@Valid @ModelAttribute("message") Message message, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/newMessage";
        } else {
            userInfo.setName(message.getFrom());
            messageService.addMessage(message);
            return "redirect:/messages";
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteAMessage(@PathVariable("id") int id) {
        messageService.deleteMessageOf(id);
        return "redirect:/messages";
    }
}

