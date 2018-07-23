package com.oliveoyl;

import com.google.common.collect.ImmutableList;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.UniqueIdentifier;
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
    public void test() throws Exception {
        IssueCryptoFishyFlow flow = new IssueCryptoFishyFlow("albacore", "manilla");
        CordaFuture<SignedTransaction> future = a.startFlow(flow);
        network.runNetwork();
        SignedTransaction tx = future.get();
        assertEquals(0, tx.getInputs().size());
        assertEquals(1, tx.getTx().getOutputs().size());
    }

//    @Test
//    public void test() throws Exception {
//        IssueCryptoFishyFlow flow = new IssueCryptoFishyFlow("albacore", "manilla");
//        CordaFuture<SignedTransaction> future = a.startFlow(flow);
//        SignedTransaction tx = future.get();
//        FishCryptoFishyFlow fishFlow = new FishCryptoFishyFlow;
//        CordaFuture<SignedTransaction> future2 = a.startFlow(fishFlow);
//        network.runNetwork();
//        SignedTransaction tx = future2.get();
//        assertEquals(0, tx.getInputs().size());
//        assertEquals(1, tx.getTx().getOutputs().size());
//
//    }
}
