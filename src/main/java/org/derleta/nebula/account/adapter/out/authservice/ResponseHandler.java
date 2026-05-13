package org.derleta.nebula.account.adapter.out.authservice;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.derleta.nebula.shared.adapter.in.rest.dto.Response;

import java.io.IOException;

/**
 * Functional interface for handling HTTP responses.
 * <p>
 * This interface defines a single method for processing an HTTP request and
 * returning an appropriate response. It is implemented by classes or lambda
 * expressions that specify how to handle HTTP requests and produce a response.
 *
 * @param <T> The type of the response, constrained to types that extend the {@link Response} interface.
 */
@FunctionalInterface
public interface ResponseHandler<T extends Response> {

    /**
     * Handles the HTTP response (success and errors) and maps it to a specific response type.
     *
     * @param requestBase the HTTP request to process
     * @return the processed response
     * @throws IOException if an I/O error occurs
     */
    T handleResponse(HttpUriRequestBase requestBase) throws IOException;

}
