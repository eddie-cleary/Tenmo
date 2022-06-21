package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exceptions.InsufficientBalanceException;
import com.techelevator.tenmo.exceptions.UserNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.dto.TransferDTO;
import com.techelevator.tenmo.model.TransferType;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
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
    private UserDao userDao;

    @Autowired
    private TransferService transferService;

    public TransferController() {}

    // Accepts transfers in request body. Depending if it's a request or a send, or the status, it is handled accordingly.
    @PostMapping
    public boolean newTransfer(@Valid @RequestBody Transfer transfer, Principal principal) throws UserNotFoundException, SQLException, InsufficientBalanceException {
        switch(transfer.getType()) {
            case REQUEST:
                switch (transfer.getStatus()) {
                    case APPROVED:
                        return transferService.approveTransfer(transfer, principal);
                    case REJECTED:
                        return transferService.rejectTransfer(transfer, principal);
                    case PENDING:
                        return transferService.requestTransfer(transfer, principal);
                }
                return true;
            case SEND:
                return transferService.sendTransfer(transfer, principal);
        }
        return false;
    }

    // Retrieve a list of transfers assigned to logged in user with completed status
    @GetMapping(path = "/completed")
    public List<TransferDTO> getCompletedTransfers(Principal principal) {
        return transferService.getCompletedTransfers(principal);
    }

    // Retrieve a list of transfers the assigned to logged in user with pending status
    @GetMapping(path = "/pending")
    public List<TransferDTO> getPendingTransfers(Principal principal) {
        return transferService.getPendingTransfers(principal);
    }

    // Logged in user can access a transfer if they are the receiver or sender only
    @GetMapping(path = "/{id}")
    public TransferDTO getTransferById(@PathVariable Long id, Principal principal) {
        User receiver = transferService.getTransferDTOById(id).getReceiver();
        User sender = transferService.getTransferDTOById(id).getSender();
        User loggedInUser = userDao.findUserByUsername(principal.getName());
        // Make sure logged in user is either the sender or receiver in order to access the transfer.
        if (loggedInUser.getId().equals(receiver.getId()) || loggedInUser.getId().equals(sender.getId())) {
            return transferService.getTransferDTOById(id);
        }
        throw new DataRetrievalFailureException("Failure accessing transfer.");
    }
}
