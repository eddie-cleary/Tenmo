package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.dto.TransferDTO;
import com.techelevator.tenmo.model.*;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

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


    // Retrieve a transfer based on transfer id
    @Override
    public TransferDTO getTransferById(Long id) {
        String sql = "SELECT * FROM transfer WHERE transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next() == false) {
            throw new DataRetrievalFailureException("No transfers found with id " + id);
        }
        return mapRowToTransferDTO(results);
    }

    // Creates a transfer in database
    @Override
    public boolean requestTransfer(Transfer transfer){
        String sql = "" +
                "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount)" +
                "VALUES (?, ?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql, transfer.getType().getTransferId(), transfer.getStatus().getStatusId(), userDao.findAccountIdByUserId(transfer.getSender().getId()), userDao.findAccountIdByUserId(transfer.getReceiver().getId()), transfer.getAmount());
            return true;
        } catch (DataAccessException ex) {
            throw new DataRetrievalFailureException("Error creating transfer.");
        }

    }

    // Processes a transfer by creating it in database and updating account balances
    @Override
    public boolean sendTransfer(Transfer transfer) {
        // Transaction created to make sure both balance changes occur or none at all
        String sql = "" +
                "INSERT INTO transfer(transfer_type_id, transfer_status_id, account_from, account_to, amount)" +
                "VALUES (?, ?, ?, ?, ?);" +

                "UPDATE account " +
                "SET balance = balance + ? " +
                "WHERE user_id = ?; " +

                "UPDATE account " +
                "SET balance = balance - ? " +
                "WHERE user_id = ?;";

        try {
            jdbcTemplate.update(sql, transfer.getType().getTransferId(), transfer.getStatus().getStatusId(), userDao.findAccountIdByUserId(transfer.getSender().getId()), userDao.findAccountIdByUserId(transfer.getReceiver().getId()), transfer.getAmount(),
                    transfer.getAmount(), transfer.getReceiver().getId(), transfer.getAmount(), transfer.getSender().getId());
            return true;
        } catch (DataAccessException ex) {
            throw new DataRetrievalFailureException("Error sending transfer.");
        }
    }

    // Updates transfer status to approved and updates the associated account balances
    @Override
    public boolean approveTransfer(Transfer transfer) {
        String sql = "" +
                "UPDATE transfer " +
                "SET transfer_status_id = '2' " +
                "WHERE transfer_id = ?;" +

                "UPDATE account " +
                "SET balance = balance + ? " +
                "WHERE user_id = ?; " +

                "UPDATE account " +
                "SET balance = balance - ? " +
                "WHERE user_id = ?;";

        try {
            jdbcTemplate.update(sql, transfer.getTransferId(), transfer.getAmount(), transfer.getReceiver().getId(), transfer.getAmount(), transfer.getSender().getId());
            return true;
        } catch (DataAccessException ex) {
            throw new DataRetrievalFailureException("Error approving transfer.");
        }
    }

    // Updates a transfer to status rejected
    @Override
    public boolean rejectTransfer(Transfer transfer) {
        String sql = "" +
                "UPDATE transfer " +
                "SET transfer_status_id = '3' " +
                "WHERE transfer_id = ?;";
        try {
            jdbcTemplate.update(sql, transfer.getTransferId());
            return true;
        } catch (DataAccessException ex) {
            throw new DataRetrievalFailureException("Error rejecting transfer.");
        }
    }

    // Returns a list of completed transfers that are associated with account id passed in
    public List<TransferDTO> getCompletedTransfers(Long id) {
        List completedTransfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE (account_from = ? OR account_to = ?) AND transfer_status_id = '2';";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id, id);
        if (results.next() == false) {
            return null;
        }
        do {
            TransferDTO transfer = mapRowToTransferDTO(results);
            completedTransfers.add(transfer);
        } while (results.next());

        return completedTransfers;
    }

    // Returns a list of transfers that are pending approval/rejection. Only returns transfers related to sender not receiver
    public List<TransferDTO> getPendingTransfers(Long id) {
        List pendingTransfers = new ArrayList<>();
        String sql = "SELECT * FROM transfer WHERE account_from = ? AND transfer_status_id = '1';";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next() == false) {
            return null;
        }
        do {
            TransferDTO transfer = mapRowToTransferDTO(results);
            pendingTransfers.add(transfer);
        }
        while (results.next());
        return pendingTransfers;
    }

    private Transfer mapRowToTransfer(SqlRowSet results){
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
        transfer.setSender(userDao.findUserByAccountId(results.getLong("account_from")));
        transfer.setReceiver(userDao.findUserByAccountId(results.getLong("account_to")));
        transfer.setAmount(results.getBigDecimal("amount"));
        return transfer;
    }

    private TransferDTO mapRowToTransferDTO(SqlRowSet results) {
        TransferDTO transfer = new TransferDTO();
        transfer.setTransferId(results.getLong("transfer_id"));
        // Loop through all transfer types to see if transfer matches, if so set type
        for (TransferType type : TransferType.values()) {
            if (results.getInt("transfer_type_id") == type.getTransferId()) {
                transfer.setType(type);
                break;
            }
        }
        // Loop through all transfer status to see if transfer matches, if so set status
        for (TransferStatus status : TransferStatus.values()) {
            if (results.getInt("transfer_status_id") == status.getStatusId()) {
                transfer.setStatus(status);
                break;
            }
        }
        transfer.setSender(userDao.findUserByAccountId(results.getLong("account_from")));
        transfer.setReceiver(userDao.findUserByAccountId(results.getLong("account_to")));
        transfer.setAmount(results.getBigDecimal("amount"));
        return transfer;
    }
}
