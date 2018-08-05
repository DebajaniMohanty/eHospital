package com.oliveoyl.contracts;

import net.corda.core.contracts.CommandData;

public interface CryptoFishyCertificateCommands extends CommandData {
    class Issue implements CryptoFishyCommands {}
}
