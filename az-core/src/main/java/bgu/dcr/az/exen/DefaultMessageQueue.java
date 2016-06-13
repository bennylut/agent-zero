/* 
 * The MIT License
 *
 * Copyright 2016 Benny Lutati.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
