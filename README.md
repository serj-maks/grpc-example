# grpc-example
client &amp; server communication via gRPC
>see full guide: [link](https://openliberty.io/guides/grpc-intro.html)

## how to start
1. generate code from .proto file
`mvn -pl serverproto install`
2. run server
`mvn -pl server liberty:run`
3. run client
`mvn -pl client liberty:run`
4. point your browser to URL:

## four gRPC methods
gRPC lets you define four kinds of service method:

### unary RPC
basic unary request: the client sends a single request and receives a single response from the server

`http://localhost:9081/client/properties/os.name`

### server streaming RPC
the client sends a single request and the server returns a stream of messages

`not_implemented`

### client streaming RPC
the client sends a stream of messages and the server responds with a single message

`not_implemented`

### bidirectional RP
both client and server send a stream of messages. The client and server can read and write messages in any order

`not_implemented`

>[grpc.io/docs](https://grpc.io/docs/what-is-grpc/core-concepts/)
