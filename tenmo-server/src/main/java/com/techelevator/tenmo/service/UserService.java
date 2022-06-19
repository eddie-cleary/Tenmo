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

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    // Return balance based on current logged in user
    public BigDecimal getUserBalance(Principal principal) {
        return userDao.findBalanceByUserId(userDao.findIdByUsername(principal.getName()));
    }

    // Returns all users in system, will hide password from jsonignore annotation in User model
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    // Returns a user based on user id
    public User findUserByUserId(Long userId) {
        return userDao.findUserByUserId(userId);
    }

    // Returns a user based on account id
    public User findUserByAccountId(Long accountId) {
        return userDao.findUserByAccountId(accountId);
    }

    // Returns an account id based on a user id
    public Long findAccountIdByUserId(Long userId) { return userDao.findAccountIdByUserId(userId);}
}
