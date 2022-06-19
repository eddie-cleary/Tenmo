package com.techelevator.tenmo;

import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Authority;
import com.techelevator.tenmo.model.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class JdbcUserDaoTests {
    private UserDao sut;

    private EmbeddedDatabase mockDataSource;

    @Before
    public void setupMockDatabase() {
        mockDataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("mockDatabase.sql")
                .addScript("mockData.sql")
                .build();

        JdbcTemplate mockTemplate = new JdbcTemplate(mockDataSource);
        sut = new JdbcUserDao(mockTemplate);
    }

    @After
    public void rollback() throws SQLException {
        mockDataSource.shutdown();
    }

    @Test
    public void findAll_returns_all_users() {
        // Arrange
        int expected = 2;

        // Act
        List<User> allUsers = sut.findAll();
        int actual = allUsers.size();

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void findIdByUsername_returns_correct_long() {
        // Arrange
        Long expected = 1001L;

        // Act
        Long actual = sut.findIdByUsername("test");

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void findUserByUserId_returns_correct_user() {
        // Arrange
        User expected = new User();
        expected.setId(1001L);
        expected.setUsername("test");
        expected.setPassword("$2a$10$xEMjLs3HZArjtiqF2P2Ks.PN7FA0T/AjYtBOyP7dQiqtPlZBFtosO");
        expected.setActivated(true);
        expected.getAuthorities().add(new Authority("ROLE_USER"));

        // Act
        User actual = sut.findUserByUserId(1001L);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void findUserByAccountId_returns_correct_user() {
        // Arrange
        User expected = new User();
        expected.setId(1001L);
        expected.setUsername("test");
        expected.setPassword("$2a$10$xEMjLs3HZArjtiqF2P2Ks.PN7FA0T/AjYtBOyP7dQiqtPlZBFtosO");
        expected.setActivated(true);
        expected.getAuthorities().add(new Authority("ROLE_USER"));

        // Act
        User actual = sut.findUserByAccountId(2001L);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void findAccountIdByUserId_returns_correct_id() {
        // Arrange
        Long expected = 2001L;

        // Act
        Long actual = sut.findAccountIdByUserId(1001L);

        // Assert
        Assert.assertEquals(expected, actual);
    }



    @Test
    public void findUserByUsername_returns_correct_user() {
        // Arrange
        User expected = new User();
        expected.setId(1001L);
        expected.setUsername("test");
        expected.setPassword("$2a$10$xEMjLs3HZArjtiqF2P2Ks.PN7FA0T/AjYtBOyP7dQiqtPlZBFtosO");
        expected.setActivated(true);
        expected.getAuthorities().add(new Authority("ROLE_USER"));

        // Act
        User actual = sut.findUserByUsername("test");

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void findBalanceByUserId_returns_correct_balance() {
        // Arrange
        BigDecimal expected = new BigDecimal("1000.00");

        // Act
        BigDecimal actual = sut.findBalanceByUserId(1001L);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void create_adds_user_correctly() {
        // Arrange
        sut.create("testing", "testingpassword");
        User expected = new User();
        expected.setId(1003L);
        // Password is hashed and must be retrieved to test equality
        expected.setPassword(sut.findUserByUsername("testing").getPassword());
        expected.setUsername("testing");
        expected.setActivated(true);
        expected.getAuthorities().add(new Authority("ROLE_USER"));

        // Act
        User actualUserId = sut.findUserByUserId(1003L);
        User actualAccountId = sut.findUserByAccountId(2003L);

        // Assert
        Assert.assertEquals(expected, actualUserId);
        Assert.assertEquals(expected, actualAccountId);
    }
}
