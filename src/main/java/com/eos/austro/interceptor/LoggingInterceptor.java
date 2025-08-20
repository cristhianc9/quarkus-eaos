package com.eos.austro.interceptor;

import io.quarkus.logging.Log;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Interceptor para loggear entrada y salida de requests.
 */
@Provider
@Priority(Priorities.USER)
public class LoggingInterceptor implements ContainerRequestFilter, ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Log.infof("[IN] %s %s - Headers: %s", requestContext.getMethod(), requestContext.getUriInfo().getPath(),
                requestContext.getHeaders());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        Log.infof("[OUT] %s %s - Status: %d", requestContext.getMethod(), requestContext.getUriInfo().getPath(),
                responseContext.getStatus());
    }
}
