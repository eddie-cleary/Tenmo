package com.techelevator.tenmo.service;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

public interface UserService {

    // Return balance based on current logged in user
    public BigDecimal getUserBalance(Principal principal);

    // Returns all users in system, will hide password from jsonignore annotation in User model
    public List<User> getAllUsers();

    // Returns a user based on user id
    public User findUserByUserId(Long userId);

    // Returns a user based on account id
    public User findUserByAccountId(Long accountId);

    // Returns an account id based on a user id
    public Long findAccountIdByUserId(Long userId);
}
