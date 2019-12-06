package com.example.springboot.demo.Exception;

/**
 * @Description: AppWebException
 * version V1.0.0
 */
public class AppWebException extends Exception{
    private static final long serialVersionUID = -8198281171334131008L;

    private int errCode;

    public AppWebException() {}

    public AppWebException(int errCode, String message,
                           Exception exception) {
        super(message, exception);

        this.errCode = errCode;
    }

    public AppWebException(int errCode, String message) {
        this(errCode, message,null);
    }

    public int getErrCode() {
        return this.errCode;
    }
}
