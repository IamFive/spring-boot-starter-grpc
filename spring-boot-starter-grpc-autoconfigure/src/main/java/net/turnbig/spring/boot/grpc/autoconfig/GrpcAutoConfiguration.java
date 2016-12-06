package net.turnbig.spring.boot.grpc.autoconfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.turnbig.spring.boot.grpc.GRpcRunnerFactoryBean;
import net.turnbig.spring.boot.grpc.GRpcService;

/**
 * 
 *
 * @author Woo Cubic
 * @date   Dec 6, 2016 10:17:39 AM
 */
@Configuration
@ConditionalOnBean(annotation = GRpcService.class)
@EnableConfigurationProperties(GRpcServerProperties.class)
public class GrpcAutoConfiguration {

	private GRpcServerProperties grpcServerProperties;

	/**
	 * @param grpcServerProperties
	 */
	public GrpcAutoConfiguration(GRpcServerProperties grpcServerProperties) {
		super();
		this.grpcServerProperties = grpcServerProperties;
	}

	@Bean
	public GRpcRunnerFactoryBean grpcRunner() {
		GRpcRunnerFactoryBean bean = new GRpcRunnerFactoryBean();
		bean.setPort(this.grpcServerProperties.getPort());
		return bean;
	}

}
