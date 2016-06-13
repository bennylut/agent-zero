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
package bgu.dcr.az.texen;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author User
 */
public class TickablesExecutorService {

    private static final AtomicInteger EXECUTORS_ID_GEN = new AtomicInteger(0);

    private WorkingTickableQueue queue;
    private final ExecutorService pool;
    private TerminationStatus lastResult;
    private final int numberOfThreads;
    private final List<IdleDetectionCallback> idleCallbacks = new LinkedList<>();
    private AtomicInteger activeTickables;
    private Tickable[] tickables;
    private Semaphore joinLock;

    public TickablesExecutorService(int numberOfThreads) {
        pool = Executors.newFixedThreadPool(numberOfThreads);
        this.numberOfThreads = numberOfThreads;
    }

    public void addIdleDetectionCallback(IdleDetectionCallback callback) {
        idleCallbacks.add(callback);
    }

    public void removeIdleDetectionCallback(IdleDetectionCallback callback) {
        idleCallbacks.remove(callback);
    }

    public void shutdown() {
        pool.shutdownNow();
    }

    public TerminationStatus execute(Tickable[] tickables) {
        this.lastResult = TerminationStatus.GRACEFULLY;
        this.tickables = tickables;
        activeTickables = new AtomicInteger(tickables.length);
        queue = new WorkingTickableQueue();
        queue.initialize(tickables, numberOfThreads, idleCallbacks);
        joinLock = new Semaphore(-numberOfThreads + 1);

        for (int i = 0; i < numberOfThreads; i++) {
            pool.execute(new Executor());
        }

        try {
            joinLock.acquire();
        } catch (InterruptedException ex) {
            interupted(ex);
            Thread.currentThread().interrupt();
        }

        return lastResult;
    }

    private void terminated() {
        lastResult = TerminationStatus.GRACEFULLY;

        queue.terminate();
    }

    public void requestTick(int tickableId) {
        queue.requestTick(tickableId);
    }

    public void interupt() {
        lastResult = TerminationStatus.INTERUPTED;
        queue.terminate();
    }

    private void interupted(InterruptedException ex) {
        ex.printStackTrace();
        interupt();
    }

    private void crushed(Exception ex) {
        ex.printStackTrace();
        lastResult = TerminationStatus.CRUSHED;

        queue.terminate();
    }

    private void tickableTerminated(Tickable t) {
        if (activeTickables.decrementAndGet() == 0) {
            terminated();
        }
    }

    private class Executor implements Runnable {

        int id;

        @Override
        public void run() {
            Thread.currentThread().setName("Agent Executor :: " + (id = EXECUTORS_ID_GEN.incrementAndGet()));
            try {
                while (!queue.isTerminated() && !Thread.currentThread().isInterrupted()) {
                    queue.tick();
                }
            } catch (InterruptedException ex) {
                System.out.println("Executor Interupted.");
                interupted(ex);
            } catch (Exception ex) {
                System.out.println("Crushed.");
                crushed(ex);
            } finally {
                joinLock.release();
            }
        }

    }

    public enum TerminationStatus {

        INTERUPTED,
        CRUSHED,
        IDLE_DETECTED,
        GRACEFULLY
    }
}
