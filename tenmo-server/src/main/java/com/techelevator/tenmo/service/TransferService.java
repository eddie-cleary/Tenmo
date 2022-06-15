package com.techelevator.tenmo.service;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.dto.TransferDTO;
import com.techelevator.tenmo.exceptions.InsufficientBalanceException;
import com.techelevator.tenmo.exceptions.UserNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;

@Service
public class TransferService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private TransferDao transferDao;

    public TransferService(UserDao userDao, TransferDao transferDao) {
        this.userDao = userDao;
        this.transferDao = transferDao;
    }

    public TransferService() {}

    public boolean sendTransfer(Transfer transfer, Principal principal) throws InsufficientBalanceException, UserNotFoundException, SQLException {
        // Make sure the sender has sufficient funds
        BigDecimal senderCurrBalance = userDao.findBalanceByUserId(userDao.findIdByUsername(principal.getName()));
        if (senderCurrBalance.compareTo(transfer.getAmount()) == -1) {
            throw new InsufficientBalanceException("Insufficient balance. You can not send an amount greater than your balance of: $" + userDao.findBalanceByUserId(transfer.getSender().getId()));
        }
        // Make sure receiver exists
        User receiver = userDao.findUserByUserId(transfer.getReceiver().getId());
        if (receiver == null) {
            throw new UserNotFoundException("Receiving user with id of " + transfer.getReceiver().getId() + " does not exist. Please try again.");
        }
        // Generate and add User sender to transfer object, based on the principal. Secure way of making sure logged in user is the sender.
        transfer.setSender(userDao.findByUsername(principal.getName()));
        return transferDao.sendTransfer(transfer);
    }

    public List<TransferDTO> getCompletedTransfers(Principal principal) {
        return transferDao.getCompletedTransfers(userDao.findIdByUsername(principal.getName()));
    }

    public TransferDTO getTransferById(Long id) {
        return transferDao.getTransferById(id);
    }
}

