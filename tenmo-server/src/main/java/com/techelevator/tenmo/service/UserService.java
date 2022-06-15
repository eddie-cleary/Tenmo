package com.techelevator.tenmo.service;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

public class UserService {

    private UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public BigDecimal getUserBalance(Principal principal) {
        // Return balance based on principal's username
        return userDao.findBalanceByUserId(userDao.findIdByUsername(principal.getName()));
    }

    public User findUserByAccountId(Long id) {
        return userDao.findUserByAccountId(id);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User findUserByUserId(Long userId) {
        return userDao.findUserByUserId(userId);
    }
}
