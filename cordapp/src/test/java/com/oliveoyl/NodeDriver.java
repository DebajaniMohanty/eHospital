package com.oliveoyl;

import com.google.common.collect.ImmutableList;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.identity.CordaX500Name;
import net.corda.testing.driver.DriverParameters;
import net.corda.testing.driver.NodeHandle;
import net.corda.testing.driver.NodeParameters;
import net.corda.testing.node.User;
import com.google.common.collect.ImmutableSet;

import static net.corda.testing.driver.Driver.driver;

public class NodeDriver {
    public static void main(String[] args) {
        final User user = new User("user1", "test", ImmutableSet.of("ALL"));
        driver(new DriverParameters().withStartNodesInProcess(true).withWaitForAllNodesToFinish(true), dsl -> {
                    CordaFuture<NodeHandle> partyAFuture = dsl.startNode(new NodeParameters()
                            .withProvidedName(new CordaX500Name("PartyA", "London", "GB"))
                            .withRpcUsers(ImmutableList.of(user)));
                    CordaFuture<NodeHandle> partyBFuture = dsl.startNode(new NodeParameters()
                            .withProvidedName(new CordaX500Name("PartyB", "New York", "US"))
                            .withRpcUsers(ImmutableList.of(user)));

                    try {
                        dsl.startWebserver(partyAFuture.get());
                        dsl.startWebserver(partyBFuture.get());
                    } catch (Throwable e) {
                        System.err.println("Encountered exception in node startup: " + e.getMessage());
                        e.printStackTrace();
                    }

                    return null;
                }
        );
    }
}
