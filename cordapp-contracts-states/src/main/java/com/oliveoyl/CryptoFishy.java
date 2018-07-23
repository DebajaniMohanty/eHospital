package com.oliveoyl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;

import java.util.List;

public class CryptoFishy implements ContractState {
    private final int year;
    private final Party owner;
    private final String type;
    private final String location;
    private final boolean isFished;

    public CryptoFishy(int year, Party owner, String type, String location, boolean isFished) {
        this.year = year;
        this.owner = owner;
        this.type = type;
        this.location = location;
        this.isFished = isFished;
    }

    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(owner);
    }

    public CryptoFishy trade(Party owner) {
        return new CryptoFishy(year, owner, type, location, isFished);
    }

    public CryptoFishy fish() {
        return new CryptoFishy(year, owner, type, location, true);
    }

    public CryptoFishy throwBackIntoTheSea() {
        return new CryptoFishy(year, owner, type, location, false);
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
}
