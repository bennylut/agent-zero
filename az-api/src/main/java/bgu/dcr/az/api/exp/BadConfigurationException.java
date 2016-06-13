/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exp;

/**
 *
 * @author bennyl
 */
public class BadConfigurationException extends RuntimeException{

    public BadConfigurationException(Throwable cause) {
        super(cause);
    }

    public BadConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadConfigurationException(String message) {
        super(message);
    }

    public BadConfigurationException() {
    }
    
}
