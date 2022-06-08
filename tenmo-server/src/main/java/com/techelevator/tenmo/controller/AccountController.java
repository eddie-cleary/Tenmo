package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public double getUserBalance(Principal principal) {
        // When an authenticated user makes a get request to /account, Spring identifies which user it is based on
        // principal. We then find their id based on their username, and then return their balance based on the found id
        return userDao.findBalanceByUserId(userDao.findIdByUsername(principal.getName()));
    }
}
