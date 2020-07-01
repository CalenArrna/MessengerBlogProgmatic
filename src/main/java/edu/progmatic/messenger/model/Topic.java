package edu.progmatic.messenger.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class Topic {

    @Id
    @GeneratedValue
    private int topicID;

    @Column
    @NotNull
    @Size(min = 2, max = 30)
    private String title;

    @Column
    @NotNull
    @Size(min = 4, max = 300)
    private String description;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "topic")
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
