/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.ui.swing.configurable;

import bc.ui.swing.lists.ComponentList;
import bgu.dcr.az.api.exen.escan.VariableMetadata;
import bgu.dcr.az.dev.ui.MessageDialog;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JComponent;

/**
 *
 * @author bennyl
 */
public class VariablesEditor extends ComponentList{

    @Override
    public JComponent createComponentFor(Object item) {
        final SingleVariableEditor sv = new SingleVariableEditor();
        sv.setModel((VariableMetadata)item);
        return sv;
    }
    
    public void setModel(VariableMetadata[] vars){
        clear();
        for (VariableMetadata v : vars){
            add(v);
        }
        
        revalidate();
        repaint();
    }
    
    public Map<String, Object> getConfiguration(){
        Map<String, Object> ret = new HashMap<String, Object>();
        for (Entry<Object, JComponent> v : this.items.entrySet()){
            final Object value = ((SingleVariableEditor)v.getValue()).getValue();
            VariableMetadata var = (VariableMetadata) v.getKey();
            //TODO - IF VALUE IS NULL => provide feedback to the user.
            if (value == null){
                MessageDialog.showFail("cannot convert input to value", "the given input for the variable " + var.getName() + " cannot be converted to a legal value\n"
                        + "there can be number of reasons for that\n"
                        + "- the input format is not recognizeable by the variable class valueOf function\n"
                        + "- there is no static valueOf function defined in the given variable class\n\n"
                        + "if the problem appear to be the later suggestion you should fix the code of the statistic collector\n"
                        + "or contact the statistic collector provider.");
                return null;
            }
            ret.put(var.getName(), value);
        }
        
        return ret;
    }
}
