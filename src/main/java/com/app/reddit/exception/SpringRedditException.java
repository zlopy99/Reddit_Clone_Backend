package com.app.reddit.exception;

public class SpringRedditException extends RuntimeException {
//    public SpringRedditException(String message, Exception e){
//        super(message, e);
//    }

    public SpringRedditException(String exMessage) {
        super(exMessage);


    }
}
