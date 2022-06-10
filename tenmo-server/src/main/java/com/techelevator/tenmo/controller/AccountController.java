package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/account")
// Users must be authenticated to access their account
@PreAuthorize("isAuthenticated()")
public class AccountController {
    private UserDao userDao;

    public AccountController(UserDao userDao){
        this.userDao = userDao;
    }

    @GetMapping
    public BigDecimal getUserBalance(Principal principal) {
        // When an authenticated user makes a get request to /account, Spring identifies which user it is based on
        // principal. We then find their id based on their username, and then return their balance based on the found id
        return userDao.findBalanceByUserId(userDao.findIdByUsername(principal.getName()));
    }

    @GetMapping(path = "/{userId}")
    public User getUserById(@PathVariable Long userId) {
        return userDao.findUserById(userId);
    }
}
