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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author bennyl
 */
public class GenericTableModel<T> extends AbstractTableModel {

    List<T> model;
    DataExtractor<T> extractor;
    DataInserter<T> inserter;
    Map<Integer, Class> columnClasses;

    public GenericTableModel(DataExtractor<T> extractor) {
        model = new LinkedList<T>();
        this.extractor = extractor;
        columnClasses = new HashMap<Integer, Class>();
    }

    public void setInserter(DataInserter<T> inserter) {
        this.inserter = inserter;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Class cls = columnClasses.get(columnIndex);
        if (cls == null && !model.isEmpty()) {
            columnClasses.put(columnIndex, extractor.getData(columnIndex, extractor.getSupportedDataNames()[columnIndex], model.get(0)).getClass());
        }

        return columnClasses.containsKey(columnIndex) ? columnClasses.get(columnIndex) : Object.class;
    }

    public void changeInnerList(List<T> innerList) {
        model = innerList;
        fireTableStructureChanged();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (inserter == null) {
            return false;
        }
        final String name = extractor.getSupportedDataNames()[columnIndex];
        for (String s : inserter.getSupportedDataNames()) {
            if (s.equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        inserter.setData(extractor.getSupportedDataNames()[columnIndex], model.get(rowIndex), aValue);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    @Override
    public int getRowCount() {
        return model.size();
    }

    @Override
    public int getColumnCount() {
        return extractor.getSupportedDataNames().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return extractor.getData(columnIndex, extractor.getSupportedDataNames()[columnIndex], model.get(rowIndex));
    }

    public void setInnerList(List<T> list) {
        model = list;
        fireTableRowsInserted(model.size() - list.size(), model.size());
    }

    public void fillWith(List<T> ts) {
        model.addAll(ts);
        fireTableRowsInserted(model.size() - ts.size(), model.size());
    }

    public void add(T t) {
        model.add(t);
        fireTableRowsInserted(model.size() - 1, model.size());
    }

    @Override
    public String getColumnName(int column) {
        return extractor.getSupportedDataNames()[column];
    }

    public List<T> getInnerData() {
        return model;
    }
}
