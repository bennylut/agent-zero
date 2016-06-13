/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package bgu.dcr.az.exen.sync;

import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.exen.MessageQueue;
import bgu.dcr.az.api.exen.SystemClock;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author bennyl
 */
public class DoubleMessageQueue implements MessageQueue {

//    private BoxedVolatileInteger qToUse;
    SystemClock clock;
    private ConcurrentLinkedQueue[] q = new ConcurrentLinkedQueue[2];

    public DoubleMessageQueue(SystemClock clock) {
        this.clock = clock;
        q[0] = new ConcurrentLinkedQueue();
        q[1] = new ConcurrentLinkedQueue();
    }

    private int qToUse() {
        return ((int) clock.time()) % 2;
    }

    @Override
    public Message take() throws InterruptedException {
        return (Message) q[qToUse()].remove();
    }

    @Override
    public void add(Message e) {
        q[1 - qToUse()].offer(e);
    }

    @Override
    public int availableMessages() {
        return q[qToUse()].size();
    }

    @Override
    public void waitForNewMessages() throws InterruptedException {
        throw new UnsupportedOperationException("Multi Queue not support waiting!");
    }

    @Override
    public boolean isEmpty() {
        return this.q[qToUse()].isEmpty();
    }

    @Override
    public boolean isNotEmpty() {
        return !this.q[qToUse()].isEmpty();
    }

    @Override
    public void releaseBlockedAgent() {
        throw new UnsupportedOperationException("Multi Queue not support waiting!");
    }

    @Override
    public void onAgentFinish() {
        //dont care
    }
    
    
}
