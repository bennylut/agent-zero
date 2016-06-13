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
