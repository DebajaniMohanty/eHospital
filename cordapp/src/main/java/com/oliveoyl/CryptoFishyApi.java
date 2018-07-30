package com.oliveoyl;

import com.google.common.collect.ImmutableMap;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.messaging.CordaRPCOps;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("crypto-fishy")
public class CryptoFishyApi {
    private final CordaRPCOps rpcOps;

    public CryptoFishyApi(CordaRPCOps rpcOps) {
        this.rpcOps = rpcOps;
    }

    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, CordaX500Name> whoami() {
        return ImmutableMap.of("me", rpcOps.nodeInfo().getLegalIdentities().get(0).getName());
    }
}
