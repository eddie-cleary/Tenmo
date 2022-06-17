package com.techelevator.tenmo.service;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.dto.TransferDTO;
import com.techelevator.tenmo.exceptions.InsufficientBalanceException;
import com.techelevator.tenmo.exceptions.UserNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
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

    public TransferService() {}

    // Allows a logged in user to send a transfer from their account
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
        // Set sender as the currently logged in user. Only logged in users can send transfers.
        transfer.setSender(userDao.findByUsername(principal.getName()));
        return transferDao.sendTransfer(transfer);
    }

    // Logged in user can request a transfer
    public boolean requestTransfer(Transfer transfer, Principal principal) throws UserNotFoundException {
        User sender = userDao.findUserByUserId(transfer.getSender().getId());
        // Make sure the transfer was requested from a user that exists
        if (sender == null) {
            throw new UserNotFoundException("User " + transfer.getReceiver().getId() + " does not exist. Please try again.");
        }
        // Make sure user does not request a transfer from themselves
        if (sender.getId().equals(userDao.findIdByUsername(principal.getName()))) {
            throw new DataRetrievalFailureException("You cannot request a transfer from yourself.");
        }
        // The receiver must be the person requesting the transfer, set receiver based on principal
        transfer.setReceiver(userDao.findByUsername(principal.getName()));
        return transferDao.requestTransfer(transfer);
    }

    // Allows a logged in user to reject a transfer requested of them
    public boolean rejectTransfer(Transfer transfer, Principal principal) throws SQLException {
        // Make sure the logged in user is the sender, only senders can reject transfers requested from them
        if (principal.getName().equals(transfer.getSender().getUsername())) {
            return transferDao.rejectTransfer(transfer);
        }
        throw new DataRetrievalFailureException("Only logged in users can reject a transfer.");
    }

    // Allows a logged in user to approve a transfer requested of them
    public boolean approveTransfer(Transfer transfer, Principal principal) throws SQLException {
        // Make sure the logged in user is the sender, only senders can approve transfers requested from them
        if (principal.getName().equals(transfer.getSender().getUsername())) {
            return transferDao.approveTransfer(transfer);
        }
        throw new DataRetrievalFailureException("Only logged in users can approve a transfer.");
    }

    // Returns a list of all transfers that have been completed for the logged in user
    public List<TransferDTO> getCompletedTransfers(Principal principal) {
        return transferDao.getCompletedTransfers(userDao.findAccountIdByUserId(userDao.findIdByUsername(principal.getName())));
    }

    // Returns a list of all transfers that are pending approval/reject of the logged in user
    public List<TransferDTO> getPendingTransfers(Principal principal) {
        return transferDao.getPendingTransfers(userDao.findAccountIdByUserId(userDao.findIdByUsername(principal.getName())));
    }

    // Returns a TransferDTO based on the transfer id
    public TransferDTO getTransferById(Long id) {
        return transferDao.getTransferById(id);
    }
}

