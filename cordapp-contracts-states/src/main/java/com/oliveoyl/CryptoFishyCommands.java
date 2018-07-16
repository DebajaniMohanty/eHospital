package com.oliveoyl;

import net.corda.core.contracts.CommandData;

public interface CryptoFishyCommands extends CommandData {
    class Issue implements CryptoFishyCommands {}
    class TransferUnfished implements CryptoFishyCommands {}
    class Fish implements CryptoFishyCommands {}
    class TransferFished implements CryptoFishyCommands {}
}
