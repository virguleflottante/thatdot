

# Quick Start

## Access

Our development servers are accessible at `https://_.dev.thatdot.com` where the underscore `_` is your specific instance identifier.

Accessing `https://_.dev.thatdot.com/` with your username and password will display the @ref:[Exploration UI](../usage/exploration_ui.md). The query box in the top of the Exploration UI will allow you to type in any query to input data or get data out. Queries that return nodes will be rendered in the large canvas that makes up most of the Exploration UI.

Accessing `https://_.dev.thatdot.com/docs` with your username and password will display the @ref:[REST API Reference Documentation](../usage/rest_api.md). APIs can be used directly from the documentation pages, or called interactively or programmatically in the usual fashion.

## Creating Data

A simple example of loading static data can be done live in the Exploration UI query bar. 

```cypher
CREATE (me {name: "Me"})-[:is_using]->(thatDot {name: "thatDot Connect"})-[:can_produce]->(e {name: "Real-time Events"})-[:triggered_from]->(d {name: "Data"})<-[:produces]-(me) RETURN me, thatDot, e, d
```

More thorough examples of creating data by hand, by files, and by streams is shown in the @ref[loading data tutorials](../tutorials/loading_data.md)

## Streaming Ingest and Real-Time Event Production

The primary use of _Connect_ begins by hooking the system to a queue of data meant for streaming consumption. Details for getting started with data streams are provided in the @ref[Streaming Data Ingest Tutorial](../tutorials/loading_data.md#streaming-data-ingest) and the `Ingest Sources` section of the @ref[REST API Reference Documentation](../reference/index.md).

With streaming data provided to the system, a @ref[Standing Query](concepts.md#real-time-pattern-matching-with-standing-queries) can be set to find complex patterns in a data stream. Each time the pattern is matched, the resulting data will be used to trigger an action defined together with that Standing Query.