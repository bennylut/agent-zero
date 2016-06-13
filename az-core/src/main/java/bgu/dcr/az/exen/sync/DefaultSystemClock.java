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

import bgu.dcr.az.api.Hooks.TickHook;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.SystemClock;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

/**
 *
 * @author bennyl
 */
public class DefaultSystemClock implements SystemClock {

    private CyclicBarrier barrier;
    private long time;
    private volatile long closed = -2;
    private volatile boolean ticked = false;
    private volatile long notified = -1;
    private List<TickHook> tickHooks = new LinkedList<TickHook>();
    private Semaphore tickHookLock = new Semaphore(1);

    public DefaultSystemClock() {
        this.time = 0;
    }

    public void setExcution(Execution exc) {
        System.out.println("DefaultSystemClock: Barrier Set to Be: " + exc.getNumberOfAgentRunners());
        this.barrier = new CyclicBarrier(exc.getNumberOfAgentRunners());
    }

    @Override
    public void tick() throws InterruptedException {
        try {
            ticked = true;
            long nextTime = time + 1;

            barrier.await();

            if (closed == nextTime - 1) {
                return;
            }
            /**
             * visibility problem should not occure here as every thread must pass through the following line
             * thus its local cpu cache will get updated.
             */
            time = nextTime;

            tickHookLock.acquire();
            try {
                if (notified < time) {
                    notified = time;
                    for (TickHook t : tickHooks) {
                        t.hook(this);
                    }
//                    System.out.println("Tick " + time);
                }
            } finally {
                tickHookLock.release();
            }

            ticked = false;

        } catch (BrokenBarrierException ex) {
            if (closed == time) {
                return;
            }
            System.err.println("got BrokenBarrierException, translating it to InterruptedException (DefaultSystemClock)");
            throw new InterruptedException(ex.getMessage());
        }
    }

    @Override
    public long time() {
        return time;
    }

    @Override
    public void close() {
        if (closed == -2) {
            closed = time + 1;
        }
    }

    public boolean isClosed() {
        return closed == time;
    }

    @Override
    public boolean isTicked() {
        return ticked;
    }

    @Override
    public void hookIn(TickHook hook) {
        tickHooks.add(hook);
    }
}
