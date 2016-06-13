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
