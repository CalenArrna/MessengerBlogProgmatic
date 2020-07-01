package edu.progmatic.messenger.services;

import edu.progmatic.messenger.enums.Ordering;
import edu.progmatic.messenger.model.Message;
import edu.progmatic.messenger.model.Topic;
import org.hibernate.query.criteria.internal.expression.function.AggregationFunction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private List<Message> messages = new ArrayList<>();
    private int actualMessageID = 0;

    @PersistenceContext
    EntityManager em;


    public List<Message> getMessages() {
        return messages;
    }

    @Transactional
    public List<Message> getMessageListBy(Integer topicID, Integer limit, String orderBy, String ordering) {
        boolean isAdmin = false;
        Optional<?> grantedAuthority = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().findFirst();
        if (grantedAuthority.isPresent()) {
            isAdmin = grantedAuthority.get().toString().contains("ROLE_ADMIN");
        }

        String ord = null;
        String asc = "ASC";
        if (orderBy == null) orderBy = "id";
        if (ordering == null) ordering = "ASC";
        switch (orderBy) {
            case "from":
                orderBy = "m.from";
                break;
            case "time":
                orderBy = "m.time";
                break;
            default:
                orderBy = "m.id";
        }
        if (ordering.equals("DSC")) ordering = "DESC";
        if (limit <= 0) limit = 15;
        if (isAdmin) {
            return em.createQuery("SELECT m FROM Message m WHERE m.topic.topicID = :tId ORDER BY " + orderBy + " " + ordering, Message.class)
                    .setParameter("tId", topicID)
                    .setMaxResults(limit)
                    .getResultList();
        } else {
            return em.createQuery("SELECT m FROM Message m WHERE m.topic.topicID = :tId AND m.deleted = false ORDER BY " + orderBy + " " + ordering, Message.class)
                    .setParameter("tId", topicID)
                    .setMaxResults(limit)
                    .getResultList();
        }


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
        return em.createQuery("SELECT t from Topic t", Topic.class).getResultList();
    }

    @Transactional
    public Topic getTopicBy(int ID) {
        return em.find(Topic.class, ID);
    }

    @Transactional
    public Message getLastMessage() {
        return em.createQuery("select m from Message m WHERE m.deleted = false order by m.time desc ", Message.class)
                .setMaxResults(1)
                .getSingleResult();
    }

    @Transactional
    public void deleteMessageOf(int id) {
        em.find(Message.class, id).setDeleted(true);

    }
}
