package nl.timvandijkhuizen.commerce.webserver.errors;

import io.netty.handler.codec.http.HttpResponseStatus;

public class NotFoundHttpException extends HttpException {

    private static final long serialVersionUID = 1279860689857964915L;

    public NotFoundHttpException(String message) {
        super(HttpResponseStatus.NOT_FOUND, message);
    }
    
}
