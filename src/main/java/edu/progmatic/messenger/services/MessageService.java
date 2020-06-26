package edu.progmatic.messenger.services;

import edu.progmatic.messenger.enums.Ordering;
import edu.progmatic.messenger.model.Message;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private List<Message> messages = new ArrayList<>();
    private int actualMessageID = 0;

    {
        messages.add(new Message("Lacika", LocalDateTime.of(2020, 6, 16, 10, 45, 22), "Szave Tesa! Mikor megyünk végre el a klubba?"));
        messages.add(new Message("Lilla", LocalDateTime.of(2020, 6, 16, 11, 12, 1), "Szia Szivi! Voltál vásárolni? Van otthon kenyér? Ha nem, szólj és hozok!"));
        messages.add(new Message("Lacika", LocalDateTime.of(2020, 6, 16, 13, 0, 58), "Na, mi van már, mé nem írsz?"));
        messages.add(new Message("Sári", LocalDateTime.of(2020, 6, 15, 13, 33, 51), "Béla keresett, hívd vissza!"));
        messages.add(new Message("Béla", LocalDateTime.of(2020, 6, 16, 6, 2, 8), "Helló Gergő! Tudsz nekem kölcsön adni?"));
        for (Message message : messages) {
            message.setId(actualMessageID++);
        }
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<Message> getMessageListBy(Integer limit, String orderBy, Ordering ordering) {
        List<Message> list;
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
        return list;
    }

    public void addMessage(Message message) {
        message.setId(actualMessageID++);
        messages.add(message);
    }

    public Message getMessageOf(int ID) throws EntityNotFoundException {
        for (Message message : messages) {
            if (message.getId() == ID && !message.isDeleted()) {
                return message;
            }
        }
        throw new EntityNotFoundException(ID);
    }

    public Message getLastMessage() {
        return messages.stream().max(Comparator.comparing(Message::getTime)).get();
    }

    public void deleteMessageOf(int id) {
        try {
            getMessageOf(id).setDeleted(true);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
