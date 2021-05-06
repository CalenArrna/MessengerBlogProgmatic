package edu.progmatic.messenger.model;

import edu.progmatic.messenger.formatting.Style;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "AUTHORITY")
public class Authority implements GrantedAuthority {

    @Id
    @GeneratedValue
    private int id;

    @Column(name = "NAME")
    @NotNull
    @Size(min = 2, max = 50)
    private String name;

    @ManyToMany(mappedBy = "authorities")
    private Set<AppUser> users = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<AppUser> getUsers() {
        return users;
    }

    public void setUsers(Set<AppUser> users) {
        this.users = users;
    }

    @Override
    public String getAuthority() {
        return name;
    }
}