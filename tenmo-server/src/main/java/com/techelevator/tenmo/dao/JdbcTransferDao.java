package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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
                "UPDATE account " +
                "SET balance = balance + ? " +
                "WHERE user_id = ?; " +
                " " +
                "UPDATE account " +
                "SET balance = balance - ? " +
                "WHERE user_id = ?; " +
                " " +
                "COMMIT; ";
        System.out.println("after send transfer");
        int response = jdbcTemplate.update(sql, transfer.getAmount(), transfer.getReceiverId(), transfer.getAmount(), transfer.getSenderId());
        System.out.println("response " + response);
        return true;
        //Test this error
        //throw new DataRetrievalFailureException("Error inserting into database.");
    }
}
