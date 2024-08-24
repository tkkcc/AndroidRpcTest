# rpc performance test

multi client request-response between rust and kotlin on android

## conclusion

1. kotlin serialization json encode bytearry is very slow
2. android bitmap png compression too slow
3. tarpc with bincode serialization: 50ms to transfer image 4x1080x1920
1. with png encode decode is slower: 110ms
4. tcp or uds have no obvious differences

## reproduce

start server: run app in android studio

start client
```sh
cd client
./0.sh run
```

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
