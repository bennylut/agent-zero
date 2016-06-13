/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.exp;

/**
 *
 * @author Administrator
 */
public class UncheckedIOException extends RuntimeException{

    public UncheckedIOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UncheckedIOException(Throwable cause) {
        super(cause);
    }

    public UncheckedIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public UncheckedIOException(String message) {
        super(message);
    }

    public UncheckedIOException() {
    }
    
}
