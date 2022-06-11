package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.tenmo.model.TransferType;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private JdbcTemplate jdbcTemplate;

    private UserDao userDao;


    public JdbcTransferDao(JdbcTemplate jdbcTemplate, JdbcUserDao userDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDao = userDao;
    }

    @Override
    public boolean requestTransfer(Transfer transfer){
        String sql = "" +
                "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount)" +
                "VALUES (?, ?, ?, ?, ?)";

        // Must use account ids and not user ids, userDao is used to find account Id by User Id that was received

        if (jdbcTemplate.update(sql, transfer.getType().getTransferId(), transfer.getStatus().getStatusId(), userDao.findAccountIdByUserId(transfer.getSenderId()), userDao.findAccountIdByUserId(transfer.getReceiverId()), transfer.getAmount()) == 1) {
            return true;
        }
        throw new DataRetrievalFailureException("Database error.");
    }

    @Override
    public boolean sendTransfer(Transfer transfer) {
        // Transaction created to make sure both balance changes occur or none at all
        System.out.println("In send transfer");
        String sql = "BEGIN; " +

                "INSERT INTO transfer(transfer_type_id, transfer_status_id, account_from, account_to, amount)" +
                "VALUES (?, ?, ?, ?, ?);" +

                "UPDATE account " +
                "SET balance = balance + ? " +
                "WHERE user_id = ?; " +

                "UPDATE account " +
                "SET balance = balance - ? " +
                "WHERE user_id = ?; " +

                "COMMIT; ";
        jdbcTemplate.update(sql, transfer.getType().getTransferId(), transfer.getStatus().getStatusId(), userDao.findAccountIdByUserId(transfer.getSenderId()), userDao.findAccountIdByUserId(transfer.getReceiverId()), transfer.getAmount(),
                transfer.getAmount(), transfer.getReceiverId(), transfer.getAmount(), transfer.getSenderId());
        return true;
    }

    public List<Transfer> getCompletedTransfers(Long id) {
        List completedTransfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE account_from = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while (results.next()) {
            System.out.println("in transfer mapper");
            Transfer transfer = mapRowToTransfer(results);
            System.out.println("adding " + transfer.getType() + " to list.");
            completedTransfers.add(transfer);
        }
        return completedTransfers;
    }

    public Transfer mapRowToTransfer(SqlRowSet results){
        Transfer transfer = new Transfer();
        transfer.setTransferId(results.getLong("transfer_id"));
        for (TransferType type : TransferType.values()) {
            if (results.getInt("transfer_type_id") == type.getTransferId()) {
                transfer.setType(type);
                break;
            }
        }
        for (TransferStatus status : TransferStatus.values()) {
            if (results.getInt("transfer_status_id") == status.getStatusId()) {
                transfer.setStatus(status);
                break;
            }
        }
        transfer.setSenderId(results.getLong("account_from"));
        transfer.setReceiverId(results.getLong("account_to"));
        transfer.setAmount(results.getBigDecimal("amount"));
        return transfer;
    }
}
