package com.oliveoyl.flows.cryptofishycertificate;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.oliveoyl.contracts.CryptoFishyCertificateCommands;
import com.oliveoyl.contracts.CryptoFishyCertificateContract;
import com.oliveoyl.states.CryptoFishyCertificate;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import static net.corda.core.contracts.ContractsDSL.requireThat;



public class CryptoFishyCertificateFlow {
    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {

        private final Party otherParty; //Buyer
        private final int year;
        private final String type;
        private final String location;
        private final String md5;
        private final long timestamp;
        private final String generationDate;
        private final String cryptoFishyLinearId;

        public Initiator(Party buyer, int year, String type, String location, String md5, long timestamp, String generationDate, String cryptoFishyLinearId) {
            this.otherParty = buyer;
            this.year = year;
            this.type = type;
            this.location = location;
            this.md5 = md5;
            this.timestamp = timestamp;
            this.generationDate = generationDate;
            this.cryptoFishyLinearId = cryptoFishyLinearId;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {

            // Obtain a reference to the notary we want to use.
            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
            final Party me = getServiceHub().getMyInfo().getLegalIdentities().get(0);

            // Generate an unsigned transaction.
            CryptoFishyCertificate cryptoFishyCertificate = new CryptoFishyCertificate(me, otherParty, year, type, location, md5, timestamp, generationDate, cryptoFishyLinearId, new UniqueIdentifier());

            final Command<CryptoFishyCertificateCommands.Issue> txCommand =
                    new Command<>(new CryptoFishyCertificateCommands.Issue(),
                                  ImmutableList.of(cryptoFishyCertificate.getRegulatorBody().getOwningKey(), cryptoFishyCertificate.getBuyer().getOwningKey()));

            final TransactionBuilder txBuilder = new TransactionBuilder(notary)
                    .addOutputState(cryptoFishyCertificate, CryptoFishyCertificateContract.ID)
                    .addCommand(txCommand);

            // Verify that the transaction is valid.
            txBuilder.verify(getServiceHub());

            // Sign the transaction.
            final SignedTransaction partSignedTx = getServiceHub().signInitialTransaction(txBuilder);

            // Send the state to the counterparty, and receive it back with their signature.
            FlowSession otherPartySession = initiateFlow(otherParty);
            final SignedTransaction fullySignedTx = subFlow(
                    new CollectSignaturesFlow(partSignedTx, ImmutableSet.of(otherPartySession), CollectSignaturesFlow.Companion.tracker()));

            // Notarise and record the transaction in both parties' vaults.
            return subFlow(new FinalityFlow(fullySignedTx));
        }
    }

    @InitiatedBy(Initiator.class)
    public static class Acceptor extends FlowLogic<SignedTransaction> {

        private final FlowSession otherPartyFlow;

        public Acceptor(FlowSession otherPartyFlow) {
            this.otherPartyFlow = otherPartyFlow;
        }

        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {
            class SignTxFlow extends SignTransactionFlow {
                private SignTxFlow(FlowSession otherPartyFlow, ProgressTracker progressTracker) {
                    super(otherPartyFlow, progressTracker);
                }

                @Override
                protected void checkTransaction(SignedTransaction stx) {
                    requireThat(require -> {
                        ContractState output = stx.getTx().getOutputs().get(0).getData();
                        require.using("This must be an CryptoFishyCertificate transaction.", output instanceof CryptoFishyCertificate);
                        return null;
                    });
                }
            }

            return subFlow(new SignTxFlow(otherPartyFlow, SignTransactionFlow.Companion.tracker()));
        }
    }
}
