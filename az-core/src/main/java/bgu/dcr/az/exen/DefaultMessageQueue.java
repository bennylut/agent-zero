/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package bgu.dcr.az.exen;

import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.exen.MessageQueue;
import bgu.dcr.az.api.exp.InternalErrorException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 *
 * @author bennyl
 */
public class DefaultMessageQueue implements MessageQueue {

    public static final Message SYSTEM_RELEASE_MESSAGE = new Message("__SYSTEM_RELEASE_MESSAGE__", -1, new Object[0]);
    LinkedBlockingQueue<Message> q = new LinkedBlockingQueue<Message>();
    Semaphore blocker = new Semaphore(0);

    @Override
    public Message take() throws InterruptedException {
        Message ret = q.take();
        blocker.acquire();
        if (ret == SYSTEM_RELEASE_MESSAGE) return null;
        return ret;
    }

    @Override
    public void add(Message e) {
        if (! q.offer(e)) throw new InternalErrorException("cannot insert message " + e + " to agent queue");
        blocker.release();
    }

    @Override
    public int availableMessages() {
        return q.size();
    }

    @Override
    public void waitForNewMessages() throws InterruptedException {
        //System.out.println("Waiting for messages: q is " + q.size());
        blocker.acquire();
        blocker.release();
        //System.out.println("Done Waiting for messages");
    }
    
    @Override
    public boolean isEmpty(){
        return this.q.isEmpty();
    }

    @Override
    public void onAgentFinish() {
        //dont care...
    }
    
    @Override
    public boolean isNotEmpty(){
        return !this.q.isEmpty();
    }

    @Override
    public void releaseBlockedAgent() {
        add(SYSTEM_RELEASE_MESSAGE);
    }
}
