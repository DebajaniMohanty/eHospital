package com.oliveoyl;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class CryptoFishy implements LinearState {
    private final int year;
    @NotNull private final Party owner;
    private final String type;
    private final String location;
    private final boolean isFished;
    private final String md5;
    private final long timestamp;
    @NotNull private final Party regulatoryBody;

    private final UniqueIdentifier linearId;

    public CryptoFishy(int year, Party owner, String type, String location, boolean isFished, Party regulatoryBody) {
        this.year = year;
        this.owner = owner;
        this.type = type;
        this.location = location;
        this.isFished = isFished;
        this.regulatoryBody = regulatoryBody;
        this.md5 = null;
        this.timestamp = 0;
        this.linearId = new UniqueIdentifier();
    }

    @ConstructorForDeserialization
    public CryptoFishy(int year, Party owner, String type, String location, boolean isFished, Party regulatoryBody, String md5, long timestamp, UniqueIdentifier linearId) {
        this.year = year;
        this.owner = owner;
        this.type = type;
        this.location = location;
        this.isFished = isFished;
        this.regulatoryBody = regulatoryBody;
        this.md5 = md5;
        this.timestamp = timestamp;
        this.linearId = linearId;
    }

    @NotNull
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(owner);
    }

    public CryptoFishy fish() {
        return new CryptoFishy(year, owner, type, location, true, regulatoryBody, md5, timestamp, linearId);
    }

    public CryptoFishy transfer(Party newOwner) {
        return new CryptoFishy(year, newOwner, type, location, isFished, regulatoryBody, md5, timestamp, linearId);
    }

    public CryptoFishy attachMd5(String md5, long timestamp) {
        return new CryptoFishy(year, owner, type, location, isFished, regulatoryBody, md5, timestamp, linearId);
    }

    public int getYear() {
        return year;
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
    public Party getRegulatoryBody() {
        return regulatoryBody;
    }
    public String getMd5() {
        return md5;
    }
    public long getTimestamp() {
        return timestamp;
    }


    @NotNull
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CryptoFishy that = (CryptoFishy) o;
        return year == that.year &&
                isFished == that.isFished &&
                owner.equals(that.owner) &&
                type.equals(that.type) &&
                location.equals(that.location) &&
                regulatoryBody.equals(that.regulatoryBody) &&
                md5.equals(that.md5) &&
                timestamp == that.timestamp &&
                linearId.equals(that.linearId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, owner, type, location, isFished, regulatoryBody, md5, timestamp, linearId);
    }
}
