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
package bgu.dcr.az.api.tools;

import bgu.dcr.az.api.exen.Mailer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

/**
 *
 * @author bennyl
 */
public class IdleDetector {

    private final int maxWaiting;
    private int version = 0;
    private volatile int waiting;
    private Semaphore s = new Semaphore(1);
    private Mailer m;
    private LinkedList<Listener> listeners = new LinkedList<Listener>();
    private String groupKey;
    private HashMap<String, IdleDetector> children = new HashMap<String, IdleDetector>();

    public IdleDetector(int waiting, Mailer m, String groupKey) {
        this.maxWaiting = waiting;
        this.waiting = waiting;
        this.m = m;
        this.groupKey = groupKey;
    }

    /**
     * get idle detector for the given group key with the exact same configuration as this one
     * @param groupKey
     * @return 
     */
    public synchronized IdleDetector getSubDetector(String groupKey){
        children.put(this.groupKey, this);
        IdleDetector child = children.get(groupKey);
        if (child == null){
            child = new IdleDetector(maxWaiting, m, groupKey);
            children.put(groupKey, child);
            child.listeners = this.listeners;
        }
        
        return child;
    }
    
    public void notifyAgentWorking() {
        try {
            s.acquire();
            version++;
            waiting++;
            s.release();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            Thread.currentThread().interrupt();
        }

    }

    public void notifyAgentIdle() {
        int oversion;
        try {
            s.acquire();
            version++;
            oversion = version;
            waiting--;
            s.release();


            if (waiting == 0) {
                if (m.isAllMailBoxesAreEmpty(groupKey)) {
                    s.acquire();
                    if (oversion == version) {
                        fireIdleDetected();
                    }
                    s.release();
                }
            }

        } catch (InterruptedException ex) {
            ex.printStackTrace();
            Thread.currentThread().interrupt();
        }

    }

    public synchronized void addListener(Listener l) {
        listeners.add(l);
    }

    private void fireIdleDetected() {
        boolean resolved = false;
        for (Listener l : listeners) {
            l.onIdleDetection(); //before try handling the idle - tell all the listeners that you discover idle
        }

        for (Listener l : listeners) {
            resolved |= l.tryResolveIdle(); //the idle can be caused by different mechanisms - esspecially message delays - in this case the listeners should attempt to recover from the idle
            if (resolved) {
                break;
            }
        }

        if (!resolved) { //if the idle was not caused by any framework mechanism then it was caused by the logic of the algorithm - in that case the algorithm itself should try to resolve it, we will notify the listeners about it
            for (Listener l : listeners) {
                l.idleCannotBeResolved();
            }
        } else {
            for (Listener l : listeners) {
                l.idleResolved(); //if the idle was indeed created by the framework mechanisms - notify the listeners that it was resolved and they should resume working normally
            }
        }
    }

    public static interface Listener {

        /**
         * this callback will get called once an idle detection was found 
         * this method should never try to resolve the idle - it is only meant to 
         * make updates for shared stated before the resolving phase
         */
        void onIdleDetection();

        /**
         * this callback will get called when idle was detected - this callback should 
         * try to recover from the idle state
         * if the recovery succedded then the callback should return true and false otherwise
         * once one of the listeners return true there will be no more invokations of this callback
         */
        boolean tryResolveIdle();

        /**
         * this callback will get called after all the listeners was notified about idle detection 
         * and after all of them returned false
         */
        void idleCannotBeResolved();

        /**
         * this callback will get called if the idle was resolved
         * like the "onIdleDetection" function it is meant to update shared state
         */
        void idleResolved();
    }
}
