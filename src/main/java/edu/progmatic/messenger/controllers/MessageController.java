package edu.progmatic.messenger.controllers;

import edu.progmatic.messenger.model.Message;
import edu.progmatic.messenger.model.Topic;
import edu.progmatic.messenger.services.EntityNotFoundException;
import edu.progmatic.messenger.services.MessageService;
import edu.progmatic.messenger.session.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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


/*
ELSŐDLEGES:
OAuth
        Leírás
        Alakítsd át az üzenetküldő regisztrációs mechanizmusát úgy, hogy Gmail accountal (is) lehessen használni az üzenetküldőt.
        A regisztráció alatt jelenjen meg egy link, hogy Gmail-el használom, ha a felhasználó erre kattint, akkor az OAuth
        protokoll segítségével irányítsuk át a Gmail megfelelő felületére, ahol a Gmail-be bejelentkezve jóvá hagyhatja
        az üzenetküldő alkalmazás hozzáférését.
        Nehézség:  **

Dozer
        Leírás
        Használjuk a Dozer keretrendszert a JPA entitások és a DTO-k közötti mappaléshez!
        Nehézség: *

HA LESZ MÉG IDŐ:

Heroku
        Leírás
        Telepítsd az üzenetküldős alkalmazást Heroku-ra. A Heroku egy felhő alapú “platform as a service”, vagyis olyan hely,
        ahová lehet mindenféle alkalmazást telepíteni, többek között java-st is. A regisztráció ingyenes, és néhány alkalmazást
        lehet az ingyen regisztrációval telepíteni.
        A telepítéshez kell egy kicsit maszírozni az alkalmazás konfig fájljait (a herokun pl. egy herokus db-t fog majd elérni),
        és a gépedre telepíteni kell a Heroku CLI-t (command line interface-t).
        Ha sikeres a telepítés, akkor az alkalmazás elérhető lesz az internetről valami olyasmi url-en hogy mymessagenerapp.heroku.com.
        Nehézség: **

*/



//TODO : Fragment NAVBAR
//TODO : Get the Oicd User before get in the security context ??? Could the conversion to user get done with DOZER?   Login post Oauth2? google

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
        LocalDateTime localDate = null;
        if (stringDate != null && !stringDate.isBlank()) {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
            Date date = formatter.parse(stringDate);
            localDate = date
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }

        List<Message> list = messageService.getMessageListBy(topic, limit, orderBy, ordering, sender, text, localDate, topicList);
        model.addAttribute("messages", list);
        model.addAttribute("topics", topicList);
        model.addAttribute("username",UserController.getUsername());
        return "messages";
    }

    @RequestMapping(value = "/messageDetails/{id}", method = RequestMethod.GET)
    public String showOneMessage(
            @PathVariable("id") int msgID, Model model) throws EntityNotFoundException {
        model.addAttribute("message", messageService.getMessageOf(msgID));
        model.addAttribute("username",UserController.getUsername());
        return "messageDetails";
    }


    @RequestMapping(value = "/newMessage", method = RequestMethod.GET)
    public String showNewMessage(Message message, Model model) {
        model.addAttribute("topics", topicList);
        message.setFrom(UserController.getUsername()); //TODO get username from spring
        message.setTopic(new Topic());
        model.addAttribute("username",UserController.getUsername());
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

    @GetMapping(value = "/deleteTopic")
    @PreAuthorize("hasRole('ADMIN')")
    public String showDeleteTopic(Model model) {
        model.addAttribute("topics", topicList);
        model.addAttribute("username",UserController.getUsername());
        return "deleteTopic";
    }

    @PostMapping(value = "/deleteTopic")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteTopic(@RequestParam(name = "topic", required = true) int topicID) {
        messageService.deleteTopicWithItsMessages(topicID);
        refreshTopicList();
        return "redirect:/messages";
    }

    @GetMapping(value = "/createTopic")
    public String showCreateTopic(Topic topic, Model model) {
        model.addAttribute("username",UserController.getUsername());
        return "createTopic";
    }

    @PostMapping(path = "/createTopic")
    public String createNewTopic(@Valid @ModelAttribute("topic") Topic topic, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "createTopic";
        } else {
            messageService.createTopic(topic);
            refreshTopicList();
            return "redirect:/home";
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteAMessage(@PathVariable("id") int id, Model model) {
        model.addAttribute("username",UserController.getUsername());
        messageService.deleteMessageOf(id);
        return "redirect:/messages";
    }

    private void refreshTopicList() { //TODO: put in create topic function
        topicList = messageService.getTopicList();
    }
}

