package com.oliveoyl;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;

import javax.validation.constraints.NotNull;
import java.util.List;

public class CryptoFishy implements LinearState {
    private final int year;
    private final Party owner;
    private final String type;
    private final String location;
    private final boolean isFished;
    private final UniqueIdentifier linearId;

    public CryptoFishy(int year, Party owner, String type, String location, boolean isFished) {
        this.year = year;
        this.owner = owner;
        this.type = type;
        this.location = location;
        this.isFished = isFished;
        this.linearId = new UniqueIdentifier();
    }

    @ConstructorForDeserialization
    public CryptoFishy(int year, Party owner, String type, String location, boolean isFished, UniqueIdentifier linearId) {
        this.year = year;
        this.owner = owner;
        this.type = type;
        this.location = location;
        this.isFished = isFished;
        this.linearId = linearId;
    }

    @NotNull
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(owner);
    }

    public CryptoFishy fish() {
        return new CryptoFishy(year, owner, type, location, true, linearId);
    }

    public CryptoFishy transfer(Party newOwner) {
        return new CryptoFishy(year, newOwner, type, location, isFished, linearId);
    }

    public Party getOwner() {
        return owner;
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public boolean isFished() {
        return isFished;
    }

    public int getYear() {
        return year;
    }

    @NotNull
    public UniqueIdentifier getLinearId() {
        return linearId;
    }
}
