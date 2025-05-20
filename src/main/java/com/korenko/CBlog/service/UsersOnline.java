package com.korenko.CBlog.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UsersOnline {
    private final Set<String> usersOnline = ConcurrentHashMap.newKeySet();

    public void addUser(String username) {
        usersOnline.add(username);
    }

    public void removeUser(String username) {
        usersOnline.remove(username);
    }

    public List<String> getUsersOnline() {
        return new ArrayList<>(usersOnline);
    }
}
