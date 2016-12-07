package net.turnbig.spring.boot.grpc.example;

import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.test.context.junit4.SpringRunner;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.turnbig.spring.boot.grpc.autoconfig.GRpcServerProperties;

/**
 * Created by alexf on 28-Jan-16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Sample.class })
public class SampleTest {

	private ManagedChannel channel;

	@Rule
	public OutputCapture outputCapture = new OutputCapture();

	// @Autowired
	// @Qualifier("globalInterceptor")
	// private ServerInterceptor globalInterceptor;

	@Autowired
	GRpcServerProperties properties;

	@Before
	public void setup() {
		channel = ManagedChannelBuilder.forAddress("0.0.0.0", properties.getPort()).usePlaintext(true).build();
	}

	@After
	public void tearDown() {
		channel.shutdown();
	}

	@Test
	public void greetTest() throws ExecutionException, InterruptedException {
		String name = "John";
		final GreeterGrpc.GreeterFutureStub greeterFutureStub = GreeterGrpc.newFutureStub(channel);
		final GreeterOuterClass.HelloRequest helloRequest = GreeterOuterClass.HelloRequest.newBuilder().setName(name)
				.build();
		final String reply = greeterFutureStub.sayHello(helloRequest).get().getMessage();
		Assert.assertNotNull(reply);
		Assert.assertTrue(String.format("Replay should contain name '%s'", name), reply.contains(name));
	}

}
