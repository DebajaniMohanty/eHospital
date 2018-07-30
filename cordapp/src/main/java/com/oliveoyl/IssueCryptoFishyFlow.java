package com.oliveoyl;

import co.paralleluniverse.fibers.Suspendable;
import com.oliveoyl.CryptoFishyCommands.Issue;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
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

        int year = Date.from(Instant.now()).getYear();
        CryptoFishy cryptoFishy = new CryptoFishy(year, getOurIdentity(), type, location, false, getOurIdentity());

        TransactionBuilder builder = new TransactionBuilder(notary)
                .addOutputState(cryptoFishy, CryptoFishyContract.ID)
                .addCommand(new Issue(), getOurIdentity().getOwningKey());

        return subFlow(new VerifySignAndFinaliseFlow(builder));
    }
}
