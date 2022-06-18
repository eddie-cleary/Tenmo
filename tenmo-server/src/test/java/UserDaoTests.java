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

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class UserDaoTests {
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

        // Arrange & Act
        Long actual = sut.findIdByUsername("test");

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void findUserByUserId() {
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

}
