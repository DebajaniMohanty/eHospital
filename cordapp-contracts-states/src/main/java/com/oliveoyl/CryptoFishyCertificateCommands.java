package com.oliveoyl;

import net.corda.core.contracts.CommandData;

public interface CryptoFishyCertificateCommands extends CommandData {
    class Issue implements CryptoFishyCommands {}
}
