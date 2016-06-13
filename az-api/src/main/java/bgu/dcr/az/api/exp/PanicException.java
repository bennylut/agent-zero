/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exp;

/**
 *
 * @author bennyl
 */
public class PanicException extends RuntimeException{

    /**
     * 
     * @param cause
     */
    public PanicException(Throwable cause) {
        super(cause);
    }

    /**
     * 
     * @param message
     * @param cause
     */
    public PanicException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 
     * @param message
     */
    public PanicException(String message) {
        super(message);
    }

    /**
     * 
     */
    public PanicException() {
    }
    
}
