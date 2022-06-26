package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/account")
// Users must be authenticated to access their account
@PreAuthorize("isAuthenticated()")
public class AccountController {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserServiceImpl userServiceImpl;

    public AccountController(UserDao userDao){
        this.userDao = userDao;
    }

    @GetMapping
    public BigDecimal getUserBalance(Principal principal) {
        return userServiceImpl.getUserBalance(principal);
    }

    @GetMapping(path = "/{accountId}")
    public User getUserByAccountId(@PathVariable Long accountId) {
        return userServiceImpl.findUserByAccountId(accountId);
    }

    @GetMapping(path = "/userid")
    public Long getAccountIdByUserId(@RequestParam Long id) {
        return userServiceImpl.findAccountIdByUserId(id);
    }
}
