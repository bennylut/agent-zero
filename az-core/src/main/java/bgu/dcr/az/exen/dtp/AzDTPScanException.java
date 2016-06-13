/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.dtp;

/**
 *
 * @author Administrator
 */
public class AzDTPScanException extends Exception{

    public AzDTPScanException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AzDTPScanException(Throwable cause) {
        super(cause);
    }

    public AzDTPScanException(String message, Throwable cause) {
        super(message, cause);
    }

    public AzDTPScanException(String message) {
        super(message);
    }

    public AzDTPScanException() {
    }
    
}
