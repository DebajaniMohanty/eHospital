package com.oliveoyl;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;

public class DerivativeFlow {
    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<Void> {
        @Suspendable
        @Override public Void call() {
            CordaX500Name regulatoryBodyName = new CordaX500Name("Regulatory Body", "London", "UK");
            Party regulatoryBody = getServiceHub().getNetworkMapCache().getPeerByLegalName(regulatoryBodyName);
            FlowSession regulatoryBodySession = initiateFlow(regulatoryBody);
            regulatoryBodySession.send("reporting info");
            return null;
        }
    }
}
