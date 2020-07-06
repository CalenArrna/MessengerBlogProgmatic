package edu.progmatic.messenger.controllers;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

//TODO: Ask about username positioning!?
//TODO: Why my querry doesn't work after deleted 'Alap' topic... default listing, where?

@Controller
public class MessageController {
    private MessageService messageService;
    private List<Topic> topicList;

    @Autowired
    public MessageController(MessageService messageService, UserInfo userInfo) {
        this.messageService = messageService;
        topicList = messageService.getTopicList();
    }

    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    public String messages(@RequestParam(name = "topic", required = false) Integer topic,
                           @RequestParam(name = "limit", required = false, defaultValue = "0") Integer limit,
                           @RequestParam(name = "orderBy", required = false) String orderBy,
                           @RequestParam(name = "ordering", required = false) String ordering,
                           @RequestParam(name = "text", required = false) String text,
                           @RequestParam(name = "sender", required = false) String sender,
                           @RequestParam(name = "date", required = false) String stringDate,
                           Model model) throws ParseException {

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
        Date date = formatter.parse(stringDate);
        LocalDateTime localDate = date
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        List<Message> list = messageService.getMessageListBy(topic,limit, orderBy, ordering,sender,text,localDate,topicList);
        model.addAttribute("messages", list);
        model.addAttribute("topics", topicList);
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
     //   User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("topics", topicList);
//        userInfo.setName(user.getUsername());
     //   message.setFrom(user.getUsername()); //TODO get username from spring
        message.setTopic(new Topic());
        return "newMessage";
    }


    @PostMapping(path = "/newMessage")
    public String createAMessage(@Valid @ModelAttribute("message") Message message, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "newMessage";
        } else {
            message.setTopic(messageService.getTopicBy(message.getTopic().getTopicID()));
            messageService.createMessage(message);
            return "redirect:/messages";
        }
    }

    @GetMapping (value = "/deleteTopic")
    @PreAuthorize("hasRole('ADMIN')")
    public String showDeleteTopic (Model model) {
        model.addAttribute("topics",topicList);
        return "deleteTopic";
    }

    @PostMapping (value = "/deleteTopic")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteTopic (@RequestParam(name = "topic", required = true) int topicID) {
        messageService.deleteTopicWithItsMessages(topicID);
        refreshTopicList();
        return "redirect:/messages";
    }

    @GetMapping(value = "/createTopic")
    public String showCreateTopic(Topic topic) {
        return "createTopic";
    }

    @PostMapping(path = "/createTopic")
    public String createNewTopic (@Valid @ModelAttribute("topic") Topic topic, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "createTopic";
        }else {
            messageService.createTopic(topic);
            refreshTopicList();
            return "redirect:/home";
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteAMessage(@PathVariable("id") int id) {
        messageService.deleteMessageOf(id);
        return "redirect:/messages";
    }

    private void refreshTopicList() { //TODO: put in create topic function
        topicList = messageService.getTopicList();
    }
}

