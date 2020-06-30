package edu.progmatic.messenger.services;

import edu.progmatic.messenger.enums.Ordering;
import edu.progmatic.messenger.model.Message;
import edu.progmatic.messenger.model.Topic;
import org.hibernate.query.criteria.internal.expression.function.AggregationFunction;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private List<Message> messages = new ArrayList<>();
    private int actualMessageID = 0;

    @PersistenceContext
    EntityManager em;



/*    {
        em.persist(new Message("Lacika", LocalDateTime.of(2020, 6, 16, 10, 45, 22), "Szave Tesa! Mikor megyünk végre el a klubba?"));
        em.persist(new Message("Lilla", LocalDateTime.of(2020, 6, 16, 11, 12, 1), "Szia Szivi! Voltál vásárolni? Van otthon kenyér? Ha nem, szólj és hozok!"));
        em.persist(new Message("Lacika", LocalDateTime.of(2020, 6, 16, 13, 0, 58), "Na, mi van már, mé nem írsz?"));
        em.persist(new Message("Sári", LocalDateTime.of(2020, 6, 15, 13, 33, 51), "Béla keresett, hívd vissza!"));
        em.persist(new Message("Béla", LocalDateTime.of(2020, 6, 16, 6, 2, 8), "Helló Gergő! Tudsz nekem kölcsön adni?"));
//        for (Message message : messages) {
//            message.setId(actualMessageID++);
//        }
    }*/

    public List<Message> getMessages() {
        return messages;
    }

    @Transactional
    public List<Message> getMessageListBy(Integer limit, String orderBy, Ordering ordering) {
        return em.createQuery("SELECT m FROM Message m", Message.class).getResultList();
        /*List<Message> list;
        if (limit == -1) limit = messages.size();
        if (ordering == null) ordering = Ordering.ASC;
        if (orderBy != null) {
            Comparator<Message> comp;
            switch (orderBy) {
                case "from":
                    comp = (Message o1, Message o2) -> o1.getFrom().compareTo(o2.getFrom());
                    break;
                case "time":
                    comp = Comparator.comparing(Message::getTime);
                    break;
                default:
                    comp = Comparator.comparing(Message::getId);
                    break;
            }
            if (ordering == Ordering.DSC) {
                comp = comp.reversed();
            }

            list = messages.stream()
                    .filter(message -> !message.isDeleted())
                    .sorted(comp)
                    .limit(limit)
                    .collect(Collectors.toList());
        } else {
            list = messages.stream()
                    .filter(message -> !message.isDeleted())
                    .limit(limit)
                    .collect(Collectors.toList());
        }
        return list;*/
    }

    @Transactional
    public void createMessage(Message message) {
       // message.getTopic().setMessages(new ArrayList<>());
       // message.getTopic().getMessages().add(message);
      //  em.persist(message.getTopic());
        em.persist(message);
    }

    @Transactional
    public void createTopic(Topic topic) {
        em.persist(topic);
    }

    @Transactional
    public Message getMessageOf(int ID) {
        return em.find(Message.class, ID);
    }

    @Transactional
    public List<Topic> getTopicList() {
        return em.createQuery("SELECT t from Topic t",Topic.class).getResultList();
    }

    @Transactional
    public Topic getTopicBy(int ID) {
        return em.find(Topic.class,ID);
    }
    @Transactional
    public Message getLastMessage() {
        return em.createQuery("select m from Message m", Message.class).getResultStream()
                .max(Comparator.comparing(Message::getTime))
                .get();
    }


    public void deleteMessageOf(int id) {
        try {
            getMessageOf(id).setDeleted(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
