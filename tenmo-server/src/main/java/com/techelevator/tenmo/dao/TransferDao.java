package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.security.Principal;
import java.sql.SQLException;
import java.util.List;

public interface TransferDao {
    boolean requestTransfer(Transfer transfer);

    boolean sendTransfer(Transfer transfer) throws SQLException;

    List<Transfer> getCompletedTransfers(Long id);
}
