package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {
    boolean requestTransfer(Transfer transfer);

    boolean sendTransfer(Transfer transfer);

    List<Transfer> getCompletedTransfers(Long id);
}
