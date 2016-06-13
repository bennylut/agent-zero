/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.exp;

/**
 *
 * @author Administrator
 */
public class EmptyContainerException extends RuntimeException {

    public EmptyContainerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public EmptyContainerException(Throwable cause) {
        super(cause);
    }

    public EmptyContainerException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyContainerException(String message) {
        super(message);
    }

    public EmptyContainerException() {
    }
    
}
