package edu.progmatic.messenger.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class Topic {

    @Id
    @GeneratedValue
    private int topicID;

    @Column
    private String title;

    @Column
    private String description;

    @OneToMany(mappedBy = "topic")
    private List<Message> messages;

    public Topic() {
    }

    public Topic(String title, String description, List<Message> messages) {
        this.title = title;
        this.description = description;
        this.messages = messages;
    }

    public int getTopicID() {
        return topicID;
    }

    public void setTopicID(int topicID) {
        this.topicID = topicID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
