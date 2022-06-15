package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exceptions.InsufficientBalanceException;
import com.techelevator.tenmo.exceptions.UserNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.dto.TransferDTO;
import com.techelevator.tenmo.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private TransferService transferService;

    public TransferController() {}

    @PostMapping
    public boolean newTransfer(@Valid @RequestBody Transfer transfer, Principal principal) throws UserNotFoundException, SQLException, InsufficientBalanceException {
        return transferService.sendTransfer(transfer, principal);
    }

    @GetMapping(path = "/completed")
    public List<TransferDTO> getCompletedTransfers(Principal principal) {
        return transferService.getCompletedTransfers(principal);
    }

    @GetMapping(path = "/{id}")
    public TransferDTO getTransferById(@PathVariable Long id) {
        return transferService.getTransferById(id);
    }
}
