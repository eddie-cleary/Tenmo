import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.dto.TransferDTO;
import com.techelevator.tenmo.model.Authority;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.TransferType;
import com.techelevator.tenmo.model.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class JdbcTransferDaoTests {
    private TransferDao sut;

    private UserDao userDao;

    private EmbeddedDatabase mockDataSource;

    @Before
    public void setupMockDatabase() {
        mockDataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("mockDatabase.sql")
                .addScript("mockData.sql")
                .build();

        JdbcTemplate mockTemplate = new JdbcTemplate(mockDataSource);
        userDao = new JdbcUserDao(mockTemplate);
        sut = new JdbcTransferDao(mockTemplate, userDao);
    }

    @After
    public void rollback() throws SQLException {
        mockDataSource.shutdown();
    }

    @Test
    public void getTransferById_returns_correct_transfer() {
        // Arrange
        TransferDTO expected = new TransferDTO();
        expected.setTransferId(3001L);
        expected.setType(TransferType.SEND);
        expected.setStatus(TransferStatus.APPROVED);
        expected.setSender(userDao.findUserByUsername("test"));
        expected.setReceiver(userDao.findUserByUsername("trial"));
        expected.setAmount(new BigDecimal("50.00"));

        // Act
        TransferDTO actual = sut.getTransferById(3001L);

        // Assert
        Assert.assertEquals(expected, actual);
    }
}
