# Novelty Scoring (beta)

## Introduction

Not all data is equally meaningful. Buried in a sea of mundane data often lies a record which would be very valuable to know about. These records differ from the rest of the data in meaningful ways, and as a result, they are very valuable to identify as quickly as possible.

Finding these "diamonds-in-the-rough" is challenging even when the data is static, because doing so requires using context to understand what is anomalous and what isn't. After all, how novel is an observation that you've never seen before? That will depend on what you've seen previously and what the data _means_. Finding and scoring these novel data observations in a streaming context is even more challenging due to the incremental nature of streaming systems and the high-throughput requirements often demanded of stream processing systems.

This is the problem we created _thatDot Novelty Scoring_ to solve. _Novelty Scoring_ from thatDot operates in an entirely streaming fashion and scores each item that streams through so that other systems can understand how novel each piece of data is that streams through. We do this by building stateful, compressed, graphical models of all incoming data, using _thatDot Connect_ under the hood.

## Usage

There are two ways to pass data into the _Novelty Scoring_ subsystem and receive results: 1.) as a procedure call in a query, and 2.) through the REST API. The REST API is ideal when interacting from outside of a _thatDot Connect_ system using typical REST conventions. Procedure calls are useful when interacting with the novelty subsystem when combining with other _thatDot Connect_ capabilities like Standing Queries.

### Procedure Calls

The _Novelty Scoring_ subsystem is integrated with the Cypher language available with other aspects of _thatDot Connect_. To call the _Novelty Scoring_ system in Cypher, use the expression `CALL beta.novelty.observe(…)`. This uses the Cypher procedure syntax for the `CALL` statement, followed by the name of the procedure, in this case `beta.novelty.observe`.

The `observe` procedure makes a single data observation. It updates the underlying model and computes novelty scores. `observe` takes two arguments: 1.) the name of the novelty context, 2.) an observation consisting of a list of values which together represent a single observation.

The novelty context (1) is a user-chosen name of the model used to separate one model from another. _thatDot Connect_ can support any number of models in the same system as long as their context names are distinct.

The observation (2) is passed in to the `observe` procedure as the second argument. The observation is a list of any type and of any length. The values in this list are currently all treated as strings—so there is no distinction between the following two observations: `['foo', '5', 'bar'] == ['foo', 5, 'bar']`  However, the equivalence of numbers and strings is likely to change in later releases.

The `observe` procedure returns three values:

* `score`: A score representing the novelty calculation for the observed item, given all the previous observations. A score of `1` represents the highest possible novelty. A score of `0` represents the lowest possible novelty.
* `novelComponent`: The index of the item in the original observation list which was the most unusual part of the observation.
* `globalProb`: The probability of this exact observation (the entire list), given this and all previous observations.

### Examples

Putting this all together, a few examples of a query issued to the _Novelty Scoring_ system via a procedure call:

```cypher
CALL beta.novelty.observe("foo", ["a", "b", "c"])
```

```cypher
CALL beta.novelty.observe("any name you choose", ["one", 2, "3"]) 
YIELD novelComponent 
RETURN novelComponent
```

```cypher
MATCH (this)-[:connected_to]->(that)
CALL beta.novelty.observe(this.contextName, that.dataList) 
YIELD score as s, globalProb as gp 
RETURN s / gp
```

### Calling the REST API

The REST API is conceptually the same as the procedure call, but the interface is all that is changed. Full documentation for using the novelty system via the REST API can be found alongside the rest of the REST API documentation shipped with each instance of _thatDot Connect_.

## Novelty Scoring In Beta

Use of the _Novelty Scoring_ system during its Beta period is subject to some caveats:

* Many other detailed computations and aspects of the system are available, but not necessarily documented or easily discovered. Contact us to learn more and discuss specifics.
* Throughput per novelty context is limited and not entirely scalable—though some mitigations exist. The current implementation linearizes all observations fed in to a single novelty context so that they have a total ordering. This requires that all observations to the same context go through a single bottleneck during processing. After this first step of novelty calculation, the rest of the computation occurs in parallel. In practice, this limits the throughput of a single novelty context to approximately 2,000 observations per second, depending on the underlying hardware. Short term mitigations to support higher throughput exist, and future versions of the Novelty Scoring system are planned to avoid this limitation.
* Rapid successive observations (including via the "bulk" enpoint) may produce rare errors in the computed scores.
* To make good use of the novelty system during beta, we are exposing more of the internal state and calculations so that expert users can make their own interpretations of the intermediate stages of novelty calculation.
* Visualization of and custom processing with the underlying model is available through _thatDot Connect_.
