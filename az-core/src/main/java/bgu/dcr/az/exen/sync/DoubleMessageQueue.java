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
