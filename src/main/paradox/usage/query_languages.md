# Supported Query Languages

Quine supports several different ways of querying the graph data. This includes some existing
established query languages like Cypher and Gremlin, as well as a novel mechanism for querying
future data referred to here as Standing Queries.

## Cypher

@link:[Cypher](https://s3.amazonaws.com/artifacts.opencypher.org/openCypher9.pdf) is the most widely
use query language for interacting with data in a property graph format. It is structurally and
syntactically similar to SQL, with the main difference being the `MATCH` clause. The idea of `MATCH`
is to focus on declaratively describing the graph shape (pattern) that you want and then to let the
query compiler pick a good execution plan. What would normally require multiple `JOIN`'s in a
relational model often just reduces to one `MATCH` with a pattern that has multiple edges:

```cypher
MATCH (n: Person)-[:has_parent]->(p: Person)-[:lives_in]->(c: City)
RETURN p.name AS name, c.name AS parentsCity
```

Compare the above Cypher to the equivalent SQL below:

```sql
SELECT n.name AS name, c.name AS parentsCity
FROM persons AS n
JOIN persons AS p ON n.parent = p.id
JOIN cities AS c ON p.city = c.id
```

Cypher queries can be issued to Quine in several ways:

  * entered in the query bar of the @ref:[Exploration UI](exploration_ui.md)

  * sent directly through the @ref:[REST API](rest_api.md)

## Gremlin

@link:[Gremlin](https://tinkerpop.apache.org/gremlin.html) is another graph query language, but one
that is less declarative and more focused on letting users specify exactly the traversal they want.
The main strength that Gremlin has is that one of its focuses is traversals: instructions for how to
walk the graph structure given some starting points.

In the @ref:[Exploration UI](exploration_ui.md), quick queries are defined using Gremlin. This is
effective because those queries often really are just about taking a couple mechanical steps away
from the node on which was right clicked.

@@@ warning

Support for Gremlin in Quine is much less complete than for Cypher. Even the parts of Gremlin that
are implemented are not guaranteed to be compliant. Part of the difficulty here is that some parts
of Gremlin were designed to be executed form inside a host language, most frequently Groovy, and
don't extend naturally to remote execution (see for instance
@link:[this section](https://tinkerpop.apache.org/docs/3.4.7/reference/#_the_lambda_solution_3) of
the Gremlin manual for some complexities around anonymous functions).

@@@
