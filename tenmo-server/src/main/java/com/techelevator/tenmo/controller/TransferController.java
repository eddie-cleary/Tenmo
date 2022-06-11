package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exceptions.InsufficientBalanceException;
import com.techelevator.tenmo.exceptions.UserNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("transfer")
public class TransferController {

    private UserDao userDao;
    private TransferDao transferDao;

    public TransferController(UserDao userDao, TransferDao transferDao) {
        this.userDao = userDao;
        this.transferDao = transferDao;
    }

    @RequestMapping(method = RequestMethod.POST)
    public boolean newTransfer(@Valid @RequestBody Transfer transfer) throws InsufficientBalanceException, UserNotFoundException, DataRetrievalFailureException {
        // need to make sure the sender has sufficient funds
        BigDecimal senderCurrBalance = userDao.findBalanceByUserId(transfer.getSenderId());
        if (senderCurrBalance.compareTo(transfer.getAmount()) == -1) {
            throw new InsufficientBalanceException("Insufficient balance. You can not send an amount greater than your balance of: $" + userDao.findBalanceByUserId(transfer.getSenderId()));
        }
        // need to make sure the receiver exists
        User receiver = userDao.findUserById(transfer.getReceiverId());
        if (receiver == null) {
            throw new UserNotFoundException("Receiving user with id of " + transfer.getReceiverId() + " does not exist. Please try again.");
        }
        return transferDao.sendTransfer(transfer);
    }


}
