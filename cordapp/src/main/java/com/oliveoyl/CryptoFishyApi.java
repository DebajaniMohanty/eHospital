package com.oliveoyl;

import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Path("cryptofishy")
public class CryptoFishyApi {
    private final CordaRPCOps rpcOps;

    public CryptoFishyApi(CordaRPCOps rpcOps) {
        this.rpcOps = rpcOps;
    }

    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public String myIdentity() {
        return rpcOps.nodeInfo().getLegalIdentities().get(0).getName().getOrganisation();
    }

    @GET
    @Path("peers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getPeers() {
        List<NodeInfo> nodeInfoSnapshot = rpcOps.networkMapSnapshot();
        return nodeInfoSnapshot
                .stream()
                .map(node -> node.getLegalIdentities().get(0).getName().getOrganisation())
                .collect(toList());
    }

    @GET
    @Path("cryptofishies")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<CryptoFishy>> cryptofishies() {
        return rpcOps.vaultQuery(CryptoFishy.class).getStates();
    }

    @GET
    @Path("issue-cryptofishy")
    @Produces(MediaType.APPLICATION_JSON)
    public String issueCryptofishy(@QueryParam("owner") String ownerString, @QueryParam("type") String type, @QueryParam("location") String location) throws Exception {
        Party owner = rpcOps.partiesFromName(ownerString, false).iterator().next();
        rpcOps.startFlowDynamic(IssueCryptoFishyFlow.class, owner, type, location).getReturnValue().get();
        return "Success.";
    }

    @GET
    @Path("fish-cryptofishy")
    @Produces(MediaType.APPLICATION_JSON)
    public String fishCryptofishy(@QueryParam("id") String idString) throws Exception {
        UniqueIdentifier id = UniqueIdentifier.Companion.fromString(idString);
        rpcOps.startFlowDynamic(FishCryptoFishyFlow.class, id).getReturnValue().get();
        return "Success.";
    }

    @GET
    @Path("transfer-cryptofishy")
    @Produces(MediaType.APPLICATION_JSON)
    public String transferCryptofishy(@QueryParam("id") String idString, @QueryParam("newOwner") String newOwnerString) throws Exception {
        UniqueIdentifier id = UniqueIdentifier.Companion.fromString(idString);
        Party newOwner = rpcOps.partiesFromName(newOwnerString, false).iterator().next();
        rpcOps.startFlowDynamic(TransferCryptoFishyFlow.class, id, newOwner).getReturnValue().get();
        return "Success.";
    }
}
