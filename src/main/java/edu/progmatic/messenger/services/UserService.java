package edu.progmatic.messenger.services;

import edu.progmatic.messenger.model.AppUser;
import edu.progmatic.messenger.model.Authority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class UserService implements UserDetailsService {
    private AppUser user;

    @PersistenceContext
    EntityManager em;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
       return em.createQuery("SELECT m FROM AppUser m JOIN FETCH m.authorities WHERE m.username = :name",AppUser.class)
               .setParameter("name",s)
               .getSingleResult();
    }

    public boolean isUsernameValid (String username) {
      return em.createQuery("SELECT m from AppUser m WHERE m.username = :toCheck", AppUser.class)
                .setParameter("toCheck",username)
                .getResultList()
                .size() == 1;
    }

    public boolean isPasswordValid (String username, String password) {
        user = em.createQuery("SELECT m from AppUser m WHERE m.username = :toCheck", AppUser.class)
                .setParameter("toCheck",username)
                .getSingleResult();
        return user.getPassword().equals(password);
    }

    @Transactional
    public void createUser (AppUser newUser){
        em.persist(newUser);
    }

    public boolean usernameOccupied (String user) {
        return em.createQuery("SELECT m from AppUser m WHERE m.username = :toCheck", AppUser.class)
                .setParameter("toCheck",user)
                .getResultList()
                .size() > 0;
    }

    public Authority getUserAuthority () {
       return (Authority) em.createQuery("Select a from Authority a WHERE a.name = 'ROLE_USER'")
               .getSingleResult();
    }

    public AppUser getUser() {
        return user;
    }
}
