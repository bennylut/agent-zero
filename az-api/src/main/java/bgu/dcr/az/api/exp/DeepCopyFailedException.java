/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exp;

/**
 *
 * @author bennyl
 */
public class DeepCopyFailedException extends RuntimeException{

    public DeepCopyFailedException(Throwable cause) {
        super(cause);
    }

    public DeepCopyFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeepCopyFailedException(String message) {
        super(message);
    }

    public DeepCopyFailedException() {
    }
    
}
