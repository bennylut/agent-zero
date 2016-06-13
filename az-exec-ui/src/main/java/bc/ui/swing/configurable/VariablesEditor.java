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
