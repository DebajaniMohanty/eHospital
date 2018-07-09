package com.oliveoyl;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatedBy;

@InitiatedBy(CreateInvoiceFlow.class)
public class CreateInvoiceFlowResponder extends FlowLogic<Void> {

    @Suspendable
    public Void call() throws FlowException {
        return null;
    }
}
