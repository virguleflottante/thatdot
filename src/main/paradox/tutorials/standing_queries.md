# Writing Standing Queries

A _standing query_ is a query that matches some graph structure incrementally while new data is written in. Standing queries report results when the full pattern has been found.

Seeing which standing queries are currently running, or adding/removing a standing query is all done through the REST API, by the endpoints under the "Standing Queries" section in the docs pages shipped with each instance of _thatDot Connect_.

## Syntax and Structure

The first step to making a standing query is determining what the graph pattern is that you
want to watch for. This pattern is expressed using a subset of the same Cypher language that
is used for regular queries. The reasoning behind this is that the _matches produced by a standing
query over a period of time should be the same as the matches produced if the same Cypher query had
been issued in a non-standing fashion after all data has been written in_.

Standing queries have two parts: a "match" query and an "output". The "match" portion defines the structure of what we're looking for. The "output" defines an action to take for each result produced by the "match" query.

### Match

Here is an example of the "match" portion of a standing query:

```cypher
// Standing query to find a person with a named mother and father called "Joe"
MATCH (mother)<-[:has_mother]-(person)-[:has_father]->(father { name: "Joe" })
WHERE exists(mother.name)
RETURN id(person)
```

Standing queries must be of the form `MATCH <pattern> WHERE <constraints> RETURN id(<some-node>)`.
Additionally:

  1. The `<pattern>` must be connected and either linear or tree-shaped when viewed starting at `<some-node>`. The
     nodes in the pattern should not have and labels and edges in the pattern must be directed and
     include exactly one edge type label (eg. `:has_mother`).

  2. the `<constraints>` must be joined with an `AND` statement and in one of the following forms:

     * `nodeName.property = 123` - the property have the value on the right
     * `nodeName.property <> 123` - the property must exist but be different than the value on the right
     * `exists(nodeName.property)` - the property must exist

@@@ note

Several of the restrictions above will be loosened soon, including:

  1. labels will be allowed on node patterns
  2. the graph patterns won't need to be linear or tree-shaped
  3. constraints in `WHERE` will be arbitrary expressions
  4. any number of return values will be supported (including values that aren't IDs)

@@@

### Outputs

Once you've decided what graph structure to watch for, the second half of a standing query is
deciding what to do with the results. This step can be initially skipped as standing query outputs
can always be added even after the query is running, with the `/api/v1/query/standing/{name}/output`
endpoint. The information that is produced for each result includes:

 1. Query data returned from the "match" portion (e.g. the ID of the node).

 2. Meta information

    * `isPositiveMatch`: whether the result is a new match. When this value is false, it signifies that a previously matched result no longer matches
    * `resultId`: a UUID generated for each result. This is useful for processing cancellations.

There are pre-built output adapters for at least the following (this list is continually growing—refer
to the OpenAPI documentation under the standing query section for an exhaustive list):

  * publishing to a Kafka topic
  * publishing to an AWS Kinesis stream
  * publishing to AWS SQS and SNS
  * logging to a file
  * `POST`-ing results to an HTTP endpoint
  * executing another Cypher query

The last of these options is particularly powerful, since it makes it possible to mutate the graph
in a way that can trigger another standing query result.

#### Cypher Query as an Output

The Cypher query output is defined in terms of a regular Cypher query that is run for each result
produced by the standing query. The results from the standing query are available under a Cypher
query parameter—see the @ref:[3D tutorial](3d_data_ingest_sq.md) for an end-to-end example of
this. When using a Cypher query as a standing query output, it is highly recommended that the output
query be tested independently in the Exploration UI.

If the standing query is already running, the SSE output mentioned in the next section is also a
great way to find IDs that match so as to try the output query interactively on those nodes.

## Inspecting Running Queries

Since standing queries use a subset of regular Cypher query syntax, the standing query itself can
be run as a regular query either to see what data already in the graph would have been matched by the query or to
understand why a particular node in the graph is not a match. When doing so, you may want to constrain the starting points of
the query. See @ref:[querying potentially infinite data](querying_potentially_infinite_data.md).

When a standing query is running, it is possible to "wiretap" results and inspect them live using the SSE endpoint
`/api/v1/query/standing/{standingQueryName}/results`. That endpoint will surface new matches as
they are being produced. The Chrome web browser, for example, will continue to append new results to the bottom
of the page as they become available. `curl` will print out new results as they arrive.

```bash
$ curl http://localhost:8080/api/v1/query/standing/kidWithParents/results
data:

data:

data:{"data":{"id":"2756309260014435"},"meta":{"isInitialResult":true,"isPositiveMatch":true,"resultId":"8f408026-8fb3-3955-c81a-7259175f41b8"}}
event:result
id:8f408026-8fb3-3955-c81a-7259175f41b8

data:{"data":{"id":"7945274922095468"},"meta":{"isInitialResult":true,"isPositiveMatch":true,"resultId":"6a83dda3-08a1-e085-ee7d-14138398f336"}}
event:result
id:6a83dda3-08a1-e085-ee7d-14138398f336

data:{"data":{"id":"6994090876991233"},"meta":{"isInitialResult":true,"isPositiveMatch":true,"resultId":"59b215b4-4084-b5bb-379d-9654bb2a7c83"}}
event:result
id:59b215b4-4084-b5bb-379d-9654bb2a7c83

data:
```

Using the output above, it is possible to query the matching nodes directly with a Cypher query.
For instance, we can go look at the `name` fields of some of the matches the SSE output above tells
us we found:

```cypher
// Query for fields on nodes with IDs from the SSE endpoint above
UNWIND [2756309260014435, 7945274922095468, 6994090876991233] AS kidId
MATCH (kid) where id(kid) = kidId
RETURN n.name
```

Querying for a matched node is especially useful if there is a Cypher query registered as one of the
outputs of the standing query and if that second query modifies the data—for instance, adding an
edge connected to the node.
