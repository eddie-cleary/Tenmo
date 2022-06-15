package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exceptions.InsufficientBalanceException;
import com.techelevator.tenmo.exceptions.UserNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.dto.TransferDTO;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("transfer")
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private UserDao userDao;
    private TransferDao transferDao;

    public TransferController(UserDao userDao, TransferDao transferDao) {
        this.userDao = userDao;
        this.transferDao = transferDao;
    }

    @PostMapping
    public boolean newTransfer(@Valid @RequestBody Transfer transfer, Principal principal) throws InsufficientBalanceException, UserNotFoundException, DataRetrievalFailureException, SQLException {
        // need to make sure the sender has sufficient funds
        BigDecimal senderCurrBalance = userDao.findBalanceByUserId(userDao.findIdByUsername(principal.getName()));
        if (senderCurrBalance.compareTo(transfer.getAmount()) == -1) {
            throw new InsufficientBalanceException("Insufficient balance. You can not send an amount greater than your balance of: $" + userDao.findBalanceByUserId(transfer.getSender().getId()));
        }
        // need to make sure the receiver exists
        User receiver = userDao.findUserByUserId(transfer.getReceiver().getId());
        if (receiver == null) {
            throw new UserNotFoundException("Receiving user with id of " + transfer.getReceiver().getId() + " does not exist. Please try again.");
        }
        transfer.setSender(userDao.findByUsername(principal.getName()));
        return transferDao.sendTransfer(transfer);
    }

    @GetMapping
    @RequestMapping("/completed")
    public List<TransferDTO> getCompletedTransfers(Principal principal) {
        return transferDao.getCompletedTransfers(userDao.findAccountIdByUserId(userDao.findIdByUsername(principal.getName())));
    }

}
