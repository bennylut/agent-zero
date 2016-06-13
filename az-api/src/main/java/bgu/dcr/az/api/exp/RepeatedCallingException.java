/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exp;

/**
 *
 * @author bennyl
 */
public class RepeatedCallingException extends RuntimeException{

    public RepeatedCallingException(Throwable cause) {
        super(cause);
    }

    public RepeatedCallingException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepeatedCallingException(String message) {
        super(message);
    }

    public RepeatedCallingException() {
    }
    
}
