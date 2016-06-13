/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.exp;

/**
 *
 * @author Administrator
 */
public class BadConnectionStatusException extends RuntimeException {

    public BadConnectionStatusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BadConnectionStatusException(Throwable cause) {
        super(cause);
    }

    public BadConnectionStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadConnectionStatusException(String message) {
        super(message);
    }

    public BadConnectionStatusException() {
    }
    
}
