package com.heimdall.feign.core;

/**
 * @author crh
 * @date 2020-09-12
 */
public class RequestException extends RuntimeException {

    public RequestException() {
    }

    public RequestException(String message) {
        super(message);
    }


    public RequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestException(Throwable cause) {
        super(cause);
    }
}
