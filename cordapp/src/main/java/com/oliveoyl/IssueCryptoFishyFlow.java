package com.oliveoyl;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.contracts.Command;
import net.corda.core.flows.FinalityFlow;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;

import com.oliveoyl.CryptoFishyCommands.Issue;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.time.Instant;
import java.util.Date;

public class IssueCryptoFishyFlow extends FlowLogic<SignedTransaction> {
    private final String type;
    private final String location;

    public IssueCryptoFishyFlow(String type, String location) {
        this.type = type;
        this.location = location;
    }

    @Suspendable
    public SignedTransaction call() throws FlowException {
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        Party regulatoryBody = getOurIdentity();

        int year = Date.from(Instant.now()).getYear();
        CryptoFishy cryptoFishy = new CryptoFishy(year, regulatoryBody, type, location, false, regulatoryBody);

        Command<Issue> issueCommand = new Command<>(new Issue(), regulatoryBody.getOwningKey());

        TransactionBuilder builder = new TransactionBuilder(notary);
        builder.addOutputState(cryptoFishy, "com.oliveoyl.CryptoFishyContract");
        builder.addCommand(issueCommand);

        builder.verify(getServiceHub());

        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(builder);

        return subFlow(new FinalityFlow(signedTransaction));
    }
}
