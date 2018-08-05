package com.oliveoyl.schema;

import com.google.common.collect.ImmutableList;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;


public class CryptoFishyCertificateSchemaV1 extends MappedSchema {
    public CryptoFishyCertificateSchemaV1() {
        super(CryptoFishyCertificateSchema.class, 1, ImmutableList.of(PersistentCryptoFishyCertificate.class));
    }

    @Entity
    @Table(name = "cryptofishy_certificates")
    public static class PersistentCryptoFishyCertificate extends PersistentState {
        @Column(name = "regulatorBody") private final String regulatorBody;
        @Column(name = "buyer") private final String buyer;
        @Column(name = "year") private final int year;
        @Column(name = "type") private final String type;
        @Column(name = "location") private final String location;
        @Column(name = "md5") private final String md5;
        @Column(name = "timestamp") private final Long timestamp;
        @Column(name = "generationDate") private final String generationDate;
        @Column(name = "cryptoFishyLinearId") private final String cryptoFishyLinearId;
        @Column(name = "linear_id") private final UUID linearId;

        public PersistentCryptoFishyCertificate(String regulatorBody, String buyer, int year, String type, String location,
                                                String md5, long timestamp, String generationDate, String cryptoFishyLinearId, UUID linearId) {
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

        // Default constructor required by hibernate.
        public PersistentCryptoFishyCertificate() {
            this.regulatorBody = null;
            this.buyer = null;
            this.year = 0;
            this.type = null;
            this.location = null;
            this.md5 = null;
            this.timestamp = null;
            this.generationDate = null;
            this.cryptoFishyLinearId = null;
            this.linearId = null;
        }

        public String getRegulatorBody() { return this.regulatorBody; }
        public String getBuyer() { return this.buyer; }
        public int getYear() { return this.year; }
        public String getType() { return this.type; }
        public String getLocation() { return this.location; }
        public String getMd5() { return md5; }
        public Long getTimestamp() { return timestamp; }
        public String getGenerationDate() { return generationDate; }
        public String getCryptoFishyLinearId() { return cryptoFishyLinearId; }
        public UUID getId() { return linearId; }
    }
}