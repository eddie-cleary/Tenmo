package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exceptions.InsufficientBalanceException;
import com.techelevator.tenmo.exceptions.UserNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
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
        System.out.println(transfer.getSenderId() + " " + transfer.getReceiverId() + " " + transfer.getAmount() + " " + transfer.getStatus() + " " + transfer.getType());
        return transferDao.sendTransfer(transfer);
    }

    @GetMapping
    @RequestMapping("/completed")
    public List<Transfer> getCompletedTransfers(Principal principal) {
        System.out.println("in transfer/completed");
        System.out.println("logged in user " + principal.getName());
        System.out.println("user id you are sending " + userDao.findAccountIdByUserId(userDao.findIdByUsername(principal.getName())));
        return transferDao.getCompletedTransfers(userDao.findAccountIdByUserId(userDao.findIdByUsername(principal.getName())));
    }

}
