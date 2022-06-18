package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserDao {

    List<User> findAll();

    User findUserByUsername(String username);

    Long findIdByUsername(String username);

    BigDecimal findBalanceByUserId(Long id);

    User findUserByUserId(Long id);

    Long findAccountIdByUserId(Long id);

    User findUserByAccountId(Long id);

    boolean create(String username, String password);
}
