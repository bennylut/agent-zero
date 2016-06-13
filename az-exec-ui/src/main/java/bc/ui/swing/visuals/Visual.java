/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.ui.swing.visuals;

import java.util.LinkedList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;

/**
 *
 * @author bennyl
 */
public class Visual {

    Object item;
    String text;
    String description;
    Icon icon;

    public Visual(Object item, String text, String description, Icon icon) {
        this.item = item;
        this.text = text;
        this.description = description;
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public Object getItem() {
        return item;
    }

    public void setItem(Object item) {
        this.item = item;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Visual) {
            Visual other = (Visual) obj;
            return other.item.equals(this.item);
        } else if (obj.getClass().equals(this.item.getClass())) {
            return obj.equals(item);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + (this.item != null ? this.item.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return text;
    }

    public static void populate(JComboBox cbox, List<Visual> visuals){
        DefaultComboBoxModel boxModel = new DefaultComboBoxModel(visuals.toArray());
        cbox.setModel(boxModel);
    }
    
    public static Object getSelected(JComboBox cbox){
        return ((Visual) cbox.getSelectedItem()).getItem();
    }
    
    public static LinkedList<Visual> adapt(List items) {
        LinkedList<Visual> ret = new LinkedList<Visual>();
        for (Object i : items) {
            ret.add(new Visual(i, i.toString(), "", null));
        }

        return ret;
    }

    public static LinkedList<Visual> adapt(List items, VisualGen func) {
        LinkedList<Visual> ret = new LinkedList<Visual>();
        for (Object i : items) {
            ret.add(func.gen(i));
        }

        return ret;
    }

    public static LinkedList<Visual> adapt(Object[] items, VisualGen func) {
        LinkedList<Visual> ret = new LinkedList<Visual>();
        for (Object i : items) {
            ret.add(func.gen(i));
        }

        return ret;
    }

    public static interface VisualGen {

        Visual gen(Object it);
    }
}
