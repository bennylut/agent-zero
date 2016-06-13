/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exp;

/**
 *
 * @author bennyl
 */
public class ConnectionFaildException extends Exception{

    public ConnectionFaildException(Throwable cause) {
        super(cause);
    }

    public ConnectionFaildException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionFaildException(String message) {
        super(message);
    }

    public ConnectionFaildException() {
    }
    
}
