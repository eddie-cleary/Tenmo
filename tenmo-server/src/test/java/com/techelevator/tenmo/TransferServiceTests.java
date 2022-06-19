package com.techelevator.tenmo;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.dto.TransferDTO;
import com.techelevator.tenmo.exceptions.InsufficientBalanceException;
import com.techelevator.tenmo.exceptions.UserNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.TransferType;
import com.techelevator.tenmo.service.TransferService;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.SQLException;

public class TransferServiceTests {

    private TransferService sut;

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

    public TransferServiceTests() {
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
        sut = new TransferService(userDao, transferDao);
    }

    @After
    public void rollback() throws SQLException {
        mockDataSource.shutdown();
    }

    @Test
    public void sendTransfer_sends_successful_transfer() throws UserNotFoundException, SQLException, InsufficientBalanceException {
        // Arrange
        boolean expected = true;

        // Act
        Transfer transferToSend = new Transfer();
        transferToSend.setStatus(TransferStatus.APPROVED);
        transferToSend.setType(TransferType.SEND);
        transferToSend.setSender(userDao.findUserByUsername("test"));
        transferToSend.setReceiver(userDao.findUserByUsername("trial"));
        transferToSend.setAmount(new BigDecimal("50.00"));
        boolean actual = sut.sendTransfer(transferToSend, principal);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void sendTransfer_throws_InsufficientBalanceException() throws UserNotFoundException, SQLException, InsufficientBalanceException {
        // Arrange
        Transfer transferToSend = new Transfer();
        transferToSend.setStatus(TransferStatus.APPROVED);
        transferToSend.setType(TransferType.SEND);
        transferToSend.setSender(userDao.findUserByUsername("test"));
        transferToSend.setReceiver(userDao.findUserByUsername("trial"));
        transferToSend.setAmount(new BigDecimal("500000.00"));

        // Act & Assert
        exception.expect(InsufficientBalanceException.class);
        sut.sendTransfer(transferToSend, principal);
    }

    @Test
    public void requestTransfer_sends_successful_request() throws UserNotFoundException, SQLException {
        // Arrange
        boolean expected = true;
        Transfer transferToRequest = new Transfer();
        transferToRequest.setStatus(TransferStatus.PENDING);
        transferToRequest.setType(TransferType.REQUEST);
        transferToRequest.setSender(userDao.findUserByUsername("trial"));
        transferToRequest.setReceiver(userDao.findUserByUsername("test"));
        transferToRequest.setAmount(new BigDecimal("50.00"));

        // Act
        boolean actual = sut.requestTransfer(transferToRequest, principal);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void requestTransfer_throws_DataRetrievalFailureException() throws UserNotFoundException, SQLException {
        // Arrange
        Transfer transferToRequest = new Transfer();
        transferToRequest.setStatus(TransferStatus.PENDING);
        transferToRequest.setType(TransferType.REQUEST);
        transferToRequest.setSender(userDao.findUserByUsername("test"));
        transferToRequest.setReceiver(userDao.findUserByUsername("trial"));
        transferToRequest.setAmount(new BigDecimal("50.00"));

        // Act & Assert
        exception.expect(DataRetrievalFailureException.class);
        sut.requestTransfer(transferToRequest, principal);
    }

    @Test
    public void rejectTransfer_returns_true() throws SQLException {
        // Arrange
        boolean expected = true;

        // Act
        boolean actual = sut.rejectTransfer(transferDao.getTransferById(3003L), principal);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void approveTransfer_returns_true() throws SQLException {
        // Arrange
        boolean expected = true;

        // Act
        boolean actual = sut.approveTransfer(transferDao.getTransferById(3003L), principal);

        // Assert
        Assert.assertEquals(expected, actual);
    }
}
