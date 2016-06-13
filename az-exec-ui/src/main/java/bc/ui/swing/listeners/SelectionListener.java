/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.ui.swing.listeners;

import java.util.List;

/**
 *
 * @author bennyl
 */
public interface SelectionListener<SOURCE, SELECTION> {
    public void onSelectionChanged(SOURCE source, List<SELECTION> selectedItems);
}
