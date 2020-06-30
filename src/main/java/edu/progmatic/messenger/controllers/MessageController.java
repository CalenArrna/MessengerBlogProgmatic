package edu.progmatic.messenger.controllers;

import edu.progmatic.messenger.enums.Ordering;
import edu.progmatic.messenger.model.Message;
import edu.progmatic.messenger.model.Topic;
import edu.progmatic.messenger.services.EntityNotFoundException;
import edu.progmatic.messenger.services.MessageService;
import edu.progmatic.messenger.session.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
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
    public String showNewMessage(Message message, Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("topics", messageService.getTopicList());
        userInfo.setName(user.getUsername());
        message.setFrom(userInfo.getName());
        message.setTopic(new Topic());
        return "newMessage";
    }


    @PostMapping(path = "/newMessage")
    public String createAMessage(@Valid @ModelAttribute("message") Message message, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/newMessage";
        } else {
            message.setTopic(messageService.getTopicBy(message.getTopic().getTopicID()));
            userInfo.setName(message.getFrom());
            messageService.createMessage(message);
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

