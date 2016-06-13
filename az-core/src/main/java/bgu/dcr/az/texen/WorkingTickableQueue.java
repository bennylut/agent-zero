/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.texen;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author User
 */
public class WorkingTickableQueue {

    private Tickable[] tickables;
    private AtomicInteger[] waitingTickCount;
    private ConcurrentLinkedQueue<Tickable> waitingTickables;
    private List<IdleDetectionCallback> idleCallbacks;
    private Semaphore queueCounter;
    private AtomicInteger waitingThreads;
    private int numberOfThreads;
    private ReentrantReadWriteLock idleNotificationLock = new ReentrantReadWriteLock();
    private AtomicBoolean terminated;
    private AtomicInteger numberOfActiveTickables;

    public void tick() throws InterruptedException {
        while (!terminated.get()) {
            Tickable next;
            idleNotificationLock.readLock().lock();
            try {
                next = waitingTickables.poll();
            } finally {
                idleNotificationLock.readLock().unlock();
            }

            if (next == null) {
                boolean queueWasEmpty = waitingTickables.isEmpty(); //the order of those lines are important!!!
                int old = waitingThreads.getAndIncrement();

                if (old == numberOfThreads - 1 && queueWasEmpty) {
                    //this means that the queue was empty before i tested the number of waiting threads,
                    //when i tested - all the other threads are waiting which means that no threads is able to produce a tick request
                    //more over the order of the above lines indicate that i was the only thread that recognize the idle - so i should 
                    //be the one that notifies it.

                    idleNotificationLock.writeLock().lock();
                    try {
                        for (IdleDetectionCallback idleCallback : idleCallbacks) {
                            idleCallback.onIdleDetection();
                        }
                    } finally {
                        idleNotificationLock.writeLock().unlock();
                    }

                }

                if (terminated.get()) {
                    return;
                }
                
                queueCounter.acquire(); // blocking until someone need ticking..
                queueCounter.release();
                waitingThreads.incrementAndGet();
            } else { //got someone to animate - i am so happy!
                if (!next.isTerminated()) {
                    queueCounter.acquire();
                    next.tick();

                    if (next.isTerminated()) {
                        int active = numberOfActiveTickables.decrementAndGet();
                        if (active == 0) {
                            terminate();
                        }
                    }

                }

                int tickNeeded = waitingTickCount[next.getId()].decrementAndGet();
                if (tickNeeded > 0) {
                    waitingTickables.add(next);
                    queueCounter.release();
                }

                return;
            }
        }
    }

    public void requestTick(int tickable) {
        int old = waitingTickCount[tickable].getAndIncrement();
        if (old == 0) {
            waitingTickables.add(tickables[tickable]);
            queueCounter.release();
        }
    }

    public void initialize(Tickable[] tickables, int numberOfThreads, List<IdleDetectionCallback> callbacks) {
        this.tickables = tickables;
        this.waitingTickCount = new AtomicInteger[tickables.length];
        this.waitingTickables = new ConcurrentLinkedQueue<>();
        this.waitingThreads = new AtomicInteger(0);
        this.terminated = new AtomicBoolean(false);
        this.queueCounter = new Semaphore(0);
        this.idleCallbacks = callbacks;
        this.numberOfThreads = numberOfThreads;
        this.numberOfActiveTickables = new AtomicInteger(tickables.length);

        queueCounter.release(tickables.length);
        for (int i = 0; i < tickables.length; i++) {
            waitingTickCount[i] = new AtomicInteger(1); // kickstart
            waitingTickables.add(tickables[i]);
        }
    }

    public void terminate() {
        terminated.set(true);
        queueCounter.release(numberOfThreads * 2); //*2 just to be on the safe side 
    }

    public boolean isTerminated() {
        return terminated.get();
    }
}
