/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.utils;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public abstract class PokedWorker implements Runnable {

    long nextTime = -1;
    int minimumWorkTime;
    Semaphore pokeLock;

    public PokedWorker(int minumumWorkTime) {
        this.minimumWorkTime = minumumWorkTime;
        this.pokeLock = new Semaphore(1);
        try {
            this.pokeLock.acquire();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                pokeLock.acquire();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            long next = getNextTime();

            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            long time = -1984;
            try {
                time = next - System.currentTimeMillis();

                if (time > 0) {
                    Thread.sleep(time);
                }
                setNextTime(-1);
                work();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private synchronized long getNextTime() {
        return nextTime;
    }

    private synchronized void setNextTime(long nextTime) {
        this.nextTime = nextTime;
    }

    public synchronized void poke() {
        if (getNextTime() < 0) {
            setNextTime(System.currentTimeMillis() + minimumWorkTime);
            pokeLock.release();
        }
    }

    public abstract void work();
}
