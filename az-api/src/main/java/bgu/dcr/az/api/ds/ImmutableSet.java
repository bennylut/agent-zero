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
package bgu.dcr.az.api.ds;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * extends hashset - all the mutational operations are throwing unsupported
 * operation exception
 *
 * @author bennyl
 * @param <T>
 */
public class ImmutableSet<T> implements Set<T> {

    private Set<T> delegate;

    public ImmutableSet(Collection<T> data) {
        this(data, false);
    }

    public ImmutableSet(Collection<T> data, boolean wrap) {
        if (wrap) {
            delegate = (Set) data;
        } else {
            delegate = new HashSet<>();
            for (T o : data) {
                delegate.add(o);
            }
        }
    }

    @Override
    public boolean add(T arg0) {
        throw new UnsupportedOperationException("cannot modify to imuuteable set");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("cannot modify to imuuteable set");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("cannot modify to imuuteable set");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("cannot modify to imuuteable set");
    }

    @Override
    public boolean addAll(java.util.Collection<? extends T> c) {
        throw new UnsupportedOperationException("cannot modify to imuuteable set");
    }

    @Override
    public Iterator<T> iterator() {
        final Iterator<T> i = delegate.iterator();
        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public T next() {
                return i.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("cannot modify to imuuteable set");
            }
        };
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("cannot modify to imuuteable set");
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

}
