package net.turnbig.spring.boot.grpc.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.grpc.stub.StreamObserver;
import net.turnbig.spring.boot.grpc.GRpcService;

@SpringBootApplication
public class Sample {

	@GRpcService(interceptors = { LogInterceptor.class })
	public class GreeterService extends GreeterGrpc.GreeterImplBase {
		@Override
		public void sayHello(GreeterOuterClass.HelloRequest request,
				StreamObserver<GreeterOuterClass.HelloReply> responseObserver) {
			final GreeterOuterClass.HelloReply.Builder replyBuilder = GreeterOuterClass.HelloReply.newBuilder()
					.setMessage("Hello " + request.getName());
			responseObserver.onNext(replyBuilder.build());
			responseObserver.onCompleted();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(Sample.class, args);
	}

}
