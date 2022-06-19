package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.dto.TransferDTO;
import org.springframework.data.relational.core.sql.SQL;

import java.sql.SQLException;
import java.util.List;

public interface TransferDao {
    boolean requestTransfer(Transfer transfer) throws SQLException;

    boolean sendTransfer(Transfer transfer) throws SQLException;

    boolean approveTransfer(Transfer transfer) throws SQLException;

    boolean rejectTransfer(Transfer transfer) throws SQLException;

    List<TransferDTO> getCompletedTransfers(Long id);

    List<TransferDTO> getPendingTransfers(Long id);

    TransferDTO getTransferDTOById(Long id);

    Transfer getTransferById(Long id);
}
