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
package bgu.dcr.az.api.prob;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public class NeighboresSet implements Set<Integer>{
    boolean[] neighbores;
    int size;

    public NeighboresSet(int numvars) {
        neighbores = new boolean[numvars];
        size = 0;
    }
    
    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return neighbores[((Integer) o)];
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {

            int pos = 0;
            int read = 0;
            
            @Override
            public boolean hasNext() {
                return read < size && size != 0;
            }

            @Override
            public Integer next() {
                for (; pos<neighbores.length; pos++){
                    if (neighbores[pos]) {
                        read++;
                        return pos++;
                    }
                }
                
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported.");
            }
        };
    }

    @Override
    public Object[] toArray() {
        Integer[] ret = new Integer[size];
        int pos=0;
        
        for (int i=0; i<neighbores.length; i++){
            if (neighbores[i]) ret[pos++] = i;
        }
        
        return ret;
    }

    @Override
    public String toString() {
        return Arrays.toString(toArray());
    }

    
    @Override
    public <T> T[] toArray(T[] a) {
        return (T[]) toArray();
    }

    @Override
    public boolean add(Integer e) {
        boolean prev = neighbores[e];
        neighbores[e] = true;
        if (!prev) size++;
        return prev;
    }

    @Override
    public boolean remove(Object o) {
        final Integer i = (Integer)o;
        boolean prev = neighbores[i];
        neighbores[i] = false;
        
        if (prev) size--;
        return prev;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c){
            if (!contains(o)) return false;
        }
        
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends Integer> c) {
        boolean b = true;
        for (Integer i : c){
            b &= add(i);
        }
        
        updateSize();
        return b;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean[] tneighbores = neighbores;
        neighbores = new boolean[neighbores.length];
        addAll((Collection) c);
        for (int i=0; i<neighbores.length; i++){
            neighbores[i] &= tneighbores[i];
        }
        
        updateSize();
        return false; //TODO
    }
    
    private void updateSize(){
        size = 0;
        for (int i=0; i<neighbores.length; i++) {
            if (neighbores[i]) size++;
        }
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for (Iterator<?> it = c.iterator(); it.hasNext();) {
            Integer i = (Integer) it.next();
            neighbores[i] = false;
        }
        
        updateSize();
        return false;
    }

    @Override
    public void clear() {
        neighbores = new boolean[neighbores.length];
    }
    
}
