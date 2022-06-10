package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("transfer")
public class TransferController {

    private UserDao userDao;

    public TransferController(UserDao userDao) {
        this.userDao = userDao;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Transfer newTransfer(@Valid @RequestBody Transfer transfer) {
        // need to make sure the sender has sufficient funds
        BigDecimal senderCurrBalance = userDao.findBalanceByUserId(transfer.getSenderId());
        if (senderCurrBalance.compareTo(transfer.getAmount()) == -1) {
            System.out.println("Throw insufficient balance exception");
            return null;
        }
        // need to make sure the receiver exists
        User receiver = userDao.findUserById(transfer.getReceiverId());
        if (receiver == null) {
            System.out.println("Throw user not found exception");
        }
        // TODO create a transfer in database and return transfer.
        return transfer;
    }


}
