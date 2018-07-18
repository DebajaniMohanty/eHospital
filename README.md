# CryptoFishies

CryptoFishies is a CorDapp for managing fishing rights.

## Architecture

### States

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

Initially, `isFished == false`. The state represents the right to fish a fish of the given type in the given location.

After fishing the corresponding fish, we update the state to `isFished == true`. The state represents a fish of the given type 
fished in the given location.

By requiring that a `CryptoFish` state of the correct type and location is transferred whenever a fish is sold, we prevent 
overfishing.

### Flows

TODO
