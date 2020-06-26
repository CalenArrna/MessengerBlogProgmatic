package edu.progmatic.messenger.services;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private Map<String, User> userMap;

    public Map<String, User> getUserMap() {
        return userMap;
    }

    public void setUserMap(User user) {
        if (userMap.containsKey(user.getUsername())) {

        }else {
            userMap.put(user.getUsername(),user);
        }
    }
}
