package edu.progmatic.messenger.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
public class AppUser {

    @Id
    @GeneratedValue
    private int id;

    @Column(name="Username")
    @NotNull
    @Size(min=2, max=30)
    private String username;

    @Column(name="Password")
    private String password;

    @Column(name="Emailaddress") //TODO: create a profil HTML
    @NotNull
    @Size(min=2, max=300)
    private String email;

    @Column(name="RealName")
    @Size(min=2, max=30)
    private String realName;

    public AppUser() {
    }

    public AppUser(@NotNull @Size(min = 2, max = 30) String username, String password) {
        this.username = username;
        this.password = password;
    }

    public AppUser(String username, String password, String email, String realName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.realName = realName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
