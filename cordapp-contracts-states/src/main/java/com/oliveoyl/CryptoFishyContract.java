package com.oliveoyl;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.transactions.LedgerTransaction;

import java.util.List;

public class CryptoFishyContract implements Contract {
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        isFishy(tx);
    }

    private void isFishy(LedgerTransaction tx) throws IllegalArgumentException {

        List<CommandWithParties<CommandData>> commands = tx.getCommands();
        if(commands == null || commands.size() != 1) {
            throw new IllegalArgumentException("There must be exactly one command");
        }
        if (! (commands.get(0).component3() instanceof CryptoFishyCommands)) {
            throw new IllegalArgumentException("Command is not a CryptoFishyCommand");
        }
        CryptoFishyCommands command = (CryptoFishyCommands) commands.get(0).component3();

        if (command instanceof CryptoFishyCommands.Issue) {
            verifyIssue(tx);
        }
        else if (command instanceof CryptoFishyCommands.Fish) {
            verifyFishCommand(tx);
        }
        else if (command instanceof CryptoFishyCommands.TransferFished) {
            verifyTransferFished(tx);
        }
        else if (command instanceof CryptoFishyCommands.TransferUnfished) {
            verifyTransferUnfished(tx);
        }
    }

    private void verifyTransferUnfished(LedgerTransaction tx) throws IllegalArgumentException {

    }

    private void verifyTransferFished(LedgerTransaction tx) throws IllegalArgumentException {

    }

    private void verifyFishCommand(LedgerTransaction tx) throws IllegalArgumentException  {

    }

    private void verifyIssue(LedgerTransaction tx) throws IllegalArgumentException {

        // check no inputs
        List<ContractState> inputStates = tx.getInputStates();
        if (inputStates.size() != 0) {
            throw new IllegalArgumentException("Issuing transaction must not have input states.");
        }

        // TODO check if issuer is regulator

        // check exactly one fish as output
        // check output !isFished


    }
}
