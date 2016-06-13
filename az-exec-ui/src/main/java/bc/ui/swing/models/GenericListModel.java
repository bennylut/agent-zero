/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
