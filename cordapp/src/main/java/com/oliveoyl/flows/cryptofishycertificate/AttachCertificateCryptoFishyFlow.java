package com.oliveoyl.flows.cryptofishycertificate;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.oliveoyl.contracts.CryptoFishyCommands;
import com.oliveoyl.contracts.CryptoFishyContract;
import com.oliveoyl.flows.VerifySignAndFinaliseFlow;
import com.oliveoyl.states.CryptoFishy;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

@InitiatingFlow
@StartableByRPC
public class AttachCertificateCryptoFishyFlow extends FlowLogic<SignedTransaction> {
    private final UniqueIdentifier linearId;
    private final String md5;
    private final long timestamp;

    public AttachCertificateCryptoFishyFlow(UniqueIdentifier linearId, String md5, long timestamp) {
        this.linearId = linearId;
        this.md5 = md5;
        this.timestamp = timestamp;
    }

    @Suspendable
    public SignedTransaction call() throws FlowException {
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(linearId.getId()));
        StateAndRef<CryptoFishy> inputStateAndRef = getServiceHub().getVaultService().queryBy(CryptoFishy.class, queryCriteria).getStates().get(0);

        CryptoFishy outputFishy = inputStateAndRef.getState().getData().attachMd5(md5, timestamp);

        TransactionBuilder builder = new TransactionBuilder(inputStateAndRef.getState().getNotary())
                .addInputState(inputStateAndRef)
                .addOutputState(outputFishy, CryptoFishyContract.ID)
                .addCommand(new CryptoFishyCommands.AttachMd5(), getOurIdentity().getOwningKey());

        return subFlow(new VerifySignAndFinaliseFlow(builder));
    }
}
