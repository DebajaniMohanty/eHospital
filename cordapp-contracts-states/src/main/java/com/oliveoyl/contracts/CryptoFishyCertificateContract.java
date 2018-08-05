package com.oliveoyl.contracts;

import com.oliveoyl.states.CryptoFishyCertificate;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.AbstractParty;
import net.corda.core.transactions.LedgerTransaction;

import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;



public class CryptoFishyCertificateContract implements Contract {

    public static final String ID = "com.oliveoyl.contracts.CryptoFishyCertificateContract";


    @Override
    public void verify(LedgerTransaction tx) {

        final CommandWithParties<CryptoFishyCertificateCommands.Issue> command = requireSingleCommand(tx.getCommands(), CryptoFishyCertificateCommands.Issue.class);
        requireThat(require -> {

            // Generic constraints
            require.using("No inputs should be consumed when issuing an CryptoFishyCertificate.", tx.getInputs().isEmpty());
            require.using("Only one output state should be created.", tx.getOutputs().size() == 1);
            final CryptoFishyCertificate out = tx.outputsOfType(CryptoFishyCertificate.class).get(0);
            require.using("The regulatorBody and the buyer cannot be the same entity.", out.getRegulatorBody() != out.getBuyer());
            require.using("All of the participants must be signers.",
                    command.getSigners().containsAll(out.getParticipants().stream().map(AbstractParty::getOwningKey).collect(Collectors.toList())));

            // Specific constraints.
            require.using("The Year value must be non-negative.", out.getYear() > 0);
            require.using("The Type must be non-blank.", !out.getType().isEmpty());
            require.using("The Location must be non-blank.", !out.getLocation().isEmpty());
            require.using("The Date value must be non-negative.", out.getTimestamp() > 0);
            require.using("The Document hash (MD5) value must be non-blank.", !out.getMd5().isEmpty());
            require.using("The Date value must be non-blank.", !out.getGenerationDate().isEmpty());

            return null;
        });
    }

}
