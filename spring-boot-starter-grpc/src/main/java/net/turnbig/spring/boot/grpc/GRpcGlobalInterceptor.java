package net.turnbig.spring.boot.grpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * Marks a ServerInterceptor Component as global interceptor 
 * 	which will be applied to all GRpc-Services by default
 * 
 * @see    {io.grpc.ServerInterceptor}
 * @author Woo Cubic
 * @date   Dec 5, 2016 6:02:04 PM
 */
@Component
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface GRpcGlobalInterceptor {
}
