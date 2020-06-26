package edu.progmatic.messenger.controllers;

import edu.progmatic.messenger.model.Message;
import edu.progmatic.messenger.services.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @Test
    @WithUserDetails("user")
    void homePage() throws Exception {
        Message msg = new Message("Béla", LocalDateTime.now(), "Én vagyok az utolsó");
        Mockito.when(messageService.getLastMessage()).thenReturn(msg);
        mockMvc.perform(MockMvcRequestBuilders.get("/greeting"))
                .andExpect(MockMvcResultMatchers.view().name("home"))
                .andExpect(MockMvcResultMatchers.model().attribute("message", msg));
    }
}