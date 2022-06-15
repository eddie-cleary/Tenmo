package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.service.UserService;
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
    private UserService userService;

    public AccountController(UserDao userDao){
        this.userDao = userDao;
    }

    @GetMapping
    public BigDecimal getUserBalance(Principal principal) {
        return userService.getUserBalance(principal);
    }

    @GetMapping(path = "/{id}")
    public User getUserFromAccountId(@PathVariable Long id) {
        return userService.findUserByAccountId(id);
    }
}
