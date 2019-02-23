package org.openhab.binding.plugwiseha.internal.api;

/**
 * Exception for errors from the API Client.
 *
 * @author Kaj Visser - Initial contribution
 *
 */
public class PlugwiseHAApiClientException extends Exception {
    public PlugwiseHAApiClientException() {
    }

    public PlugwiseHAApiClientException(String message) {
        super(message);
    }

    public PlugwiseHAApiClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
