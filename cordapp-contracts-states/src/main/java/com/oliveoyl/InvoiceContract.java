package com.oliveoyl;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;

/**
 * Define your contract here.
 */
public class InvoiceContract implements Contract {
    public static final String INVOICE_CONTRACT_ID = "com.oliveoyl.InvoiceContract";

    @Override
    public void verify(LedgerTransaction tx) {}

    public interface Commands extends CommandData {
        class Create implements Commands {}
        class Settle implements Commands {}
        class Sell implements Commands {}
    }
}