package edu.progmatic.messenger.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;



@ControllerAdvice
public class MyControllerAdvice {

    static Logger logger = LoggerFactory.getLogger(MyControllerAdvice.class);


    @ExceptionHandler(Exception.class)
    public String handleErrors(Exception ex, Model model){
        logger.error("Error", ex);
        model.addAttribute("exceptionMessage", ex.getMessage());
        return "error";
    }
}
