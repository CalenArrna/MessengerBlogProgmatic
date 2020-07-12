package edu.progmatic.messenger.services;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import edu.progmatic.messenger.model.Message;
import edu.progmatic.messenger.model.QMessage;
import edu.progmatic.messenger.model.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    private List<Message> messages = new ArrayList<>();
    private JPAQueryFactory queryFactory;

    @PersistenceContext
    EntityManager em;

    @Autowired
    public MessageService() {
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<Message> getMessages() {
        return messages;
    }

    @Transactional
    public List<Message> getMessageListBy(Integer topicID, Integer limit, String orderBy, String ordering,
                                          String sender, String text, LocalDateTime localDateTime, List<Topic> topics) {
        boolean isAdmin = false;
        Optional<?> grantedAuthority = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().findFirst();
        if (grantedAuthority.isPresent()) {
            isAdmin = grantedAuthority.get().toString().contains("ROLE_ADMIN");
        }
        if (ordering == null) ordering = "ASC";
        Order order = ordering.equals("DSC") ? Order.DESC : Order.ASC;

        if (topicID == null) topicID = topics.get(0).getTopicID();

        if (orderBy == null) orderBy = "id";

        BooleanBuilder whereCondition = new BooleanBuilder();

        whereCondition.and(QMessage.message.topic.topicID.eq(topicID));

        if (!isAdmin) whereCondition.and(QMessage.message.deleted.isFalse());

        if (sender != null) whereCondition.and(QMessage.message.from.like("%"+sender+"%"));

        if (text != null) whereCondition.and(QMessage.message.text.like("%"+text+"%"));

        if (localDateTime != null) whereCondition.and(QMessage.message.time.after(localDateTime));

        switch (orderBy) {
            case "from":
                orderBy = "from";
                break;
            case "time":
                orderBy = "time";
                break;
            default:
                orderBy = "id";
        }

        queryFactory =  new JPAQueryFactory(em);
        if (limit > 0) {
            return queryFactory.selectFrom(QMessage.message)
                    .where(whereCondition)
                    .orderBy(getOrderBy(order,orderBy))
                    .limit(limit)
                    .fetch();
        }else {
            return queryFactory.selectFrom(QMessage.message)
                    .where(whereCondition)
                    .orderBy(getOrderBy(order,orderBy))
                    .fetch();
        }

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
    public void deleteTopicWithItsMessages (int id) {
        Topic topicToRemove = em.find(Topic.class, id);
        em.remove(topicToRemove);
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

    private OrderSpecifier<?> getOrderBy(Order order, String field){
        Path<Object> fieldPath = Expressions.path(Object.class,QMessage.message,field);
        return new OrderSpecifier(order,fieldPath);
    }
}
