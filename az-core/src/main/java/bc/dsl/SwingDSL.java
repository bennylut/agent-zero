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
package bc.dsl;

import javax.swing.JPanel;
import java.util.List;
import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;
import static bc.dsl.JavaDSL.*;

/**
 *
 * @author bennyl
 */
public class SwingDSL {

    private static boolean uiConfigured = false;
    private static HashMap<String, ImageIcon> icons = new HashMap<String, ImageIcon>();

    public static ImageIcon resIcon(Class loc, String path) {
        if (path == null) return null;
        String rpath = loc.getResource(path).toString();

        if (!icons.containsKey(rpath)) {
            System.out.println("loading " + path + " relative to " + loc.getCanonicalName());
            icons.put(rpath, new ImageIcon(loc.getResource(path)));
        }

        return icons.get(rpath);
    }

    public static ImageIcon resIcon(String absolutePath) {
        if (absolutePath == null) return null;
        if (absolutePath.isEmpty()) {
            return null;
        }
        if (!absolutePath.endsWith(".png")) {
            absolutePath = "resources/img/" + absolutePath + ".png";
        }
        try {
            return new ImageIcon(ClassLoader.getSystemClassLoader().getResource(absolutePath));
        } catch (Exception ex) {
            Logger.getLogger(SwingDSL.class.getName()).log(Level.SEVERE, "cannot find icon: " + absolutePath, ex);
            return null;
        }

    }

    public static ImageIcon loadIcon(String path) {
        if (icons == null) {
            icons = new HashMap<String, ImageIcon>();
        }
        if (!icons.containsKey(path)) {
            icons.put(path, new ImageIcon(path));
        }
        return icons.get(path);
    }

    public static File choosefile() {
        JFileChooser jfc = new JFileChooser();
        jfc.showDialog(null, "Choose");
        return jfc.getSelectedFile();
    }

    public static File choosefile(String from) {
        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(new File(from));
        jfc.showDialog(null, "Choose");
        return jfc.getSelectedFile();
    }

    public static File chooseSaveLocation() {
        JFileChooser jfc = new JFileChooser();
        jfc.showSaveDialog(null);
        return jfc.getSelectedFile();
    }

    public static File chooseSaveLocation(String from) {
        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory(new File(from));
        jfc.showSaveDialog(null);
        return jfc.getSelectedFile();
    }

    public static JComboBox fill(JComboBox combo, Class cenum) {
        EnumSet es = EnumSet.allOf(cenum);
        DefaultComboBoxModel dbom = new DefaultComboBoxModel();
        for (Object e : es) {
            dbom.addElement(e);
        }
        combo.setModel(dbom);
        return combo;
    }

    public static JComboBox fill(JComboBox cn, Object[] aopt) {
        DefaultComboBoxModel dbom = new DefaultComboBoxModel();
        for (Object e : aopt) {
            dbom.addElement(e);
        }
        cn.setModel(dbom);
        return cn;
    }

    public static JComboBox fill(JComboBox cn, List aopt) {
        DefaultComboBoxModel dbom = new DefaultComboBoxModel();
        for (Object e : aopt) {
            dbom.addElement(e);
        }
        cn.setModel(dbom);
        return cn;
    }

    public static void showInFrame(JPanel pan){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(pan);
        frame.pack();
        frame.setVisible(true);
    }
    
    public static JComboBox append(JComboBox combo, Object obj) {
        ((DefaultComboBoxModel) combo.getModel()).addElement(obj);
        return combo;
    }

    public static JComboBox select(JComboBox combo, Object obj) {
        combo.getModel().setSelectedItem(obj);
        return combo;
    }

    public static <T extends JComponent> T styleFont(T component, int style) {
        Font font = component.getFont();
        component.setFont(font.deriveFont(style));
        //component.invalidate();
        return component;
    }

    public static TitledBorder styleFont(TitledBorder titleb, int style) {
        Font font = titleb.getTitleFont();
        titleb.setTitleFont(font.deriveFont(style));
        return titleb;
    }

    /**
     * open via desktop
     */
    public static void dopen(File f) {
        try {
            Desktop.getDesktop().open(f);
        } catch (IOException ex) {
            log(ex);
        }
    }

    public static boolean ynqbox(String title, String message) {
        int result = JOptionPane.showConfirmDialog(null, message, title,JOptionPane.YES_NO_OPTION ,JOptionPane.INFORMATION_MESSAGE, resIcon("ynqbox-logo"));
        return result == JOptionPane.YES_OPTION;
    }
    
    public static void msgbox(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE, resIcon("msgbox-logo"));
    }

    public static void errbox(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE, resIcon("errbox-logo"));
    }

    public static void configureUI() {
        if (uiConfigured) return;
        uiConfigured = true;
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SwingDSL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(SwingDSL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SwingDSL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(SwingDSL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
