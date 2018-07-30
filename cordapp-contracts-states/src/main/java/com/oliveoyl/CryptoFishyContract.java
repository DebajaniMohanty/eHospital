package com.oliveoyl;

import com.google.common.collect.ImmutableList;
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
        else if (commandType instanceof CryptoFishyCommands.Fish) verifyFish(tx, command);
        else if (commandType instanceof CryptoFishyCommands.Transfer) verifyTransfer(tx, command);
    }

    private void verifyTransfer(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException {
        requireThat(require -> {
            require.using("A CryptoFishyTransfer transaction should only consume one input state.", tx.getInputs().size() == 1);
            require.using("A CryptoFishyTransfer transaction should only create one output state.", tx.getOutputs().size() == 1);

            final CryptoFishy in = tx.inputsOfType(CryptoFishy.class).get(0);
            final CryptoFishy out = tx.outputsOfType(CryptoFishy.class).get(0);
            require.using("Only the owner property may change in a CryptoFishyTransfer transaction.", in.equals(out.transfer(in.getOwner())));
            require.using("The owner property must change in a CryptoFishyTransfer transaction.", !(in.getOwner().equals(out.getOwner())));
            require.using("There must only be one signers (the current owner) in a CryptoFishyTransfer transaction.", command.getSigners().size() == 1);
            final Party currentOwner = in.getOwner();
            final Party newOwner = out.getOwner();
            require.using("The current owner and new owner must be signers in a CryptoFishyTransfer transaction.",
                    command.getSigners().containsAll(ImmutableList.of(currentOwner.getOwningKey())));
            require.using("The new owner must not be null in a CryptoFishyTransfer transaction.", out.getOwner() != null);

            return null;
        });
    }

    private void verifyFish(LedgerTransaction tx, CommandWithParties command) throws IllegalArgumentException  {
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
