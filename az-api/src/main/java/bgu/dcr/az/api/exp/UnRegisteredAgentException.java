/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exp;

/**
 *
 * @author bennyl
 */
public class UnRegisteredAgentException extends RuntimeException {

    /**
     * 
     * @param cause
     */
    public UnRegisteredAgentException(Throwable cause) {
        super(cause);
    }

    /**
     * 
     * @param message
     * @param cause
     */
    public UnRegisteredAgentException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 
     * @param message
     */
    public UnRegisteredAgentException(String message) {
        super(message);
    }

    /**
     * 
     */
    public UnRegisteredAgentException() {
    }
    
}
