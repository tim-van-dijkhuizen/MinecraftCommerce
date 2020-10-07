package nl.timvandijkhuizen.commerce.webserver.errors;

import io.netty.handler.codec.http.HttpResponseStatus;

public class ServerErrorHttpException extends HttpException {

    private static final long serialVersionUID = 2648578803126803364L;

    public ServerErrorHttpException(String message) {
        super(HttpResponseStatus.INTERNAL_SERVER_ERROR, message);
    }
    
}
