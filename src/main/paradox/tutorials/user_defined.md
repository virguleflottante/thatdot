# User-Defined Functions and Procedures

User-defined functions and user-defined procedures (UDF/UDP) are a way to extend the functionality
of _thatDot Connect_ with custom logic particularly relevant to specific users and use cases. UDFs
and UDPs can enable you to reuse code that has already been written, by calling it directly within
_thatDot Connect_ and not requiring an outside service. UDFs can also be used to simplify the
queries written in the system or to otherwise streamline the processing of data for specific
applications.

User-defined functions and procedures can be written in any JVM-compatible language. This
documentation focuses on UDFs/UDPs written in Scala or Java, loaded into the system via the
`/api/v1/query/cypher/user-defined` REST endpoint, and then available for use in Cypher queries.

  * __User-defined functions__ (UDFs) are functions that take any number of arguments and are
    expected to produce *a single* value as their output. They are pure functions that cause no
    changes to the data stored in the graph. Results are produced synchronously.

  * __User-defined procedures__ (UDPs) are similar to functions in that they take arguments and
    produce results, but UDPs produce a back-pressured stream of rows, each of which may contain
    *multiple* values. Since the results are in a stream, it is possible to do *asynchronous*
    computation (producing an output row only on completion) or to return many results.

## Steps to Create UDFs/UDPs

Creating user-defined functions and procedures requires:

* writing code for your custom procedure in a JVM compatible language
* compiling that code (with the thatDot Connect JAR available as a dependency)
* packaging the output into a JAR
* copy the that JAR file to all of the cluster members
* loading the code in the JAR by calling the REST API endpoint

## Example: Defining a `math.factorial` UDF

Here is what the code for defining a `math.factorial` UDF looks like in Scala
and in Java. We assume that the snippets are compiled with the full
_thatDot Connect_ JAR available (since otherwise they won't compile due to
missing types). Some important requirements:

  * The UDF is defined as a `class` which has a public no-argument constructor
  * The UDF class is annotated with `com.thatdot.quine.runtime.cypher.CypherUDF`
  * The UDF class extends `com.thatdot.quine.runtime.cypher.UserDefinedFunction`
    or its subclass `com.thatdot.quine.runtime.cypher.JavaUserDefinedFunction`

Scala
:   @@snip [example-factorial.scala](/src/main/resources/com/thatdot/quine/runtime/cypher/Factorial.scala)

Java
:   @@snip [example-factorial.java](/src/main/resources/com/thatdot/quine/runtime/cypher/JavaFactorial.java)

In order to extend `UserDefinedFunction`, it is necessary to implement several
members:

  - the `name` specifies how the UDF will be called in Cypher

  - the `call` method defines what it means to call the UDF, taking in as an
    argument the internal representation of a Cypher value and producing
    another Cypher value as output

  - the `signatures` field specifies the function signature(s) of the UDF (used for
    ruling out obviously ill-typed calls at query compilation time and producing helpful errors)


Assuming the above has been compiled & packaged into `cypher-factorial.jar` and
the JAR is copied to the servers beside the _thatDot Connect_ JAR, the following REST API
call is enough to load the UDF into the system.

```bash
curl -X POST "http://localhost:8080/api/v1/query/cypher/user-defined" \
     -H  "accept: */*" -H  "Content-Type: application/json" \
     -d '["cypher-factorial.jar"]'
```

The `math.factorial` function can now be used from Cypher:

```bash
curl -X POST "http://localhost:8080/api/v1/query/cypher" \
     -H  "accept: application/json" -H  "Content-Type: text/plain" \
     -d "RETURN math.factorial(5)"
```

Executing this command uses our new user-defined function and returns:
```json
{"columns":["math.factorial(5)"],"results":[[120]]}
```
