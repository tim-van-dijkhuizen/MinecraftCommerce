package nl.timvandijkhuizen.commerce.webserver.errors;

import io.netty.handler.codec.http.HttpResponseStatus;

public class BadRequestHttpException extends HttpException {

    private static final long serialVersionUID = 4763118735855244577L;

    public BadRequestHttpException(String message) {
        super(HttpResponseStatus.BAD_REQUEST, message);
    }

}