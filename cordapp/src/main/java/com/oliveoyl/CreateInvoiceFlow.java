package com.oliveoyl;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.security.PublicKey;
import java.util.List;
import java.util.stream.Collectors;

@InitiatingFlow
@StartableByRPC
public class CreateInvoiceFlow extends FlowLogic<Void> {
    private InvoiceState invoice;

    public CreateInvoiceFlow(InvoiceState invoice) {
        this.invoice = invoice;
    }

    @Suspendable
    public Void call() throws FlowException {
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        List<PublicKey> requiredSigners = invoice.getParticipants().stream()
                .map(AbstractParty::getOwningKey)
                .collect(Collectors.toList());

        TransactionBuilder builder = new TransactionBuilder(notary);
        builder.addOutputState(invoice, InvoiceContract.INVOICE_CONTRACT_ID);
        builder.addCommand(new InvoiceContract.Commands.Create(), requiredSigners);

        builder.verify(getServiceHub());
        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(builder);

        FlowSession toSession = initiateFlow(invoice.getTo());
        SignedTransaction fullySignedTransaction = subFlow(new CollectSignaturesFlow(signedTransaction, ImmutableList.of(toSession)));

        subFlow(new FinalityFlow(fullySignedTransaction));

        return null;
    }
}