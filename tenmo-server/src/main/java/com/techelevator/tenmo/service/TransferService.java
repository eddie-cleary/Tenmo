package com.techelevator.tenmo.service;
import com.techelevator.tenmo.dto.TransferDTO;
import com.techelevator.tenmo.exceptions.InsufficientBalanceException;
import com.techelevator.tenmo.exceptions.UserNotFoundException;
import com.techelevator.tenmo.model.Transfer;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;

public interface TransferService {

    public boolean sendTransfer(Transfer transfer, Principal principal) throws InsufficientBalanceException, UserNotFoundException, SQLException;

    public boolean requestTransfer(Transfer transfer, Principal principal) throws UserNotFoundException, SQLException, InsufficientBalanceException ;

    public boolean rejectTransfer(Transfer transfer, Principal principal) throws SQLException ;

    public boolean approveTransfer(Transfer transfer, Principal principal) throws SQLException, UserNotFoundException, InsufficientBalanceException;

    public boolean validateTransfer(Transfer transfer, Principal principal) throws InsufficientBalanceException, UserNotFoundException ;

    public List<TransferDTO> getCompletedTransfers(Principal principal);

    public List<TransferDTO> getPendingTransfers(Principal principal);

    public TransferDTO getTransferDTOByTransferId(Long id);
}


