package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

public interface TransferDao {
    boolean requestTransfer(Transfer transfer);

    boolean sendTransfer(Transfer transfer);
}
