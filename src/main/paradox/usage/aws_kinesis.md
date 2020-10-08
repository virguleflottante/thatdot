# AWS Kinesis Support

## Reading Records from Kinesis
Connect has full support for reading records from Kinesis Data Streams. The means by which Connect interprets a record into some graph-structured data is highly configurable via the @ref:[REST API](rest_api.md).

### Example
In this example, we will register a multiple-shard Kinesis stream of JSON objects (one JSON object per Kinesis record) as a data source, creating a single node in the graph for each object.

#### Preparation
For the purposes of this tutorial, you will need @link:[a Kinesis data stream](https://console.aws.amazon.com/kinesis/home#/streams/create) and credentials (an access key ID and secret access key) for an @link:[IAM User](https://console.aws.amazon.com/iam/home?#/users$new?step=details) with the following privileges:

 - kinesis:RegisterStreamConsumer
 - kinesis:DeregisterStreamConsumer
 - kinesis:SubscribeToShard
 - kinesis:DescribeStreamSummary
 - kinesis:DescribeStreamConsumer
 - kinesis:GetShardIterator
 - kinesis:GetRecords
 - kinesis:DescribeStream
 - kinesis:ListTagsForStream

 For our example, we'll assume we have such a user with access to the `json-logs` stream with access key ID `AKIAMYACCESSKEY` and secret `AWSScRtACCessKeyAWS/ScRtACCessKey`. These will be used to register the data source with Connect.

#### Registering Kinesis as a data source
To register Kinesis as a data source to thatDot Connect, we need to describe our stream via the ingest @ref:[REST API](rest_api.md).

For example, we'll use the aforementioned Kinesis stream hosted in the region `us-west-2`, named `json-logs` and we'll give the Connect ingest stream the name `kinesis-logs`. Thus, we make our API request a POST to `/api/v1/ingest/kinesis-logs` with the following payload:

```json
{
  "format": {
    "query": "CREATE ($props)",
    "parameter": "props",
    "type": "CypherJson"
  },
  "streamName": "json-logs",
  "parallelism": 2,
  "shardIds": [],
  "type": "KinesisIngest",
  "credentials": {
    "region": "us-west-2",
    "accessKeyId": "AKIAMYACCESSKEY",
    "secretAccessKey": "AWSScRtACCessKeyAWS/ScRtACCessKey"
  },
  "iteratorType": "TRIM_HORIZON"
}
```
We pass in an empty list of shard IDs to specify that Connect should read from all shards in the stream. If we wanted to only read from particular shards, we would instead list out the shard IDs from which Connect should read.

Because the Kinesis stream is filled with JSON records, we choose the `CypherJson` import format, which reads each record as a JSON object before passing it as a `Map` to a cypher query.

The cypher query can access this object using the name specified by `parameter`; in our case, the name is `props`. Thus, our configured query `CREATE ($props)` will create a node for each JSON record with the same property structure as the JSON record.

In this example, we use a Kinesis stream populated with JSON objects as records, though Connect offers other options for how to interpret records from a stream. These options are configurable via the same endpoint by using different `format`s in the above JSON payload.

Finally, we choose to read all records from the Kinesis stream, including records already present in the stream configuring the Connect data source. To get this behavior, we use a `TRIM_HORIZON` Kinesis iterator type. If we wished to only read records written to the Kinesis stream _after_ setting up the Connect data source, we would have used the `LATEST` iterator type.

<!-- ## Writing Records to Kinesis -->
<!-- Coming soon! -->