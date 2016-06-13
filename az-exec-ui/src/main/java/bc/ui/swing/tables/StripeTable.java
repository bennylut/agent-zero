/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.ui.swing.tables;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

/**
 * A better-looking table than JTable. In particular, on Mac OS this looks
 * more like a Cocoa table than the default Aqua LAF manages.
 *
 * @author Elliott Hughes
 */
public class StripeTable extends JTable {

    private Color evenRowColor = new Color(245, 245, 245);
    private Color oddRowColor = new Color(225,225,225);
    
    public StripeTable() {
    }

    public Color getEvenRowColor() {
        return evenRowColor;
    }

    public Color getOddRowColor() {
        return oddRowColor;
    }

    public void setEvenRowColor(Color evenRowColor) {
        this.evenRowColor = evenRowColor;
    }

    public void setOddRowColor(Color oddRowColor) {
        this.oddRowColor = oddRowColor;
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
    }
    
    /**
     * Paints empty rows too, after letting the UI delegate do
     * its painting.
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paintEmptyRows(g);
    }

    /**
     * Paints the backgrounds of the implied empty rows when the
     * table model is insufficient to fill all the visible area
     * available to us. We don't involve cell renderers, because
     * we have no data.
     */
    protected void paintEmptyRows(Graphics g) {
        final int rowCount = getRowCount();
        final Rectangle clip = g.getClipBounds();
        if (rowCount * rowHeight < clip.height) {
            for (int i = rowCount; i <= clip.height / rowHeight; ++i) {
                g.setColor(colorForRow(i));
                g.fillRect(clip.x, i * rowHeight, clip.width, rowHeight);
            }
        }
    }

    /**
     * Changes the behavior of a table in a JScrollPane to be more like
     * the behavior of JList, which expands to fill the available space.
     * JTable normally restricts its size to just what's needed by its
     * model.
     */
    @Override
    public boolean getScrollableTracksViewportHeight() {
        if (getParent() instanceof JViewport) {
            JViewport parent = (JViewport) getParent();
            return (parent.getHeight() > getPreferredSize().height);
        }
        return false;
    }

    /**
     * Returns the appropriate background color for the given row.
     */
    protected Color colorForRow(int row) {
        return (row % 2 == 0) ? evenRowColor : oddRowColor;
    }

    /**
     * Shades alternate rows in different colors.
     */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (isCellSelected(row, column) == false) {
            c.setBackground(colorForRow(row));
            c.setForeground(getForeground());
        } else {
            c.setBackground(UIManager.getColor("Table.selectionBackground"));
            c.setForeground(UIManager.getColor("Table.selectionForeground"));
        }
        return c;
    }
}