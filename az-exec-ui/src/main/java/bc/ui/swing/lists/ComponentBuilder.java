/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.ui.swing.lists;

import bc.ui.swing.visuals.Visual;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 *
 * @author bennyl
 */
public interface ComponentBuilder {
    JComponent build(Visual forItem);
    
    public static class DefaultComponentBuilder implements ComponentBuilder{

        @Override
        public JComponent build(Visual forItem) {
            return new JLabel(forItem.getText(), forItem.getIcon(), JLabel.TRAILING);
        }
        
    }
}
