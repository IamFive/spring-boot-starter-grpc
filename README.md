# spring-boot-starter-grpc

Spring boot starter for [gRPC](http://www.grpc.io/).

> Basic codes are forked from [grpc-spring-boot-starter](https://github.com/LogNet/grpc-spring-boot-starter)

## Provided Features

### Integration gRPC with spring-boot-starter style
- [x] Scan for gRPC service implement annotated with @GRpcService
- [x] global and customer gRPC interceptors support
- [x] gRPC service daemon runner
- [ ]  TLS Configuration

### Load balance support
- [ ] etcd & consul service register plugin
- [ ] HAProxy sample


## Tutorial

- git clone the project
- run `mvn clean package install` to install jar to local
- then check the example project under `spring-boot-starter-grpc-example` folder

## Usage

### Implement and Run gRPC service

1. Define gRPC service with protobuf 3 format

```exmaple.proto
service Greeter {
    rpc SayHello ( HelloRequest) returns (  HelloReply) {}
}
```

2. Generate gRPC-java code from the service defination files(*.proto)

```mvn generate-sources```

3. implement the gRPC service and annatation the service with `@GRpcService`

```java
    @GRpcService
    public static class GreeterService extends  GreeterGrpc.GreeterImplBase{
        @Override
        public void sayHello(GreeterOuterClass.HelloRequest request, StreamObserver<GreeterOuterClass.HelloReply> responseObserver) {
            final GreeterOuterClass.HelloReply.Builder replyBuilder = GreeterOuterClass.HelloReply.newBuilder().setMessage("Hello " + request.getName());
            responseObserver.onNext(replyBuilder.build());
            responseObserver.onCompleted();
        }
    }
```

4. spring application context now will auto scan and run the gRPC service

### Interceptors support
The starter supports the registration of two kinds of interceptors: _Global_  and _Per Service_. +
In both cases the interceptor has to implement `io.grpc.ServerInterceptor` interface.

- Per service


```
@GRpcService(interceptors = { LogInterceptor.class })
public  class GreeterService extends  GreeterGrpc.GreeterImplBase{
    // ommited
}
```
`LogInterceptor` will be instantiated via spring factory if there is bean of type `LogInterceptor`, or via no-args constructor otherwise.

- Global

```
@GRpcGlobalInterceptor
public  class MyInterceptor implements ServerInterceptor{
    // ommited
}
```

The annotation on java config factory method is also supported :

```
 @Configuration
 public class MyConfig{
     @Bean
     @GRpcGlobalInterceptor
     public  ServerInterceptor globalInterceptor(){
         return new ServerInterceptor(){
             @Override
             public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
                // your logic here
                 return next.startCall(call, headers);
             }
         };
     }
 }
```

The particular service also has the opportunity to disable the global interceptors :

```
@GRpcService(applyGlobalInterceptors = false)
public  class GreeterService extends  GreeterGrpc.GreeterImplBase{
    // ommited
}
```


