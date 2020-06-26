package edu.progmatic.messenger.controllers;

import edu.progmatic.messenger.model.Message;
import edu.progmatic.messenger.services.EntityNotFoundException;
import edu.progmatic.messenger.services.MessageService;
import edu.progmatic.messenger.session.UserInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.Provider;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = MessageController.class)
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private UserInfo userInfo;

    @Test
    void messages() throws Exception {
        List<Message> msgs = new ArrayList<>();
        msgs.add(new Message("Béla", LocalDateTime.now(), "Én vagyok az utolsó"));
        Mockito.when(messageService.getMessageListBy(-1,null,null)).thenReturn(msgs);
        mockMvc.perform(MockMvcRequestBuilders.get("/messages"))
                .andExpect(MockMvcResultMatchers.view().name("messages"))
                .andExpect(MockMvcResultMatchers.model().attribute("messages", msgs));
    }

    @Test
    void showOneMessage() throws EntityNotFoundException, Exception {
        Message msg =new Message("Béla", LocalDateTime.now(), "Én vagyok az utolsó");
        Mockito.when(messageService.getMessageOf(0)).thenReturn(msg);
        mockMvc.perform(MockMvcRequestBuilders.get("/messageDetails/0"))
                .andExpect(MockMvcResultMatchers.view().name("messageDetails"))
                .andExpect(MockMvcResultMatchers.model().attribute("message", msg));

    }

    @Test
    @WithUserDetails("user")
    void showNewMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/newMessage"))
                .andExpect(MockMvcResultMatchers.view().name("newMessage"));

    }

    @Test
    void showNewMessageAsGuest() throws Exception { //TODO: Missing autentication in newMessage
        mockMvc.perform(MockMvcRequestBuilders.get("/newMessage"))
                .andExpect(MockMvcResultMatchers.view().name("login"));

    }

    @Test
    void createAMessage() {
    }

    @Test
    void deleteAMessage() {
    }
}