/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.exp;

/**
 *
 * @author Administrator
 */
public class FullContainerException extends RuntimeException{

    public FullContainerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public FullContainerException(Throwable cause) {
        super(cause);
    }

    public FullContainerException(String message, Throwable cause) {
        super(message, cause);
    }

    public FullContainerException(String message) {
        super(message);
    }

    public FullContainerException() {
    }
    
}
