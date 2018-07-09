package com.oliveoyl;

import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;

import java.util.List;

public class DerivativeState implements ContractState {
    private List<AbstractParty> participants;
    private String uniqueTransactionIdentifier;
    private String legalEntityIdentifier;

    public DerivativeState(List<AbstractParty> participants, String uniqueTransactionIdentifier, String legalEntityIdentifier) {
        this.participants = participants;
        this.uniqueTransactionIdentifier = uniqueTransactionIdentifier;
        this.legalEntityIdentifier = legalEntityIdentifier;
    }

    public List<AbstractParty> getParticipants() {
        return participants;
    }

    public String getLegalEntityIdentifier() {
        return legalEntityIdentifier;
    }

    public String getUniqueTransactionIdentifier() {
        return uniqueTransactionIdentifier;
    }
}