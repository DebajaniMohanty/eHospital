package com.oliveoyl;

import co.paralleluniverse.fibers.Suspendable;
import com.oliveoyl.CryptoFishyCommands.Issue;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.time.Instant;
import java.util.Date;

@InitiatingFlow
@StartableByRPC
public class IssueCryptoFishyFlow extends FlowLogic<SignedTransaction> {
    private final Party owner;
    private final String type;
    private final String location;

    public IssueCryptoFishyFlow(Party owner, String type, String location) {
        this.owner = owner;
        this.type = type;
        this.location = location;
    }

    @Suspendable
    public SignedTransaction call() throws FlowException {
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        int year = Date.from(Instant.now()).getYear();
        CryptoFishy cryptoFishy = new CryptoFishy(year, owner, type, location, false, getOurIdentity());

        TransactionBuilder builder = new TransactionBuilder(notary)
                .addOutputState(cryptoFishy, CryptoFishyContract.ID)
                .addCommand(new Issue(), getOurIdentity().getOwningKey());

        return subFlow(new VerifySignAndFinaliseFlow(builder));
    }
}
