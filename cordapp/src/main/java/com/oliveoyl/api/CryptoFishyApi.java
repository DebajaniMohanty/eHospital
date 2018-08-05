package com.oliveoyl.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.oliveoyl.flows.cryptofishy.FishCryptoFishyFlow;
import com.oliveoyl.flows.cryptofishy.IssueCryptoFishyFlow;
import com.oliveoyl.flows.cryptofishy.TransferCryptoFishyFlow;
import com.oliveoyl.flows.cryptofishycertificate.AttachCertificateCryptoFishyFlow;
import com.oliveoyl.flows.cryptofishycertificate.CryptoFishyCertificateFlow;
import com.oliveoyl.schema.CryptoFishyCertificateSchemaV1;
import com.oliveoyl.states.CryptoFishy;
import com.oliveoyl.states.CryptoFishyCertificate;
import com.oliveoyl.utils.Base64File;
import com.oliveoyl.utils.CryptoFishyCertificateInfo;
import com.oliveoyl.utils.MD5Utils;
import com.oliveoyl.utils.PDFUtils;
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
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.Response.Status.*;

@Path("cryptofishy")
public class CryptoFishyApi {

    static private final Logger logger = LoggerFactory.getLogger(CryptoFishyApi.class);

    private final CordaRPCOps rpcOps;
    private final CordaX500Name myLegalName;
    private final List<String> serviceNames = ImmutableList.of("Notary Service", "Network Map Service");

    public CryptoFishyApi(CordaRPCOps rpcOps) {
        this.rpcOps = rpcOps;
        this.myLegalName = rpcOps.nodeInfo().getLegalIdentities().get(0).getName();
    }

//    @GET
//    @Path("me")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String myIdentity() {
//        return rpcOps.nodeInfo().getLegalIdentities().get(0).getName().getOrganisation();
//    }

    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, CordaX500Name> myIdentity() {
        return ImmutableMap.of("me", rpcOps.nodeInfo().getLegalIdentities().get(0).getName());
    }

    @GET
    @Path("regulatorBody-name")
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
    @Path("buyer-name")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> getBuyerName() throws Exception {
        List<NodeInfo> nodeInfoSnapshot = rpcOps.networkMapSnapshot();
        List<String> lista =  nodeInfoSnapshot
                .stream()
                .map(node -> node.getLegalIdentities().get(0).getName().getOrganisation())
                .filter(name -> name.contains("Buyer"))
                .collect(toList());
        if(lista.size() < 1) {
            throw new Exception("Buyer not found");
        }

        return ImmutableMap.of("buyer", lista.get(0));
    }

//    @GET
//    @Path("peers")
//    @Produces(MediaType.APPLICATION_JSON)
//    public List<String> getPeers() {
//        List<NodeInfo> nodeInfoSnapshot = rpcOps.networkMapSnapshot();
//        return nodeInfoSnapshot
//                .stream()
//                .map(node -> node.getLegalIdentities().get(0).getName().getOrganisation())
//                .collect(toList());
//    }

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
    @Path("others-fishermen")
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
    @Path("create-cryptofishy")
    public Response createCryptofishy(@QueryParam("owner") String ownerString, @QueryParam("type") String type, @QueryParam("location") String location) {
        Party owner = rpcOps.partiesFromName(ownerString, false).iterator().next();
        try {

            final SignedTransaction signedTx = rpcOps.startFlowDynamic(AttachCertificateCryptoFishyFlow.class, owner, type, location).getReturnValue().get();
            final String msg = String.format("Transaction id %s committed to ledger.\n", signedTx.getId());
            return Response.status(CREATED).entity(msg).build();

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(ex.getMessage(), ex);
            return Response.status(BAD_REQUEST).entity(msg).build();
        }
    }

//    @GET
//    @Path("issue-cryptofishy")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String issueCryptofishy(@QueryParam("owner") String ownerString, @QueryParam("type") String type, @QueryParam("location") String location) throws Exception {
//        Party owner = rpcOps.partiesFromName(ownerString, false).iterator().next();
//        rpcOps.startFlowDynamic(IssueCryptoFishyFlow.class, owner, type, location).getReturnValue().get();
//        return "Success.";
//    }

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

//    @GET
//    @Path("fish-cryptofishy")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String fishCryptofishy(@QueryParam("id") String idString) throws Exception {
//        UniqueIdentifier id = UniqueIdentifier.Companion.fromString(idString);
//        rpcOps.startFlowDynamic(FishCryptoFishyFlow.class, id).getReturnValue().get();
//        return "Success.";
//    }

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

//    @GET
//    @Path("transfer-cryptofishy")
//    @Produces(MediaType.APPLICATION_JSON)
//    public String transferCryptofishy(@QueryParam("id") String idString, @QueryParam("newOwner") String newOwnerString) throws Exception {
//        UniqueIdentifier id = UniqueIdentifier.Companion.fromString(idString);
//        Party newOwner = rpcOps.partiesFromName(newOwnerString, false).iterator().next();
//        rpcOps.startFlowDynamic(TransferCryptoFishyFlow.class, id, newOwner).getReturnValue().get();
//        return "Success.";
//    }

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


    @GET
    @Path("getDoc")
    @Produces("application/pdf")
    public  Response getDocument(@QueryParam("id") String idString,
                                 @QueryParam("owner") String ownerString,
                                 @QueryParam("otherParty") String otherPartyString) {

        //CryptoFishy linearId
        UniqueIdentifier cryptoFishy_linearId = UniqueIdentifier.Companion.fromString(idString);

        //Getting the regulatoryBody node and Buyer nodes
        Party otherParty = rpcOps.partiesFromName(otherPartyString, false).iterator().next();
        Party owner = rpcOps.partiesFromName(ownerString, false).iterator().next();
        if (owner == null) {
            return Response.status(BAD_REQUEST).entity("Party named " + ownerString + "cannot be found.\n").build();
        }
        if (otherParty == null) {
            return Response.status(BAD_REQUEST).entity("Party named " + otherPartyString + "cannot be found.\n").build();
        }

        //Query to search the CryptoFishy by linearId
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(cryptoFishy_linearId.getId()));
        StateAndRef<CryptoFishy> inputStateAndRef = rpcOps.vaultQueryByCriteria(queryCriteria, CryptoFishy.class).getStates().get(0);

        //Get the CryptoFishy
        CryptoFishy cryptoFishy = inputStateAndRef.getState().getData();
        if(cryptoFishy == null){
            return Response.status(INTERNAL_SERVER_ERROR).entity("ERROR on certificate generation.\n").build();
        }

        //Info for the document name
        String type = cryptoFishy.getType().trim();
        String location = cryptoFishy.getLocation().trim();
        Long timestamp = System.currentTimeMillis();
        Timestamp ts = new Timestamp(timestamp);
        StringBuilder fileName = new StringBuilder();
        fileName.append(timestamp)
                .append("-")
                .append(cryptoFishy.getYear())
                .append("-")
                .append(type)
                .append("-")
                .append(location)
                .append(".pdf");

        //Create the pdf document
        PDFUtils.generatePDFCertificate(cryptoFishy, fileName.toString(), owner.getName().toString());

        //Output the document to certificates/generated directory
        File file = new File("certificates/generated/" + fileName.toString());

        //Response with the pdf document
        Response.ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment; filename=\"" + cryptoFishy.getYear() + "-" + type + "-" + location + ".pdf\"");

        //Hash
        String md5value = MD5Utils.getMD5("certificates/generated/" + fileName.toString());

        try {

            final SignedTransaction signedTxCertificate =
                    rpcOps.startTrackedFlowDynamic(CryptoFishyCertificateFlow.Initiator.class, otherParty, cryptoFishy.getYear(),
                                                   cryptoFishy.getType(), cryptoFishy.getLocation(), md5value, timestamp, ts.toString(), idString)
                            .getReturnValue()
                            .get();

            if(signedTxCertificate == null) {
                return Response.status(BAD_REQUEST).entity("ERROR on certificate generation.\n").build();
            }

            final SignedTransaction signedTxAttachMd5 =
                        rpcOps.startFlowDynamic(AttachCertificateCryptoFishyFlow.class, cryptoFishy_linearId, md5value, timestamp).getReturnValue().get();

            if(signedTxAttachMd5 == null) {
                return Response.status(BAD_REQUEST).entity("ERROR on certificate generation.\n").build();
            }
            final String msg = String.format("Transaction id %s committed to ledger.\n", signedTxCertificate.getId());
            logger.info(msg);

            return Response.status(CREATED).entity(msg).build();

        } catch (Throwable ex) {
            final String msg = ex.getMessage();
            logger.error(msg, ex);
            return Response.status(INTERNAL_SERVER_ERROR).entity("ERROR on certificate generation.\n").build();
        }

        //return response.build();
    }

    @GET
    @Path("certificates")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<CryptoFishyCertificate>> getCertificates() {
        return rpcOps.vaultQuery(CryptoFishyCertificate.class).getStates();

    }

    @GET
    @Path("downloadDoc")
    @Produces("application/pdf")
    public Response downloadDocument(@QueryParam("id") String idString) throws NoSuchFieldException {

        Field field = null;
        field = CryptoFishyCertificateSchemaV1.PersistentCryptoFishyCertificate.class.getDeclaredField("cryptoFishyLinearId");
        CriteriaExpression expresion = Builder.equal(field, idString);
        QueryCriteria queryCriteria = new QueryCriteria.VaultCustomQueryCriteria(expresion);
        StateAndRef<CryptoFishyCertificate> inputStateAndRef = rpcOps.vaultQueryByCriteria(queryCriteria, CryptoFishyCertificate.class).getStates().get(0);

        //Get the CryptoFishy
        CryptoFishyCertificate cryptoFishyCertificate = inputStateAndRef.getState().getData();
        if(cryptoFishyCertificate == null){
            return Response.status(INTERNAL_SERVER_ERROR).entity("ERROR on certificate generation.\n").build();
        }


        String type = cryptoFishyCertificate.getType().trim();
        String location = cryptoFishyCertificate.getLocation().trim();
        StringBuilder fileName = new StringBuilder();
        fileName.append(cryptoFishyCertificate.getTimestamp())
                .append("-")
                .append(cryptoFishyCertificate.getYear())
                .append("-")
                .append(type)
                .append("-")
                .append(location)
                .append(".pdf");

        File file = new File("certificates/generated/" + fileName.toString());

        Response.ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment; filename=\"" + cryptoFishyCertificate.getYear() + "-" + type + "-" + location + ".pdf\"");

        return response.build();
    }

    @GET
    @Path("downloadDoc-fisherman")
    @Produces("application/pdf")
    public Response downloadDocumentForFisherman(@QueryParam("id") String idString) throws NoSuchFieldException {

        //CryptoFishyCertificate linearId
        UniqueIdentifier linearId = UniqueIdentifier.Companion.fromString(idString);

        //Query to search the CryptoFishyCertificate by linearId
        QueryCriteria queryCriteria = new QueryCriteria.LinearStateQueryCriteria(null, ImmutableList.of(linearId.getId()));
        StateAndRef<CryptoFishy> inputStateAndRef = rpcOps.vaultQueryByCriteria(queryCriteria, CryptoFishy.class).getStates().get(0);

        //Get the CryptoFishy
        CryptoFishy cryptoFishy = inputStateAndRef.getState().getData();
        if(cryptoFishy == null){
            return Response.status(INTERNAL_SERVER_ERROR).entity("ERROR on certificate generation.\n").build();
        }

        String type = cryptoFishy.getType().trim();
        String location = cryptoFishy.getLocation().trim();
        StringBuilder fileName = new StringBuilder();
        fileName.append(cryptoFishy.getTimestamp())
                .append("-")
                .append(cryptoFishy.getYear())
                .append("-")
                .append(type)
                .append("-")
                .append(location)
                .append(".pdf");

        File file = new File("certificates/generated/" + fileName.toString());

        Response.ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment; filename=\"" + cryptoFishy.getYear() + "-" + type + "-" + location + ".pdf\"");

        return response.build();
    }

    @POST
    @Path("validateDoc")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response validateDocument(String base64file) {

        byte[] decodedBytes = new byte[0];
        File tmpFile = null;
        try {

            //create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            //convert json string to object
            Base64File file = objectMapper.readValue(base64file, Base64File.class);
            decodedBytes = Base64.decodeBase64(file.getBase64file());

            Long timestamp = System.currentTimeMillis();
            String tmpFilePath = "certificates/tmp/" + timestamp.toString() + ".pdf";
            try (FileOutputStream fileOuputStream = new FileOutputStream(tmpFilePath)) {
                fileOuputStream.write(decodedBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            tmpFile = new File(tmpFilePath);

            CryptoFishyCertificateInfo certificateInfo = PDFUtils.getDocumentInfo(tmpFile);

            if(certificateInfo.equals(null)) {
                System.out.println("ERROR - Null Document MetaInf");
                tmpFile.delete();
                return Response.status(INTERNAL_SERVER_ERROR).entity("ERROR on certificate validation.\n").build();
            }

            String md5value = MD5Utils.getMD5(tmpFilePath);

            CryptoFishyCertificate cryptoFishyCertificate = null;

            Field year = null;
            Field type = null;
            Field location = null;
            Field regulatorBody = null;

            try {

                regulatorBody = CryptoFishyCertificateSchemaV1.PersistentCryptoFishyCertificate.class.getDeclaredField("regulatorBody");
                year = CryptoFishyCertificateSchemaV1.PersistentCryptoFishyCertificate.class.getDeclaredField("year");
                type = CryptoFishyCertificateSchemaV1.PersistentCryptoFishyCertificate.class.getDeclaredField("type");
                location = CryptoFishyCertificateSchemaV1.PersistentCryptoFishyCertificate.class.getDeclaredField("location");

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                tmpFile.delete();
                return Response.status(INTERNAL_SERVER_ERROR).entity("ERROR on certificate validation.\n").build();
            }

            if(regulatorBody.equals(null) || year.equals(null) || type.equals(null) || location.equals(null)){
                tmpFile.delete();
                return Response.status(INTERNAL_SERVER_ERROR).entity("ERROR on certificate generation.\n").build();
            }

            CriteriaExpression regulatorBodyCriteriaExpression = Builder.equal(regulatorBody, certificateInfo.getRegulatorBody());
            CriteriaExpression yearCriteriaExpression = Builder.equal(year, Integer.parseInt(certificateInfo.getYear()));
            CriteriaExpression typeCriteriaExpression = Builder.equal(type, certificateInfo.getType());
            CriteriaExpression locationCriteriaExpression = Builder.equal(location, certificateInfo.getLocation());

            QueryCriteria regulatorBodyCriteria = new QueryCriteria.VaultCustomQueryCriteria(regulatorBodyCriteriaExpression);
            QueryCriteria yearCriteria = new QueryCriteria.VaultCustomQueryCriteria(yearCriteriaExpression);
            QueryCriteria typeCriteria = new QueryCriteria.VaultCustomQueryCriteria(typeCriteriaExpression);
            QueryCriteria locationCriteria = new QueryCriteria.VaultCustomQueryCriteria(locationCriteriaExpression);

            QueryCriteria criteria = regulatorBodyCriteria.and(yearCriteria).and(typeCriteria).and(locationCriteria);

            List<StateAndRef<CryptoFishyCertificate>> stateList = rpcOps.vaultQueryByCriteria(criteria, CryptoFishyCertificate.class).getStates();

            for (StateAndRef<CryptoFishyCertificate> element : stateList) {
                if(element.getState().getData().getMd5().compareTo(md5value) == 0) {
                    System.out.print("File exists in the ledger");
                    tmpFile.delete();
                    return Response.status(OK).entity("Successfully certificate validation.\n").build();
                }
            }

            tmpFile.delete();

        } catch (Exception e) {
            e.printStackTrace();
            tmpFile.delete();
            return Response.status(INTERNAL_SERVER_ERROR).entity("ERROR on certificate validation.\n").build();
        }

        return Response.status(INTERNAL_SERVER_ERROR).entity("Document not valid\n").build();

    }

}
