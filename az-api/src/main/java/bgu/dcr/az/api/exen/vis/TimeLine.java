/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
