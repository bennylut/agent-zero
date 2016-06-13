/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.ui.swing.models;

/**
 *
 * @author bennyl
 */
public abstract class DataInserter<T> {
    
    String[] supported;

    public DataInserter(String... supported) {
        this.supported = supported;
    }

    public abstract void setData(String dataName, T from, Object newValue);

    public String[] getSupportedDataNames() {
        return supported;
    }

}
