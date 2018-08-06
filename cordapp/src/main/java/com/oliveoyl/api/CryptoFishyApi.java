package com.oliveoyl.api;

import com.google.common.collect.ImmutableMap;
import com.oliveoyl.flows.cryptofishy.FishCryptoFishyFlow;
import com.oliveoyl.flows.cryptofishy.IssueCryptoFishyFlow;
import com.oliveoyl.flows.cryptofishy.TransferCryptoFishyFlow;
import com.oliveoyl.states.CryptoFishy;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.Builder;
import net.corda.core.node.services.vault.CriteriaExpression;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;

@Path("cryptofishy")
public class CryptoFishyApi {

    static private final Logger logger = LoggerFactory.getLogger(CryptoFishyApi.class);

    private final CordaRPCOps rpcOps;
    private final CordaX500Name myLegalName;

    public CryptoFishyApi(CordaRPCOps rpcOps) {
        this.rpcOps = rpcOps;
        this.myLegalName = rpcOps.nodeInfo().getLegalIdentities().get(0).getName();
    }

    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, CordaX500Name> myIdentity() {
        return ImmutableMap.of("me", rpcOps.nodeInfo().getLegalIdentities().get(0).getName());
    }

    @GET
    @Path("regulatory-body")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> getRegulatorBodyName() throws Exception {
        List<NodeInfo> nodeInfoSnapshot = rpcOps.networkMapSnapshot();
        List<String> lista =  nodeInfoSnapshot
                                .stream()
                                .map(node -> node.getLegalIdentities().get(0).getName().getOrganisation())
                                .filter(name -> name.contains("Regulator"))
                                .collect(toList());
        if(lista.size() < 1) {
            throw new Exception("RegulatorBody not found");
        }

        return ImmutableMap.of("regulatorBody", lista.get(0));
    }

    @GET
    @Path("fishermen")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getFishermen() {
        List<NodeInfo> nodeInfoSnapshot = rpcOps.networkMapSnapshot();
        return nodeInfoSnapshot
                .stream()
                .map(node -> node.getLegalIdentities().get(0).getName().getOrganisation())
                .filter(name -> name.contains("Fisherman"))
                .collect(toList());
    }

    @GET
    @Path("other-fishermen")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getOtherFishers() {
        List<NodeInfo> nodeInfoSnapshot = rpcOps.networkMapSnapshot();
        return nodeInfoSnapshot
                .stream()
                .map(node -> node.getLegalIdentities().get(0).getName().getOrganisation())
                .filter(name -> name.contains("Fisherman") && !name.equals(myLegalName.getOrganisation()))
                .collect(toList());
    }

    @GET
    @Path("buyers")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getBuyers() {
        List<NodeInfo> nodeInfoSnapshot = rpcOps.networkMapSnapshot();
        return nodeInfoSnapshot
                .stream()
                .map(node -> node.getLegalIdentities().get(0).getName().getOrganisation())
                .filter(name -> name.contains("Buyer"))
                .collect(toList());
    }

    @GET
    @Path("cryptofishies")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<CryptoFishy>> cryptofishies() {
        return rpcOps.vaultQuery(CryptoFishy.class).getStates();
    }


    @GET
    @Path("consumed-cryptofishies")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<CryptoFishy>> consumedCryptofishies() {
        Field isFished = null;
        try {
            isFished = CryptoFishy.class.getDeclaredField("isFished");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        CriteriaExpression isFishedCriteriaExpression = Builder.notEqual(isFished, true);
        QueryCriteria isFishedCriteria = new QueryCriteria.VaultCustomQueryCriteria(isFishedCriteriaExpression, Vault.StateStatus.CONSUMED);
        return rpcOps.vaultQueryByCriteria(isFishedCriteria, CryptoFishy.class).getStates();
    }

    @GET
    @Path("issue-cryptofishy")
    public Response issueCryptofishy(@QueryParam("owner") String ownerString, @QueryParam("type") String type, @QueryParam("location") String location) {
        Party owner = rpcOps.partiesFromName(ownerString, false).iterator().next();
        try {

            final SignedTransaction signedTx = rpcOps.startFlowDynamic(IssueCryptoFishyFlow.class, owner, type, location).getReturnValue().get();
            final String msg = String.format("Transaction id %s committed to ledger.\n", signedTx.getId());
            return Response.status(CREATED).entity(msg).build();

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }
    }

    @GET
    @Path("fish-cryptofishy")
    public Response fishCryptofishy(@QueryParam("id") String idString) {
        UniqueIdentifier id = UniqueIdentifier.Companion.fromString(idString);
        try {
            final SignedTransaction signedTx = rpcOps.startFlowDynamic(FishCryptoFishyFlow.class, id).getReturnValue().get();
            final String msg = String.format("Transaction id %s committed to ledger.\n", signedTx.getId());
            return Response.status(CREATED).entity(msg).build();

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }
    }

    @GET
    @Path("transfer-cryptofishy")
    public Response transferCryptofishy(@QueryParam("id") String idString, @QueryParam("newOwner") String newOwnerString) {
        UniqueIdentifier id = UniqueIdentifier.Companion.fromString(idString);
        Party newOwner = rpcOps.partiesFromName(newOwnerString, false).iterator().next();
        try {
            final SignedTransaction signedTx = rpcOps.startFlowDynamic(TransferCryptoFishyFlow.class, id, newOwner).getReturnValue().get();
            final String msg = String.format("Transaction id %s committed to ledger.\n", signedTx.getId());
            return Response.status(CREATED).entity(msg).build();

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }
    }
}
