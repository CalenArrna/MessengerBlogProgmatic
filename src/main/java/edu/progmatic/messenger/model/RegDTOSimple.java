package edu.progmatic.messenger.model;

import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RegDTOSimple {
    @NotNull
    @Size(min = 4, max = 20)
    private String username;

    @NotNull
    @Size(min = 8, max = 16)
    private String password;

    @NotNull
    @Size(min = 8, max = 16)
    private String passConf;

    @Autowired
    public RegDTOSimple(String username, String password, String passConf) {
        this.username = username;
        this.password = password;
        this.passConf = passConf;
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

    public String getPassConf() {
        return passConf;
    }

    public void setPassConf(String passConf) {
        this.passConf = passConf;
    }
}
