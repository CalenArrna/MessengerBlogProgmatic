package edu.progmatic.messenger.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserData {
    @NotNull
    @Size(min = 4, max = 20)
    private String username;
    @NotNull
    @Size(min = 8, max = 16)
    private String password;

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
}
