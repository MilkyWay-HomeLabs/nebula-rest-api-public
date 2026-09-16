package org.derleta.nebula.shared.domain.exception;

/**
 * Custom exception that represents errors occurring during HTTP request execution
 * or response handling. This exception is primarily used to wrap I/O exceptions
 * and provide additional context related to the HTTP request.
 * <p>
 * This exception is a runtime exception, meaning it does not need to be explicitly
 * declared in the `throws` clause of methods and can be used for unchecked error
 * propagation.
 */
public class HttpRequestException extends RuntimeException {
    public HttpRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
