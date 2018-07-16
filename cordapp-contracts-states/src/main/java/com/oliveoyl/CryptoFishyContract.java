package com.oliveoyl;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;

public class CryptoFishyContract implements Contract {
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        isFishy(tx);
    }

    private void isFishy(LedgerTransaction tx) throws IllegalArgumentException {

    }
}
