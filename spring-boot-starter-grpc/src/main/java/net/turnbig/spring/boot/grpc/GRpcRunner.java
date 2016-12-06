package net.turnbig.spring.boot.grpc;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.type.StandardMethodMetadata;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptor;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;

/**
 * Bootstrap all GRpc-Service annotated with @GRpcService
 *
 * @author Woo Cubic
 * @date   Dec 5, 2016 5:59:26 PM
 */
public class GRpcRunner implements CommandLineRunner, DisposableBean {

	private static final Logger log = LoggerFactory.getLogger(GRpcRunner.class);

	@Autowired
	private ConfigurableApplicationContext applicationContext;

	private Server server;

	// GRpc runner properties
	private Integer port; // the port GRpc will service on

	@Override
	public void run(String... args) throws Exception {
		log.info("Starting GRpc Server now ...");

		Collection<ServerInterceptor> globalInterceptors = getBeanNamesByTypeWithAnnotation(GRpcGlobalInterceptor.class,
				ServerInterceptor.class)
						.map(name -> applicationContext.getBeanFactory().getBean(name, ServerInterceptor.class))
						.collect(Collectors.toList());

		final ServerBuilder<?> serverBuilder = ServerBuilder.forPort(this.port);
		// find and register all GRpcService-enabled beans
		getBeanNamesByTypeWithAnnotation(GRpcService.class, BindableService.class).forEach(name -> {
			BindableService srv = applicationContext.getBeanFactory().getBean(name, BindableService.class);
			ServerServiceDefinition serviceDefinition = srv.bindService();
			GRpcService gRpcServiceAnn = applicationContext.findAnnotationOnBean(name, GRpcService.class);
			serviceDefinition = bindInterceptors(serviceDefinition, gRpcServiceAnn, globalInterceptors);
			serverBuilder.addService(serviceDefinition);
			log.info("'{}' service has been registered.", srv.getClass().getName());

		});

		server = serverBuilder.build().start();
		log.info("GRpc Server started, service on port {}.", this.port);
		startDaemonAwaitThread();
	}

	private <T> Stream<String> getBeanNamesByTypeWithAnnotation(Class<? extends Annotation> annotationType,
			Class<T> beanType) throws Exception {

		String[] beanNamesForType = applicationContext.getBeanNamesForType(beanType);
		return Stream.of(beanNamesForType).filter(name -> {
			BeanDefinition beanDefinition = applicationContext.getBeanFactory().getBeanDefinition(name);
			if (beanDefinition.getSource() instanceof StandardMethodMetadata) {
				StandardMethodMetadata metadata = (StandardMethodMetadata) beanDefinition.getSource();
				return metadata.isAnnotated(annotationType.getName());
			}
			return null != applicationContext.getBeanFactory().findAnnotationOnBean(name, annotationType);
		});
	}

	private ServerServiceDefinition bindInterceptors(ServerServiceDefinition serviceDefinition, GRpcService gRpcService,
			Collection<ServerInterceptor> globalInterceptors) {

		Stream<? extends ServerInterceptor> privateInterceptors = Stream.of(gRpcService.interceptors())
				.map(interceptorClass -> {
					try {
						return 0 < applicationContext.getBeanNamesForType(interceptorClass).length
								? applicationContext.getBean(interceptorClass) : interceptorClass.newInstance();
					} catch (Exception e) {
						throw new BeanCreationException("Failed to create interceptor instance.", e);
					}
				});

		List<ServerInterceptor> interceptors = Stream
				.concat(gRpcService.applyGlobalInterceptors() ? globalInterceptors.stream() : Stream.empty(),
						privateInterceptors)
				.distinct().collect(Collectors.toList());
		return ServerInterceptors.intercept(serviceDefinition, interceptors);
	}

	private void startDaemonAwaitThread() {
		Thread awaitThread = new Thread() {
			@Override
			public void run() {
				try {
					GRpcRunner.this.server.awaitTermination();
				} catch (InterruptedException e) {
					log.error("gRPC server stopped.", e);
				}
			}

		};
		awaitThread.setDaemon(false);
		awaitThread.start();
	}

	@Override
	public void destroy() throws Exception {
		log.info("Shutting down gRPC server ...");
		Optional.ofNullable(server).ifPresent(Server::shutdown);
		log.info("gRPC server stopped.");
	}

	/**
	 * @return the port
	 */
	public Integer getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	/**
	 * @return the applicationContext
	 */
	public ConfigurableApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * @param applicationContext the applicationContext to set
	 */
	public void setApplicationContext(ConfigurableApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
