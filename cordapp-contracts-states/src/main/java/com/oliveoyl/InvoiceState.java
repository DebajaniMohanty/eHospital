package com.oliveoyl;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;

import java.util.Date;
import java.util.List;

public class InvoiceState implements LinearState {
    private Party from;
    private Party to;
    private int value;
    private Date dueDate;
    private List<InvoiceEntry> items;
    private int paid;
    private UniqueIdentifier linearId;

    public InvoiceState(Party from, Party to, int value, Date dueDate, List<InvoiceEntry> items, int paid, UniqueIdentifier linearId) {
        this.from = from;
        this.to = to;
        this.value = value;
        this.linearId = linearId;
        this.dueDate = dueDate;
        this.items = items;
        this.paid = paid;
    }

    public int getValue() {
        return value;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public List<InvoiceEntry> getItems() {
        return items;
    }

    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(from, to);
    }

    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    public int getPaid() {
        return paid;
    }
}