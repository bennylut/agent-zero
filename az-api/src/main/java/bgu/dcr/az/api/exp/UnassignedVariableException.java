/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exp;

/**
 *
 * @author bennyl
 */
public class UnassignedVariableException extends RuntimeException{

    public UnassignedVariableException(Throwable cause) {
        super(cause);
    }

    public UnassignedVariableException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnassignedVariableException(String message) {
        super(message);
    }

    public UnassignedVariableException() {
    }
    
}
