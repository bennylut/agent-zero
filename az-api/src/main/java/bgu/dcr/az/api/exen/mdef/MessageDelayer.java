/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.mdef;

import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.exen.Execution;

/**
 *
 * @author bennyl
 */
public interface MessageDelayer {
    /**
     * initialize the delayer - this method will get called on each new execution that is started
     * the delayer is reused between executions and this method will get called to notify it to reinitialize itself
     * on the new execution
     * @param ex 
     */
    void initialize(Execution ex);
    
    /**
     * @return the initial time
     * the mailer will take this time in order to initialize itself
     */
    long getInitialTime();
    
    /**
     * extract the time from the given message 
     * most of the time all the "timing" information will be stored inside the message metadata.
     * @param m
     * @return 
     */
    long extractTime(Message m);
    
    /**
     * add delay to the given message
     * the message is sent between agent 'from' to agent 'to'
     * @param m
     * @param from
     * @param to 
     */
    void addDelay(Message m, int from, int to);
}
