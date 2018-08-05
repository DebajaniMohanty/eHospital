package com.oliveoyl;

import net.corda.core.contracts.CommandData;

public interface CryptoFishyCommands extends CommandData {
    class Issue implements CryptoFishyCommands {}
    class Transfer implements CryptoFishyCommands {}
    class Fish implements CryptoFishyCommands {}
    class AttachMd5 implements CryptoFishyCommands {}
}