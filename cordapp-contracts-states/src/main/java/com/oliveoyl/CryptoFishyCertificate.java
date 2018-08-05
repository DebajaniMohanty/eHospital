package com.oliveoyl;

import com.google.common.collect.ImmutableList;
import com.oliveoyl.schema.CryptoFishyCertificateSchemaV1;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;


public class CryptoFishyCertificate implements LinearState, QueryableState {

    @NotNull private final Party regulatorBody;
    @NotNull private final Party buyer;
    private final int year;
    private final String type;
    private final String location;
    private final String md5;
    private final Long timestamp;
    private final String generationDate;
    private final String cryptoFishyLinearId;
    private final UniqueIdentifier linearId;

    public CryptoFishyCertificate(Party regulatorBody,
                                  Party buyer,
                                  int year,
                                  String type,
                                  String location,
                                  String md5,
                                  long timestamp,
                                  String generationDate,
                                  String cryptoFishyLinearId,
                                  UniqueIdentifier linearId)
    {

        this.regulatorBody = regulatorBody;
        this.buyer = buyer;
        this.year = year;
        this.type = type;
        this.location = location;
        this.md5 = md5;
        this.timestamp = timestamp;
        this.generationDate = generationDate;
        this.cryptoFishyLinearId = cryptoFishyLinearId;
        this.linearId = linearId;
    }

    public Party getRegulatorBody() { return this.regulatorBody; }
    public Party getBuyer() { return this.buyer; }
    public int getYear() { return this.year; }
    public String getType() { return this.type; }
    public String getLocation() { return this.location; }
    public String getMd5() { return this.md5; }
    public long getTimestamp() { return this.timestamp; }
    public String getGenerationDate() { return generationDate; }
    public String getCryptoFishyLinearId() { return this.cryptoFishyLinearId; }

    @Override public UniqueIdentifier getLinearId() { return this.linearId; }
    @Override public List<AbstractParty> getParticipants() {
        return Arrays.asList(this.regulatorBody, this.buyer);
    }


    @Override public PersistentState generateMappedObject(MappedSchema schema) {
        if (schema instanceof CryptoFishyCertificateSchemaV1) {
            return new CryptoFishyCertificateSchemaV1.PersistentCryptoFishyCertificate(
                    this.regulatorBody.getName().toString(),
                    this.buyer.getName().toString(),
                    this.year,
                    this.type,
                    this.location,
                    this.md5,
                    this.timestamp,
                    this.generationDate,
                    this.cryptoFishyLinearId,
                    this.linearId.getId());
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }


    @Override public Iterable<MappedSchema> supportedSchemas() {
        return ImmutableList.of(new CryptoFishyCertificateSchemaV1());
    }

    @Override
    public String toString() {
        return String.format("CryptoFishyCertificateState(regulatorBody=%s, buyer=%s, year=%s, type=%s, location=%s, " +
                                                         "md5=%s, timestamp=%s, generationDate=%s, cryptoFishyLinearId=%s, linearId=%s)",
                                                          regulatorBody, buyer, year, type, location, md5, timestamp,
                                                          generationDate, cryptoFishyLinearId, linearId);
    }
}
