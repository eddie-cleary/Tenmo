package com.techelevator.tenmo.service;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public UserService() {}

    public BigDecimal getUserBalance(Principal principal) {
        // Return balance based on principal's username
        return userDao.findBalanceByUserId(userDao.findIdByUsername(principal.getName()));
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public User findUserByUserId(Long userId) {
        return userDao.findUserByUserId(userId);
    }
}
