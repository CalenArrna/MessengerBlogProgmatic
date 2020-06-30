package edu.progmatic.messenger.model;
import edu.progmatic.messenger.formatting.Style;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "MESSAGES")
public class Message {

    @Id
    @GeneratedValue
    private int id;

    @Column(name="FROMCOL")
    @NotNull
    @Size(min=2, max=30)
    private String from;

    @Column(name="TIME")
    @DateTimeFormat(pattern = Style.date)
    private LocalDateTime time;

    @Column(name="TEXT")
    @NotNull
    @Size(min=2, max=300)
    private String text;

    @ManyToOne
    @NotNull
    private Topic topic;

    @Column(name="ISDELETED")
    private boolean deleted;


    //String uniqueID = UUID.randomUUID().toString(); // FOR DATABASE STYLE GENERATED RANDOM UNIQUE ID

    public Message() {
        time = LocalDateTime.now();
        this.deleted = false;
    }

    public Message(String from, LocalDateTime time, String text, Topic topic) {
        this.from = from;
        this.time = time;
        this.topic = topic;
        this.text = text;
        this.deleted = false;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}
