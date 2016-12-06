package net.turnbig.spring.boot.grpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Service;

import io.grpc.ServerInterceptor;

/**
 * Marks the annotated class to be registered as GRpc-service bean;
 * 
 * @author Woo Cubic
 * @date   Dec 5, 2016 5:44:28 PM
 */
@Service
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GRpcService {

	/**
	 * GRpc-service's customer interceptors
	 * @return
	 */
	Class<? extends ServerInterceptor>[] interceptors() default {};

	boolean applyGlobalInterceptors() default true;

}
