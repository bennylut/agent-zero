/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen;

/**
 *
 * @author bennyl
 */
public interface MessageQueue extends NonBlockingMessageQueue {

    void waitForNewMessages() throws InterruptedException;

    boolean isNotEmpty();

    /**
     * will cause the agent that is waiting for new messages to awake and take the message 'null'
     */
    void releaseBlockedAgent();
    
    /**
     * will get called when the agent finish
     */
    void onAgentFinish();
}
