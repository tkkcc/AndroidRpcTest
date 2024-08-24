# rpc performance test

multi client request-response between rust and kotlin on android

## conclusion

1. kotlin serialization json encode bytearry is very slow
2. android bitmap png compression too slow


## exp

communication strategy:

1. kotlin http server, ktor, json serialization
1. kotlin grpc server, OkHttp, protobuf serialization
1. axum + jni, http/tcp, json serialization
1. axum + jni, uds, json serialization
1. axum + jni, tarpc over http/tcp, json serialization
1. axum + jni, tarpc over uds, json serialization
1. axum + jni, tarpc over uds, bincode serialization

not test:

1. tonic: grpc, protobuf serialization
1. capnp-rpc: client call is builder style
1. jsonrpsee: assume it's same as tarpc with json serialization
1. serde-byte: need try on our image buffer


time: duration of request response decode at client

task: get screenshot, get screennode, click position
