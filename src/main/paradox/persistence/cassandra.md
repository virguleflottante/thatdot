# Cassandra

@link:[Cassandra](https://cassandra.apache.org/) can be thought of as a fancy distributed key-value
store (technically a @link:[wide-column store](https://en.wikipedia.org/wiki/Wide-column_store)).
The Cassandra persistence backend offers good performance and durability.

To configure the use of the Cassandra persistor, at a minimum, you'll need to specify
`connect.store.type=cassandra`. Here are the other options:

```
connect.store {
  type = cassandra
  endpoints = ["localhost:9042"]
  local-datacenter = datacenter1
  keyspace = thatdot
  should-save-snapshots = true
  should-create-tables = true    # Whether or not to create tables if they don't already exist
  should-create-keyspace = true  # Whether or not to create the specified keyspace if it doesn't already exist
}
```

The settings other than `type` are optional, and given here with their default values.
This will be sufficient to connect to a Cassandra on localhost with the default configuration.

If the specified keyspace doesn't already exist on the Cassandra cluster, it will be @link:[created](https://docs.datastax.com/en/cql-oss/3.x/cql/cql_reference/cqlCreateKeyspace.html) on startup, by executing:

```cql
CREATE KEYSPACE IF NOT EXISTS thatdot WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}
```

The same goes for the tables in that keyspace that thatDot uses: `journals`, `snapshots`, and `standing_queries` -
they will be created on startup if they don't already exist.

## Server deployment
### Self-hosted Cassandra

JDK8 is required. OpenJDK is recommended.

Instructions to install Cassandra from `.deb` or `.rpm` are on <https://cassandra.apache.org/download/>.

Data managed by Cassandra is stored in `/var/lib/cassandra`.
On EC2 instances with attached ephemeral storage (such as an @link:[I3 type](https://aws.amazon.com/ec2/instance-types/i3/), or an M4 with a "d" in the name), you may want to mount the the local SSD drive there.

##### Example setup:
```
sudo mkfs.xfs -L cassandra /dev/nvme1n1  # Or whatever the name of the attached emphemeral storage device is
sudo mkdir /var/lib/cassandra
```
And then add the following line to `/etc/fstab`
```
LABEL=cassandra		/var/lib/cassandra	xfs	noatime,noquota	0 2
```

@link:[Here](https://docs.datastax.com/en/dse-planning/doc/planning/planningEC2.html#GuidelinesforEC2productionclusters) is the recommendation from DataStax for disk IO on EC2.

Configuration is in `/etc/cassandra` (or `/etc/cassandra/conf` on Amazon Linux 2).
The main config file is [`cassandra.yml`](https://cassandra.apache.org/doc/latest/configuration/cassandra_config_file.html).

The default config binds only to the localhost interface.
To change this, change the value of the [seeds](https://docs.datastax.com/en/dse/6.0/dse-admin/datastax_enterprise/production/seedNodesForSingleDC.html) setting from `"127.0.0.1"` to the IP of the server.
Then change `rpc_address` and `listen_address` to that as well, or just comment them out to have it auto-populate them based on the current hostname.
Alternatively you can set `rpc_interface` and `listen_interface`.
Technically you only need to change `rpc_address` for a single-node cluster, as `listen_address` is for other other Cassandra nodes to talk to this one, as opposed to `rpc_address`, which is for Cassandra clients to issue queries on, but you may as well go ahead and change them both now.

To set up a cluster, see <https://docs.datastax.com/en/cassandra-oss/3.x/cassandra/initialize/initSingleDS.html>.

You can edit Cassandra's JVM options, e.g. max and min heap size, in `jvm.options`.

##### Performance tweaks

* [concurrent_compactors](https://cassandra.apache.org/doc/latest/configuration/cassandra_config_file.html#concurrent-compactors)
On SSD, set this to the number of CPU cores.
* [compaction-throughput-mb-per-sec](https://cassandra.apache.org/doc/latest/configuration/cassandra_config_file.html#compaction-throughput-mb-per-sec)
Suggested setting is 16-32 times the rate at which you're inserting data.



### Hosted Cassandra

Hosted options are also available from DataStax, Instaclustr, AWS, etc.

