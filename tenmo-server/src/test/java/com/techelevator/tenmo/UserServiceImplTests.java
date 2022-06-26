package com.techelevator.tenmo;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Authority;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.service.UserServiceImpl;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.math.BigDecimal;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;

public class UserServiceImplTests {

    private UserServiceImpl sut;

    private UserDao userDao;

    private TransferDao transferDao;

    // ID that is used to indicate the next available id after the mock data is added. This can be updated if more mock
    // transfers are added. This ensures each test is always working with the latest transfer the test specifically added
    // and not mock data from sql file.
    private final Long NEXT_TRANSFER_ID = 3005L;

    @Mock
    private Principal principal;

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    private EmbeddedDatabase mockDataSource;

    public UserServiceImplTests() {
        principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("test");
    }

    @Before
    public void setupMockDatabase() {
        mockDataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("mockDatabase.sql")
                .addScript("mockData.sql")
                .build();

        JdbcTemplate mockTemplate = new JdbcTemplate(mockDataSource);
        userDao = new JdbcUserDao(mockTemplate);
        transferDao = new JdbcTransferDao(mockTemplate, userDao);
        sut = new UserServiceImpl(userDao);
    }

    @After
    public void rollback() throws SQLException {
        mockDataSource.shutdown();
    }

    @Test
    public void getUserBalance_returns_correct_balance() {
        // Arrange
        BigDecimal expected = new BigDecimal("1000.00");

        // Act
        BigDecimal actual = sut.getUserBalance(principal);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getAllUsers_returns_correct_size_of_users() {
        // Arrange
        int expected = 2;
        List<User> allUsers = sut.getAllUsers();

        // Act
        int actual = allUsers.size();

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void findAccountIdByUserId_returns_correct_account_id() {
        // Arrange
        Long expected = 2001L;

        // Act
        Long actual = sut.findAccountIdByUserId(1001L);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void findUserByUserId_returns_correct_User() {
        // Arrange
        User expected = new User();
        expected.setId(1001L);
        expected.setUsername("test");
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
        expected.setActivated(true);
        expected.getAuthorities().add(new Authority("ROLE_USER"));

        // Act
        User actual = sut.findUserByAccountId(2001L);

        // Assert
        Assert.assertEquals(expected, actual);
    }}
