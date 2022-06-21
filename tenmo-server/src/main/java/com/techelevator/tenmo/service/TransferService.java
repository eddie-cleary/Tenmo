package com.techelevator.tenmo.service;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.dto.TransferDTO;
import com.techelevator.tenmo.exceptions.InsufficientBalanceException;
import com.techelevator.tenmo.exceptions.UserNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferStatus;
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

    public TransferService(UserDao userDao, TransferDao transferDao) {
        this.userDao = userDao;
        this.transferDao = transferDao;
    }

    // Allows a logged in user to send a transfer from their account
    public boolean sendTransfer(Transfer transfer, Principal principal) throws InsufficientBalanceException, UserNotFoundException, SQLException {
        // Validate transfer
        if (!(validateTransfer(transfer, principal))) {
            return false;
        }
        // Only logged in user can send a transfer
        if (userDao.getSender(transfer).equals(userDao.getCurrentUser(principal))) {
            return transferDao.sendTransfer(transfer);
        }
        return false;
    }

    // Logged in user can request a transfer
    public boolean requestTransfer(Transfer transfer, Principal principal) throws UserNotFoundException, SQLException, InsufficientBalanceException {
        if (!(validateTransfer(transfer, principal))) {
            return false;
        }
        // Only a logged in user can request a transfer and they must be receiver
        if (userDao.getReceiver(transfer).equals(userDao.getCurrentUser(principal))) {
            return transferDao.requestTransfer(transfer);
        }
        throw new DataRetrievalFailureException("Only logged in user can request a transfer.");
    }

    // Allows a logged in user to reject a transfer requested of them
    public boolean rejectTransfer(Transfer transfer, Principal principal) throws SQLException {
        // Only a logged in user who is the sender can reject a transfer
        if (userDao.getSender(transfer).equals(userDao.getCurrentUser(principal))) {
            return transferDao.rejectTransfer(transfer);
        }
        throw new DataRetrievalFailureException("Only logged in users can reject a transfer.");
    }

    // Allows a logged in user to approve a transfer requested of them
    public boolean approveTransfer(Transfer transfer, Principal principal) throws SQLException, UserNotFoundException, InsufficientBalanceException {
        if (!(validateTransfer(transfer, principal))) {
            return false;
        }
        // Only a logged in user who is the sender can approve a transfer
        if (userDao.getSender(transfer).equals(userDao.getCurrentUser(principal))) {
            return transferDao.approveTransfer(transfer);
        }
        throw new DataRetrievalFailureException("Only logged in users can approve a transfer.");
    }

    public boolean validateTransfer(Transfer transfer, Principal principal) throws InsufficientBalanceException, UserNotFoundException {
        // Make sure the sender has sufficient funds
        BigDecimal senderCurrBalance = userDao.findBalanceByUserId(transfer.getSender().getId());
        if (transfer.getStatus().equals(TransferStatus.APPROVED) && senderCurrBalance.compareTo(transfer.getAmount()) == -1) {
            throw new InsufficientBalanceException("Insufficient balance. You can not send an amount greater than your balance of: $" + userDao.findBalanceByUserId(transfer.getSender().getId()));
        }
        // Make sure sender and receiver exists
        User receiver = userDao.getReceiver(transfer);
        User sender = userDao.getSender(transfer);
        if (receiver == null) {
            throw new UserNotFoundException("Receiving user does not exist. Please try again.");
        }
        if (sender == null) {
            throw new UserNotFoundException("Sending user does not exist. Please try again.");
        }
        if (receiver.equals(sender)) {
            throw new DataRetrievalFailureException("Sender and receiver can not be the same.");
        }
        return true;
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
    public TransferDTO getTransferDTOById(Long id) {
        return transferDao.getTransferDTOById(id);
    }
}

