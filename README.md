# CryptoFishies

CryptoFishies is a CorDapp for managing fishing rights.

## Running the nodes

Run the `NodeDriver` run configuration from IntelliJ. This starts four nodes:

* `RegulatoryBody`
* `FishermanOne`
* `FishermanTwo`
* `Buyer`

## Front-ends

* `RegulatoryBody`: http://localhost:10007/web/regulatoryBody
* `FishermanOne`: http://localhost:10011/web/fishermanOne
* `FishermanTwo`: http://localhost:10015/web/fishermanTwo
* `Buyer`: http://localhost:10019/web/buyer

The source files for the front-ends can be found under `cordapp/src/main/resources`.

## APIs

* `GET me`
* `GET peers`
* `GET cryptofishies`
* `GET issue-cryptofishy?owner=<INSERT>&type=<INSERT>&location=<INSERT>`
* `GET fish-cryptofishy?id=<INSERT>`
* `GET transfer-cryptofishy?id=<INSERT>&newOwner=<INSERT>`

### Example usage

* Regulator issues a CryptoFishy to FishermanOne:

  `http://localhost:10007/api/cryptofishy/issue-cryptofishy?owner=FishermanOne&type=albacore&location=manilla`
  
* FishermanOne checks their CryptoFishies:

  `http://localhost:10011/api/cryptofishy/cryptofishies`
  
* Using the `id` from the previous call, FishermanOne fishies a CryptoFishy:

  `http://localhost:10011/api/cryptofishy/fish-cryptofishy?id=8c624b9e-e949-4bbd-bc44-12f647e78da5`
  
* FishermanOne checks their CryptoFishies. The CryptoFishy is now fished:

  `http://localhost:10011/api/cryptofishy/cryptofishies`

* Using the `id` from the previous call, FishermanOne transfers a CryptoFishy to Buyer:

  `http://localhost:10011/api/cryptofishy/transfer-cryptofishy?id=8c624b9e-e949-4bbd-bc44-12f647e78da5&newOwner=Buyer`
  
* FishermanOne checks their CryptoFishies. They no longer have the CryptoFishy:

  `http://localhost:10011/api/cryptofishy/cryptofishies`
  
* Buyer checks their CryptoFishies. They now have the CryptoFishy:
  
  `http://localhost:10019/api/cryptofishy/cryptofishies`

## Architecture

This CorDapp has a single state, `CryptoFish`:

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

* `IssueCryptoFishyFlow`
* `FishCryptoFishyFlow`
* `TransferCryptoFishyFlow`
