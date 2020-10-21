package nl.timvandijkhuizen.commerce.webserver.errors;

import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpException extends Exception {

    private static final long serialVersionUID = -1720174799719168555L;

    private HttpResponseStatus status;

    public HttpException(HttpResponseStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpResponseStatus getStatus() {
        return status;
    }

}
