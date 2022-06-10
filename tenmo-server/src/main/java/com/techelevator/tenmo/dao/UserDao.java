package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserPublic;

import java.math.BigDecimal;
import java.util.List;

public interface UserDao {

    List<User> findAll();

    List<UserPublic> findAllNameId();

    User findByUsername(String username);

    int findIdByUsername(String username);

    // find balance based on user's id
    double findBalanceByUserId(int id);

    boolean create(String username, String password);
}
