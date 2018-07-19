# CryptoFishies

CryptoFishies is a CorDapp for managing fishing rights.

## Architecture

It has a single state, `CryptoFish`:

    -------------------
    |                 |
    |   CryptoFish    |
    |                 |
    |   - year        |
    |   - owner       |
    |   - type        |
    |   - location    |
    |   - isFished    |
    |                 |    
    -------------------

Initially, `isFished == false`. The state represents the right to fish a fish of the given type in the given location:

    -------------------------------------------------------------------------------------
    |                                                                                   |
    |    - - - - - - - - - -                                     -------------------    |
    |                                              ▲             |                 |    |
    |    |                 |                       | -►          |   CryptoFish    |    |
    |            NO             -------------------     -►       |                 |    |
    |    |                 |    |      Issue command       -►    |    isFished     |    |
    |          INPUTS           |     signed by issuer     -►    |    == false     |    |
    |    |                 |    -------------------     -►       |                 |    |
    |                                              | -►          |                 |    |
    |    - - - - - - - - - -                       ▼             -------------------    |
    |                                                                                   |
    -------------------------------------------------------------------------------------

After fishing the corresponding fish, we update the state to `isFished == true`. The state represents a fish of the given type 
fished in the given location.

    -------------------------------------------------------------------------------------
    |                                                                                   |
    |    -------------------                                     -------------------    |
    |    |                 |                       ▲             |                 |    |
    |    |   CryptoFish    |                       | -►          |   CryptoFish    |    |
    |    |                 |    -------------------     -►       |                 |    |
    |    |    isFished     |    |      Fish command        -►    |    isFished     |    |
    |    |    == false     |    |     signed by fisher     -►    |    == true      |    |
    |    |                 |    -------------------     -►       |                 |    |
    |    |                 |                       | -►          |                 |    |
    |    -------------------                       ▼             -------------------    |
    |                                                                                   |
    -------------------------------------------------------------------------------------

By requiring that a `CryptoFish` state of the correct type and location be transferred whenever a fish is sold, we prevent 
overfishing:

    -------------------------------------------------------------------------------------
    |                                                                                   |
    |    -------------------                                     -------------------    |
    |    |                 |                       ▲             |                 |    |
    |    |   CryptoFish    |                       | -►          |   CryptoFish    |    |
    |    |                 |    -------------------     -►       |                 |    |
    |    |    isFished     |    |  TransferFished command  -►    |    isFished     |    |
    |    |    == true      |    |     signed by owner      -►    |    == true      |    |
    |    |                 |    -------------------     -►       |                 |    |
    |    |                 |                       | -►          |                 |    |
    |    -------------------                       ▼             -------------------    |
    |                                                                                   |
    -------------------------------------------------------------------------------------

This requires three flows:

* IssueCryptoFishyFlow
* FishCryptoFishyFlow
* TradeFishedCryptoFishyFlow

## Extensions for V2

* Allow fishing rights (i.e. `CryptoFish` states where `isFished == false`) to be bought and sold
