package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {

    private UserDao userDao;

    public UsersController(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping
    public List getAllUsers() {
        return userDao.findAll();
    }
}
