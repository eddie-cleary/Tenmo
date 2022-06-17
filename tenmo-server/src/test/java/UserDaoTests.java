import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.UserDao;
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
import java.util.List;
import java.util.Optional;

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
        List<User> allUsers = sut.findAll();

        // Act
        int size = allUsers.size();

        // Assert
        Assert.assertEquals(2, size);
    }

    @Test
    public void find_by_username_returns_correct_long() {
        // Arrange & Act
        Long testLong = sut.findIdByUsername("test");

        // Assert
        Assert.assertEquals(Long.valueOf(1001), testLong);
    }
}
