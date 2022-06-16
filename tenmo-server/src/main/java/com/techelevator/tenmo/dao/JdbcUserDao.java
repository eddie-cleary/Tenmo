package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.dto.UserDTO;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcUserDao implements UserDao {

    private static final BigDecimal STARTING_BALANCE = new BigDecimal("1000.00");
    private JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long findIdByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT user_id FROM tenmo_user WHERE username ILIKE ?;";
        Long id = jdbcTemplate.queryForObject(sql, Long.class, username);
        if (id != null) {
            return id;
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            User user = mapRowToUser(results);
            users.add(user);
        }
        return users;
    }

    @Override
    public User findUserByUserId(Long id) {
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE user_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (rowSet.next()) {
            return mapRowToUser(rowSet);
        }
        return null;
    }

    public User findUserByAccountId(Long id) {
        System.out.println("in jdbc");
        String sql = "" +
                "SELECT tenmo_user.user_id, username, password_hash FROM tenmo_user " +
                "JOIN account ON account.user_id = tenmo_user.user_id " +
                "WHERE account.account_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while (results.next()) {
            System.out.println("in results");
            User user;
            user = mapRowToUser(results);
            return user;
        }
        return null;
    }

    public Long findAccountIdByUserId(Long userId) {
        String sql = "SELECT account_id FROM account WHERE user_id = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, Long.class, userId);
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
            return -1L;
        }
    }

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE username ILIKE ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()){
            return mapRowToUser(rowSet);
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    // retrieves balance from database where user_id matches param
    // TODO handle exception better
    @Override
    public BigDecimal findBalanceByUserId(Long id) {
        String sql = "SELECT balance FROM account WHERE user_id = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, BigDecimal.class, id);
        } catch (DataAccessException e) {
            return null;
        }

    }

    @Override
    public boolean create(String username, String password) {

        // create user
        String sql = "INSERT INTO tenmo_user (username, password_hash) VALUES (?, ?) RETURNING user_id";
        String password_hash = new BCryptPasswordEncoder().encode(password);
        Integer newUserId;
        try {
            newUserId = jdbcTemplate.queryForObject(sql, Integer.class, username, password_hash);
        } catch (DataAccessException e) {
            return false;
        }

        // create account
        sql = "INSERT INTO account (user_id, balance) values(?, ?)";
        try {
            jdbcTemplate.update(sql, newUserId, STARTING_BALANCE);
        } catch (DataAccessException e) {
            return false;
        }

        return true;
    }

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("USER");
        return user;
    }
}
