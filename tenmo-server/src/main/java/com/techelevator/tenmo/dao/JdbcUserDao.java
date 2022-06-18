package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
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
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcUserDao(){}

    // Returns the user id based on username
    @Override
    public Long findIdByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT user_id FROM tenmo_user WHERE username ILIKE ?;";
        Long id = jdbcTemplate.queryForObject(sql, Long.class, username);
        if (id != null) {
            return id;
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    // Returns a list of all users in database. Password info is obscured in response with jsonignore
    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        if (results.next() == false) {
            throw new DataRetrievalFailureException("No users found in database.");
        } else {
            do {
                users.add(mapRowToUser(results));
            } while (results.next());
        }

        return users;
    }

    // Retrieve user based on user id
    @Override
    public User findUserByUserId(Long id) {
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE user_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, id);
        if (rowSet.next() == false) {
            throw new DataRetrievalFailureException("User with id " + id + " not found in database.");
        } else {
            return mapRowToUser(rowSet);
        }
    }

    // Retrieve user based on account id
    public User findUserByAccountId(Long id) {
        String sql = "" +
                "SELECT tenmo_user.user_id, username, password_hash FROM tenmo_user " +
                "JOIN account ON account.user_id = tenmo_user.user_id " +
                "WHERE account.account_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next() == false) {
            throw new DataRetrievalFailureException("No user with account id " + id + " found in database.");
        } else {
            return mapRowToUser(results);
        }
    }

    // Retrieve account id based on user id
    public Long findAccountIdByUserId(Long userId) {
        String sql = "SELECT account_id FROM account WHERE user_id = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, Long.class, userId);
        } catch (DataAccessException e) {
            throw new DataRetrievalFailureException("No account found with user id " + userId);
        }
    }

    // Retrieve user object based on username
    @Override
    public User findUserByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE username ILIKE ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()){
            return mapRowToUser(rowSet);
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    // Retrieve balance from database based on user id.
    @Override
    public BigDecimal findBalanceByUserId(Long id) {
        String sql = "SELECT balance FROM account WHERE user_id = ?;";
        try {
            return jdbcTemplate.queryForObject(sql, BigDecimal.class, id);
        } catch (DataAccessException e) {
            throw new DataRetrievalFailureException("No balance found.");
        }
    }

    // Create new user, first by creating user then creating account
    @Override
    public boolean create(String username, String password) {

        // create user
        String sql = "INSERT INTO tenmo_user (username, password_hash) VALUES (?, ?);";
        String password_hash = new BCryptPasswordEncoder().encode(password);
        Long newUserId;
        try {
            jdbcTemplate.update(sql, username, password_hash);
            newUserId = findIdByUsername(username);
//            newUserId = jdbcTemplate.queryForObject(sql, Integer.class, username, password_hash);
        } catch (DataAccessException e) {
            throw new DataRetrievalFailureException("Error adding new user to database. " + e.getMessage());
        }

        // create account
        sql = "INSERT INTO account (user_id, balance) values(?, ?)";
        try {
            jdbcTemplate.update(sql, newUserId, STARTING_BALANCE);
        } catch (DataAccessException e) {
            throw new DataRetrievalFailureException("Error adding new account to database.");
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
