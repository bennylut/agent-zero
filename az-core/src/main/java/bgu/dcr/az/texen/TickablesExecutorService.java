/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
