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
package bgu.dcr.az.api.exen.vis;

import bgu.dcr.az.api.Agt0DSL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class TimeLine {

    private Map<Integer, Change> changes = new ConcurrentHashMap<Integer, Change>();
    private ArrayList compactChanges;
    private Object initialValue;
    private Semaphore mergeSemaphore = new Semaphore(1);
    private Semaphore addSemaphore = new Semaphore(1);

    public TimeLine(Object initialValue) {
        this.initialValue = initialValue;
    }

    public void add(int frame, Change change) {
        
        Change c = changes.get(frame);
        if (c == null) {
            try {
                addSemaphore.acquire();
                c = changes.get(frame);
                if (c == null) {
                    changes.put(frame, change);
                } else {
                    addSemaphore.release();
                    add(frame, change);
                    addSemaphore.acquire();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(TimeLine.class.getName()).log(Level.SEVERE, null, ex);
                Thread.currentThread().interrupt();
            } finally {
                addSemaphore.release();
            }
        } else {
            try {
                mergeSemaphore.acquire();
                c.mergeWith(change);
//                System.out.println("merge produced: " + c);     
            } catch (InterruptedException ex) {
                Logger.getLogger(TimeLine.class.getName()).log(Level.SEVERE, null, ex);
                Thread.currentThread().interrupt();
            } finally {
                mergeSemaphore.release();
            }
        }
    }

    public void streach(int numberOfFrames) {
        compactChanges = new ArrayList(numberOfFrames);
        LinkedList<Map.Entry<Integer, Change>> data = new LinkedList<Map.Entry<Integer, Change>>(changes.entrySet());
        Collections.sort(data, new Comparator<Map.Entry<Integer, Change>>() {
            @Override
            public int compare(Entry<Integer, Change> o1, Entry<Integer, Change> o2) {
                return o1.getKey() - o2.getKey();
            }
        });

        changes = null;
        //compactChanges.add(initialValue);
        int currentFrame = 0;
        while (!data.isEmpty()) {
            Entry<Integer, Change> cd = data.removeFirst();
//            System.out.println("" + cd);
            for (; currentFrame < cd.getKey(); currentFrame++) {
                compactChanges.add(initialValue);
            }

            initialValue = cd.getValue().reduce(initialValue, currentFrame);
            compactChanges.add(initialValue);
            currentFrame++;
        }

        for (; currentFrame < numberOfFrames; currentFrame++) {
            compactChanges.add(initialValue);
        }

    }

    public Object get(int frame) {
        return compactChanges.get(frame);
    }
}
