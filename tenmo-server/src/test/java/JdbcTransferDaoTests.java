import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.dto.TransferDTO;
import com.techelevator.tenmo.model.*;
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

public class JdbcTransferDaoTests {
    private TransferDao sut;

    private UserDao userDao;

    private EmbeddedDatabase mockDataSource;

    // ID that is used to indicate the next available id after the mock data is added. This can be updated if more mock
    // transfers are added. This ensures each test is always working with the latest transfer the test specifically added
    // and not mock data from sql file.
    private final Long NEXT_TRANSFER_ID = 3005L;

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
        TransferDTO actual = sut.getTransferDTOById(3001L);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void requestTransfer_gets_added_correctly() throws SQLException {
        // Arrange
        boolean expected = true;
        TransferDTO expectedTransfer = new TransferDTO();
        expectedTransfer.setTransferId(NEXT_TRANSFER_ID);
        expectedTransfer.setType(TransferType.REQUEST);
        expectedTransfer.setStatus(TransferStatus.PENDING);
        expectedTransfer.setSender(userDao.findUserByUsername("test"));
        expectedTransfer.setReceiver(userDao.findUserByUsername("trial"));
        expectedTransfer.setAmount(new BigDecimal("50.00"));

        // Act
        Transfer requestedTransfer = new Transfer();
        requestedTransfer.setType(TransferType.REQUEST);
        requestedTransfer.setStatus(TransferStatus.PENDING);
        requestedTransfer.setSender(userDao.findUserByUsername("test"));
        requestedTransfer.setReceiver(userDao.findUserByUsername("trial"));
        requestedTransfer.setAmount(new BigDecimal("50.00"));

        boolean actual = sut.requestTransfer(requestedTransfer);
        TransferDTO actualTransfer = sut.getTransferDTOById(NEXT_TRANSFER_ID);

        // Assert
        Assert.assertEquals(expected, actual);
        Assert.assertEquals(expectedTransfer, actualTransfer);
    }

    @Test
    public void sendTransfer_adds_transfer_and_updates_accounts_correctly() throws SQLException {
        // Arrange
        boolean expected = true;
        TransferDTO expectedTransfer = new TransferDTO();
        expectedTransfer.setTransferId(NEXT_TRANSFER_ID);
        expectedTransfer.setType(TransferType.SEND);
        expectedTransfer.setStatus(TransferStatus.APPROVED);
        expectedTransfer.setSender(userDao.findUserByUsername("test"));
        expectedTransfer.setReceiver(userDao.findUserByUsername("trial"));
        expectedTransfer.setAmount(new BigDecimal("50.00"));
        BigDecimal expectedSenderBalance = new BigDecimal("950.00");
        BigDecimal expectedReceiverBalance = new BigDecimal("1050.00");

        // Act
        Transfer transferToSend = new Transfer();
        transferToSend.setType(TransferType.SEND);
        transferToSend.setStatus(TransferStatus.APPROVED);
        transferToSend.setSender(userDao.findUserByUsername("test"));
        transferToSend.setReceiver(userDao.findUserByUsername("trial"));
        transferToSend.setAmount(new BigDecimal("50.00"));
        boolean actual = sut.sendTransfer(transferToSend);
        TransferDTO actualTransfer = sut.getTransferDTOById(NEXT_TRANSFER_ID);
        BigDecimal actualSenderBalance = userDao.findBalanceByUserId(1001L);
        BigDecimal actualReceiverBalance = userDao.findBalanceByUserId(1002L);

        // Assert
        Assert.assertEquals(expected, actual);
        Assert.assertEquals(expectedTransfer, actualTransfer);
        Assert.assertEquals(expectedSenderBalance, actualSenderBalance);
        Assert.assertEquals(expectedReceiverBalance, actualReceiverBalance);
    }

    @Test
    public void approveTransfer_updates_status_and_account_balances() throws SQLException {
        // Arrange
        boolean expected = true;
        TransferDTO expectedTransfer = new TransferDTO();
        expectedTransfer.setTransferId(NEXT_TRANSFER_ID);
        expectedTransfer.setType(TransferType.REQUEST);
        expectedTransfer.setStatus(TransferStatus.APPROVED);
        expectedTransfer.setSender(userDao.findUserByUsername("test"));
        expectedTransfer.setReceiver(userDao.findUserByUsername("trial"));
        expectedTransfer.setAmount(new BigDecimal("50.00"));
        BigDecimal expectedSenderBalance = new BigDecimal("950.00");
        BigDecimal expectedReceiverBalance = new BigDecimal("1050.00");

        // Act
        Transfer requestedTransfer = new Transfer();
        requestedTransfer.setType(TransferType.REQUEST);
        requestedTransfer.setStatus(TransferStatus.PENDING);
        requestedTransfer.setSender(userDao.findUserByUsername("test"));
        requestedTransfer.setReceiver(userDao.findUserByUsername("trial"));
        requestedTransfer.setAmount(new BigDecimal("50.00"));
        sut.requestTransfer(requestedTransfer);
        Transfer transferToApprove = sut.getTransferById(NEXT_TRANSFER_ID);
        boolean actual = sut.approveTransfer(transferToApprove);
        TransferDTO actualTransfer = sut.getTransferDTOById(NEXT_TRANSFER_ID);
        BigDecimal actualSenderBalance = userDao.findBalanceByUserId(1001L);
        BigDecimal actualReceiverBalance = userDao.findBalanceByUserId(1002L);

        // Assert
        Assert.assertEquals(expected, actual);
        Assert.assertEquals(expectedTransfer, actualTransfer);
        Assert.assertEquals(expectedSenderBalance, actualSenderBalance);
        Assert.assertEquals(expectedReceiverBalance, actualReceiverBalance);
    }

    @Test
    public void rejectTransfer_updates_status_to_rejected() throws SQLException {
        // Arrange
        TransferDTO expected = new TransferDTO();
        expected.setTransferId(NEXT_TRANSFER_ID);
        expected.setStatus(TransferStatus.REJECTED);
        expected.setType(TransferType.REQUEST);
        expected.setAmount(new BigDecimal("50.00"));
        expected.setSender(userDao.findUserByUsername("test"));
        expected.setReceiver(userDao.findUserByUsername("trial"));

        // Act
        Transfer transferToReject = new Transfer();
        transferToReject.setTransferId(NEXT_TRANSFER_ID);
        transferToReject.setStatus(TransferStatus.PENDING);
        transferToReject.setType(TransferType.REQUEST);
        transferToReject.setAmount(new BigDecimal("50.00"));
        transferToReject.setSender(userDao.findUserByUsername("test"));
        transferToReject.setReceiver(userDao.findUserByUsername("trial"));
        sut.requestTransfer(transferToReject);
        sut.rejectTransfer(transferToReject);
        TransferDTO actual = sut.getTransferDTOById(NEXT_TRANSFER_ID);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getCompletedTransfers_returns_correct_size_all_approved() {
        // Arrange
        int expected = 2;
        boolean expectedAllApprovedStatus = true;

        // Act
        List<TransferDTO> completedTransfers = sut.getCompletedTransfers(2001L);
        int actual = completedTransfers.size();
        boolean actualAllApprovedStatus = true;
        for (TransferDTO transfer : completedTransfers) {
            if (!(transfer.getStatus().equals(TransferStatus.APPROVED))) {
                actualAllApprovedStatus = false;
            }
        }

        // Assert
        Assert.assertEquals(expected, actual);
        Assert.assertEquals(expectedAllApprovedStatus, actualAllApprovedStatus);
    }

}
