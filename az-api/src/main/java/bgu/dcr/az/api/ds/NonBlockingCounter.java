/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.ds;

import java.util.concurrent.atomic.AtomicLong;

/**
 * a non blocking concurrent counter
 * @author bennyl
 */
public class NonBlockingCounter {

    private AtomicLong value = new AtomicLong(0);

    public long getValue() {
        return value.get();
    }

    public long add(long sum) {
        long v;
        do {
            v = value.get();
        } while (!value.compareAndSet(v, v + sum));
        return v + sum;
    }
    
    public long set(long val){
        long v;
        do {
            v = value.get();
        } while (!value.compareAndSet(v, val));
        return val;
    }
}
