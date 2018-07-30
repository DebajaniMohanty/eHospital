package com.oliveoyl;

import com.google.common.collect.ImmutableList;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.transactions.LedgerTransaction;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.StartedMockNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;

public class FlowTests {
    private MockNetwork network;
    private StartedMockNode a;
    private StartedMockNode b;

    @Before
    public void setup() {
        network = new MockNetwork(ImmutableList.of("com.oliveoyl"));
        a = network.createNode();
        b = network.createNode();
        network.runNetwork();
    }

    @After
    public void tearDown() {
        network.stopNodes();
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void issue() throws Exception {
        SignedTransaction tx = issue("albacore", "manilla");

        assertEquals(0, tx.getInputs().size());
        assertEquals(1, tx.getTx().getOutputs().size());
        // TODO: More assertions.
    }

    @Test
    public void transfer() throws Exception {
        SignedTransaction issueTx = issue("albacore", "manilla");
        LedgerTransaction issueLedgerTx = issueTx.toLedgerTransaction(a.getServices());
        UniqueIdentifier linearId = issueLedgerTx.outputsOfType(CryptoFishy.class).get(0).getLinearId();
        SignedTransaction fishTx = fish(linearId);

        assertEquals(1, fishTx.getInputs().size());
        assertEquals(1, fishTx.getTx().getOutputs().size());
        // TODO: More assertions.
    }

    private SignedTransaction issue(String type, String location) throws Exception {
        IssueCryptoFishyFlow flow = new IssueCryptoFishyFlow(type, location);
        CordaFuture<SignedTransaction> future = a.startFlow(flow);
        network.runNetwork();
        return future.get();
    }

    private SignedTransaction fish(UniqueIdentifier linearId) throws Exception {
        FishCryptoFishyFlow flow = new FishCryptoFishyFlow(linearId);
        CordaFuture<SignedTransaction> future = a.startFlow(flow);
        network.runNetwork();
        return future.get();
    }
}
