package net.turnbig.spring.boot.grpc.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * GRpc Service Runner auto-configuration properties
 * 
 * @author Woo Cubic
 * @date   Dec 6, 2016 9:58:27 AM
 */
@ConfigurationProperties("grpc")
public class GRpcServerProperties {

	/**
	 * gRPC server port
	 */
	private int port = 6565;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
