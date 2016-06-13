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
package bc.ui.swing.models;

import java.io.File;
import java.util.List;
import java.util.LinkedList;
import javax.swing.AbstractListModel;

import static bc.dsl.JavaDSL.*;

/**
 *
 * @author BLutati
 */
public class GenericListModel<T> extends AbstractListModel {

    LinkedList<T> model = new LinkedList<T>();

    @Override
    public int getSize() {
        return model.size();
    }

    public void fireItemChanged(T item){
        int idx = model.indexOf(item);
        fireContentsChanged(this, idx, idx);
    }

    public List<T> getInnerList() {
        return model;
    }

    @Override
    public T getElementAt(int index) {
        return model.get(index);
    }

    public void addLast(T item) {
        model.addLast(item);
        final int size = model.size();
        fireIntervalAdded(this, size - 1, size);
    }

    public void add(int idx, T item) {
        if (idx > model.size()) {
            model.addLast(item);
        } else {
            model.add(idx, item);
        }
        fireIntervalAdded(this, idx, min(idx + 1, getSize()));
    }

    public boolean remove(T item) {
        int idx = model.indexOf(item);
        if (idx >= 0) {
            remove(idx);
            return true;
        } else {
            return false;
        }
    }

    public T remove(int idx) {
        T ret = model.remove(idx);
        fireIntervalRemoved(this, idx, min(getSize(), idx + 1));

        return ret;
    }

    public void clear() {
        int size = getSize();
        model.clear();
        fireIntervalRemoved(this, 0, size);
    }

    public void setInnerList(LinkedList<T> model) {
        LinkedList<T> last = model;
        this.model = model;
        fireIntervalRemoved(this, 0, last.size());
        fireIntervalAdded(this, 0, model.size());
    }
    
    public void fillWith(List<T> includedTests) {
        model.addAll(includedTests);
        fireIntervalAdded(this, model.size() - includedTests.size(), model.size());
    }
}
