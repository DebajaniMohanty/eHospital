package com.oliveoyl;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

public class CryptoFishyContract implements Contract {
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        isFishy(tx);
    }

    private void isFishy(LedgerTransaction tx) throws IllegalArgumentException {
        CommandWithParties<CryptoFishyCommands> command = requireSingleCommand(tx.getCommands(), CryptoFishyCommands.class);
        CryptoFishyCommands commandType = command.getValue();

        if (commandType instanceof CryptoFishyCommands.Issue) verifyIssue(tx, command);
        else if (commandType instanceof CryptoFishyCommands.Fish) verifyFishCommand(tx, command);
        else if (commandType instanceof CryptoFishyCommands.TransferFished) verifyTransferFished(tx, command);
        else if (commandType instanceof CryptoFishyCommands.TransferUnfished) verifyTransferUnfished(tx, command);
        else throw new IllegalArgumentException("Command is not a CryptoFishyCommand");
    }

    private void verifyTransferUnfished(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            // Generic constraints over Transaction Transfering a Fish right.
            require.using("A Fishing right TransferUnfished transaction should only consume one input state.", tx.getInputs().size() == 1);
            require.using("A Fishing right TransferUnfished transaction should only create one output state.", tx.getOutputs().size() == 1);
            final CryptoFishy in = tx.inputsOfType(CryptoFishy.class).get(0);
            final CryptoFishy out = tx.outputsOfType(CryptoFishy.class).get(0);
            require.using("Only the Fishing right owner property may change in a TransferUnfished Transaction.", in == out.transfer(in.getOwner()));
            require.using("The Fishing right owner property must change in a TransferUnfished transaction.", in.getOwner() != out.getOwner());
            require.using("Only two signers (the current owner and new one) in the TransferUnfished transaction.", command.getSigners().size() == 2);
            final Party currentOwner = in.getOwner();
            final Party newOwner = out.getOwner();
            require.using("The current Fishing right owner and new owner must be signers in a TransferUnfished Transaction.",
                    command.getSigners().containsAll(ImmutableList.of(currentOwner.getOwningKey(), newOwner.getOwningKey())));

            // CryptoFishy specific constraints.
            require.using("The Year value must be non-negative.", in.getYear() > 0);
            require.using("The current Owner must not be null.", in.getOwner() != null);
            require.using("The new Owner must not be null.", out.getOwner() != null);
            require.using("The Type value must not be blank or empty.", !in.getType().isEmpty());
            require.using("The Location value must not be blank or empty.", !in.getLocation().isEmpty());
            require.using("The isFished field for Fishing right must be false before and after TransferUnfished Transaction.", (!in.isFished() && !out.isFished()));

            return null;
        });

    }

    private void verifyTransferFished(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            // Generic constraints over Transaction Transfering a Fish right.
            require.using("A Fishing right TransferFished transaction should only consume one input state.", tx.getInputs().size() == 1);
            require.using("A Fishing right TransferFished transaction should only create one output state.", tx.getOutputs().size() == 1);
            final CryptoFishy in = tx.inputsOfType(CryptoFishy.class).get(0);
            final CryptoFishy out = tx.outputsOfType(CryptoFishy.class).get(0);
            require.using("Only the Fishing right owner property may change in a TransferFished transaction.", in == out.transfer(in.getOwner()));
            require.using("The Fishing right owner property must change in a TransferFished transaction.", in.getOwner() != out.getOwner());
            require.using("Only two signers (the current owner and new one) in the TransferFished transaction.", command.getSigners().size() == 2);
            final Party currentOwner = in.getOwner();
            final Party newOwner = out.getOwner();
            require.using("The current Fishing right owner and new owner must be signers in a TransferFished Transaction..",
                    command.getSigners().containsAll(ImmutableList.of(currentOwner.getOwningKey(), newOwner.getOwningKey())));

            // CryptoFishy specific constraints.
            require.using("The Year value must be non-negative.", in.getYear() > 0);
            require.using("The current Owner must not be null.", in.getOwner() != null);
            require.using("The new Owner must not be null.", out.getOwner() != null);
            require.using("The Type value must not be blank or empty.", !in.getType().isEmpty());
            require.using("The Location value must not be blank or empty.", !in.getLocation().isEmpty());
            require.using("The isFished field for Fishing right must be true before and after TransferFished Transaction.", (in.isFished() && out.isFished()));

            return null;
        });
    }

    private void verifyFishCommand(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException  {
        requireThat(require -> {
            // Generic constraints over Transaction Transfering a Fish right.
            require.using("A Fishing right Fish transaction should only consume one input state.", tx.getInputs().size() == 1);
            require.using("A Fishing right Fish transaction should only create one output state.", tx.getOutputs().size() == 1);
            final CryptoFishy in = tx.inputsOfType(CryptoFishy.class).get(0);
            final CryptoFishy out = tx.outputsOfType(CryptoFishy.class).get(0);
            require.using("The Fishing right owner property must not be change in a Fish transaction.", in.getOwner() != out.getOwner());
            require.using("Only one signer (the current owner) in the Fish transaction.", command.getSigners().size() == 1);
            final Party owner = in.getOwner();
            require.using("The Fishing right owner must be the only one signer.", command.getSigners().containsAll(ImmutableList.of(owner.getOwningKey())));

            // CryptoFishy specific constraints.
            require.using("The Year value must be non-negative.", in.getYear() > 0);
            require.using("The in and out state Year field mus be the same value", in.getYear() == out.getYear());
            require.using("The current Owner must not be null.", in.getOwner() != null);
            require.using("The in and out state Owner field must be the same value", in.getOwner().equals(out.getOwner()));
            require.using("The Type value must not be blank or empty.", !in.getType().isEmpty());
            require.using("The in and out state Type field mus be the same value", in.getType().equals(out.getType()));
            require.using("The Location value must not be blank or empty.", !in.getLocation().isEmpty());
            require.using("The in and out state Location field mus be the same value", in.getLocation().equals(out.getLocation()));
            require.using("The Fishing right must not be used (fished), isFished field on CryptoFishy state must be false.", !in.isFished());
            require.using("The isFished field for Fishing right must be false before fishing and after fishing must be true.", (!in.isFished() && out.isFished()));

            return null;
        });
    }

    private void verifyIssue(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            // Generic constraints over Transaction Issuing a Fish right.
            // TODO check if issuer is regulator
            require.using("No inputs should be consumed issuing a Fishing right.", tx.getInputs().isEmpty());
            require.using("Only one output should be created when submitting a Fishing right.", tx.getOutputs().size() == 1);
            // Only one CryptoFishy output
            final CryptoFishy out = tx.outputsOfType(CryptoFishy.class).get(0);
            require.using("Only one signer in the transaction.", command.getSigners().size() == 1);
            final Party regulatoryBody = out.getRegulatoryBody();
            final List<PublicKey> requiredSigners = ImmutableList.of(regulatoryBody.getOwningKey());
            require.using("The Fishing right owner (regulator) must be the only one signer.", command.getSigners().containsAll(requiredSigners));

            // CryptoFishy specific constraints.
            require.using("The Year value must be non-negative.", out.getYear() > 0);
            require.using("The Owner must not be null.", out.getOwner() != null);
            require.using("The Type value must not be blank or empty.", !out.getType().isEmpty());
            require.using("The Location value must not be blank or empty.", !out.getLocation().isEmpty());
            require.using("The Fishing right must not be used (fished), isFished field on CryptoFishy state must be false.", !out.isFished());

            return null;
        });
    }
}
