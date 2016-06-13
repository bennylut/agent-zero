/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.ui.swing.buttons;

import com.sun.java.swing.plaf.motif.MotifToggleButtonUI;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 *
 * @author bennyl
 */
public class FlatToggleButton extends JToggleButton{

    Color unselectesColor = new Color(245,245,245);
    
    public FlatToggleButton() {
        setUI(new MotifToggleButtonUI());
        setBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(3, 3, 3, 3)));
    }

    public Color getUnselectedColor() {
        return unselectesColor;
    }

    public void setUnselectedColor(Color unselectesColor) {
        this.unselectesColor = unselectesColor;
    }
    
    
}
