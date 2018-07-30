package com.oliveoyl;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.FinalityFlow;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.identity.Party;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

public class TradeCryptoFishyFlow extends FlowLogic<SignedTransaction> {
    private final UniqueIdentifier linearId;
    private final Party newOwner;

    public TradeCryptoFishyFlow(UniqueIdentifier linearId, Party newOwner) {
        this.linearId = linearId;
        this.newOwner = newOwner;
    }

    public SignedTransaction call() throws FlowException {
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(linearId.getId()));
        StateAndRef<CryptoFishy> inputStateAndRef = getServiceHub().getVaultService().queryBy(CryptoFishy.class, queryCriteria).getStates().get(0);

        CryptoFishy inputFishy = inputStateAndRef.getState().getData();
        CryptoFishy outputFishy = inputFishy.transfer(newOwner);

        Command<CryptoFishyCommands.TransferFished> transferCommand = new Command<>(new CryptoFishyCommands.TransferFished(), getOurIdentity().getOwningKey());

        TransactionBuilder builder = new TransactionBuilder(inputStateAndRef.getState().getNotary())
                .addInputState(inputStateAndRef)
                .addOutputState(outputFishy, "com.oliveoyl.CryptoFishyContract")
                .addCommand(transferCommand);

        builder.verify(getServiceHub());

        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(builder);

        return subFlow(new FinalityFlow(signedTransaction));
    }
}
