package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserPublic;

import java.math.BigDecimal;
import java.util.List;

public interface UserDao {

    List<User> findAll();

    User findByUsername(String username);

    Long findIdByUsername(String username);

    // find balance based on user's id
    BigDecimal findBalanceByUserId(Long id);

    User findUserById(Long id);

    boolean create(String username, String password);
}
