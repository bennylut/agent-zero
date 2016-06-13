/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen;


/**
 *
 * @author bennyl
 */
public interface Process extends Runnable {

    /**
     * @return true if this process is finished 
     * TODO: transform to status enum.
     */
    boolean isFinished();

    /**
     * each execution should supply a way to stop
     * stopping every child process or thread that "belongs" to this process.
     */
    void stop();
        
}
