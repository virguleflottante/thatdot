# Deploying a _thatDot Connect_ cluster

## Prerequisites

Each host in a cluster must be able to freely communicate over the network with each other host (that is, there must be no firewall rules preventing any two clustered hosts from communicating on any network port).

Each host in a cluster must have `java` installed. Java 8u191 or higher is recommended.

## Deployment Architecture

A clustered configuration of _thatDot Connect_ is composed of any number of machines, each a member of the cluster and running one instance of the _thatDot Connect_ application. When a machine is started with the provided `.jar` file, _thatDot Connect_ will start up on that machine and use its configuration information to find a seed node and join the cluster. The cluster handles leader election and management of the overall cluster status.

At this time, _thatDot Connect_ cluster configuration is defined statically. The cluster becomes available and ready to process requests when the configuration that defines the cluster is satisfied by each of the expected cluster members joining the whole cluster. If any member of the cluster drops out, the entire cluster enters a “degraded” state to preserve data correctness and pauses all interaction until the cluster member is restored or the cluster restarted.

## Configuring _thatDot Connect_

_thatDot Connect_ is highly configurable, but this section focuses on the minimum configuration needed. Configuration options follow typical JVM conventions and can be provided via command-line arguments, or in bulk with a configuration file. The syntax of the configuration uses the [HOCON](https://github.com/lightbend/config) standard, which is a superset of JSON.

Providing configuration for the cluster requires a common cluster definition given to each cluster member (usually as a single `.conf` file), as well as a small number of unique configuration values passed to each cluster member (typical via command-line arguments). The configuration specific to each cluster member consists of:

```hocon
connect.cluster {
  hostname = "10.0.0.15"  // the cluster member's hostname or IP address
  port = 1600
}
```

While an example of the common configuration which must be shared and agreed upon by each cluster member can be:

```hocon
connect.cluster {
  topology = [
    { hostname : "10.0.0.15", port : 1600, first-shard : 0,  last-shard : 7  },
    { hostname : "10.0.0.20", port : 1600, first-shard : 8,  last-shard : 15 },
    { hostname : "10.0.0.25", port : 1600, first-shard : 16, last-shard : 23 },
    { hostname : "10.0.0.30", port : 1600, first-shard : 24, last-shard : 31 },
    { hostname : "10.0.0.35", port : 1600, first-shard : 32, last-shard : 39 },
    { hostname : "10.0.0.40", port : 1600, first-shard : 40, last-shard : 47 },
    { hostname : "10.0.0.45", port : 1600, first-shard : 48, last-shard : 55 },
  ]
}
```

In this example, we are configuring the host `10.0.0.15`. This host is part of a seven-node cluster across the hosts `10.0.0.15`, `10.0.0.20`, `10.0.0.25`, `10.0.0.30`, `10.0.0.35`, `10.0.0.40`, and `10.0.0.45`.

Note that values in `connect.cluster.hostname` and `connect.cluster.port` are specific to the machine they are passed to, but the combination should appear as part of the `connect.cluster.topology` configuration. To make this configuration suitable for another node in the cluster, we simply replace the `connect.cluster.hostname` and `connect.cluster.port` values with the IP/hostname and port specific to the machine to which this configuration is passed.

## Running _thatDot Connect_

_thatDot Connect_ can be run as an executable JAR as follows:
```bash
java -Dconnect.cluster.hostname="<IP/HOSTNAME>" -Dconnect.cluster.port=<PORT_NUMBER> -Dconfig.file=<CONFIG_FILE> -jar <JAR_FILE>
```

For example, if the JAR file for _thatDot Connect_ is named `thatdot-connect.jar` and a config file named `cluster.conf` in the same directory:

```bash
java -Dconnect.cluster.hostname="10.0.0.15" -Dconnect.cluster.port=1600 -Dconfig.file=cluster.conf -jar thatdot-connect.jar
```

Upon startup, each instance of _thatDot Connect_ will attempt to connect to the seed node defined as the first element in `connect.cluster.topology`. Once the application has been started on all hosts, the hosts will connect to each other according to the provided configuration file, and the cluster leader will promote the entire cluster to operational status.

## Connecting to the cluster
Once the cluster is fully-formed, each host will expose a web interface at `http://<hostname>:8080`. A web browser directed to this web server root will show the exploration UI. Queries issued to the exploration UI hosted on a single machine will query the data managed by the entire cluster. Note that some queries issued to the exploration UI that scan all nodes are not fully supported.

Generated API documentation is available on each host at `http://<hostname>:8080/docs/`. API calls that set up ingest streams are performed only on the machine to which the API call is issued. If an API call to ingest from a Kinesis stream is issued, the machine receiving it will start ingesting from the prescribed Kinesis stream. All other machines in the cluster however will not begin ingest from that stream, and instead will need to have separate API calls issued to the respective URLs.

## Shutting down the cluster

Upon receiving a termination signal (from the operating system or through the API), the cluster will propagate the request for graceful shutdown to all members of the cluster. The shutdown process goes through several stages. During shutdown, new API calls are disallowed and existing ingest streams are stopped, but _thatDot Connect_ can still remain active for some time. This is necessary to allow all the internal standing queries and other activity occurring in the graph to complete their operation. When the internal graph has reached a stable state and all internal operations have concluded, each cluster member will withdraw from the cluster and finish shutdown of the local process.
