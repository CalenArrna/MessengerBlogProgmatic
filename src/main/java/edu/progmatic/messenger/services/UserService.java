package edu.progmatic.messenger.services;


import edu.progmatic.messenger.model.AppUser;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Set;


@Service
public class UserService {
    @PersistenceContext
    EntityManager em;

    public void createUser (AppUser newUser){
        em.persist(newUser);
    }

//    public Set<String> getUsernames () {
//        em.q
//    }
}
