package edu.progmatic.messenger.controllers;

import edu.progmatic.messenger.model.Message;
import edu.progmatic.messenger.model.Topic;
import edu.progmatic.messenger.services.MessageService;
import edu.progmatic.messenger.session.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.management.DynamicMBean;
import javax.validation.Valid;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RestController
public class RestMessageController {
    private MessageService messageService;
    private List<Topic> topicList;

    @Autowired
    public RestMessageController(MessageService messageService, UserInfo userInfo) {
        this.messageService = messageService;
        topicList = messageService.getTopicList();
    }

    @RequestMapping(value = "rest/messages", method = RequestMethod.GET)
    public List<Message> messages(@RequestParam(name = "topic", required = false) Integer topic,
                           @RequestParam(name = "limit", required = false, defaultValue = "0") Integer limit,
                           @RequestParam(name = "orderBy", required = false) String orderBy,
                           @RequestParam(name = "ordering", required = false) String ordering,
                           @RequestParam(name = "text", required = false) String text,
                           @RequestParam(name = "sender", required = false) String sender,
                           @RequestParam(name = "date", required = false) String stringDate) throws ParseException {
        LocalDateTime localDate = null;
        if (stringDate != null && !stringDate.isBlank()) {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
            Date date = formatter.parse(stringDate);
            localDate = date
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }

       return messageService.getMessageListBy(topic, limit, orderBy, ordering, sender, text, localDate, topicList);
    }

    @PostMapping(path = "rest/newMessage")
    public void createAMessage(@RequestBody Message message) {
            message.setTopic(messageService.getTopicBy(message.getTopic().getTopicID()));
            messageService.createMessage(message);
    }

    @GetMapping(value = "rest/CSRF")
    public CsrfToken getCSFRtoken (CsrfToken token) {
        return token;
    }

}
