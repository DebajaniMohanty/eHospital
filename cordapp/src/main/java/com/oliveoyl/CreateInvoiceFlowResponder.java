package com.oliveoyl;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

@InitiatedBy(CreateInvoiceFlow.class)
public class CreateInvoiceFlowResponder extends FlowLogic<Void> {
    private final FlowSession fromSession;

    public CreateInvoiceFlowResponder(FlowSession otherPartyFlow) {
        this.fromSession = otherPartyFlow;
    }

    @Suspendable
    public Void call() throws FlowException {
        class SignTxFlow extends SignTransactionFlow {
            private SignTxFlow(FlowSession fromFlow) {
                super(fromFlow, null);
            }

            protected void checkTransaction(SignedTransaction stx) {
                // TODO("Perform checking.")
            }
        }

        subFlow(new SignTxFlow(fromSession));

        return null;
    }
}
