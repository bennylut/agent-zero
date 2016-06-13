/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
