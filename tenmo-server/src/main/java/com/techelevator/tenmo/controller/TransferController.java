package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exceptions.InsufficientBalanceException;
import com.techelevator.tenmo.exceptions.UserNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.dto.TransferDTO;
import com.techelevator.tenmo.service.TransferService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("transfer")
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private UserDao userDao;
    private TransferDao transferDao;

    private TransferService transferService;

    public TransferController(UserDao userDao, TransferDao transferDao) {
        this.userDao = userDao;
        this.transferDao = transferDao;
    }

    @PostMapping
    public boolean newTransfer(@Valid @RequestBody Transfer transfer, Principal principal) throws UserNotFoundException, SQLException, InsufficientBalanceException {
        return transferService.sendTransfer(transfer, principal);
    }

    @GetMapping
    @RequestMapping("/completed")
    public List<TransferDTO> getCompletedTransfers(Principal principal) {
        return transferService.getCompletedTransfers(principal);
    }

}
