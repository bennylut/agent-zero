package bc.ui.swing.trees;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * A JTree that draws a zebra-striped background.
 */
public class StripeTree
        extends javax.swing.JTree {

    public java.awt.Color rowColors[] = new java.awt.Color[]{new Color(245, 245, 245), new Color(225, 225, 225)};
    private boolean drawStripes = false;
    private IconProvider iconProvider;

    public StripeTree() {
    }

    public void setIconProvider(IconProvider iconProvider) {
        this.iconProvider = iconProvider;
    }

    public IconProvider getIconProvider() {
        return iconProvider;
    }

    public StripeTree(java.util.Hashtable<?, ?> value) {
        super(value);
    }

    public Color getEvenColor() {
        return rowColors[0];
    }

    public Color getOddColor() {
        return rowColors[1];
    }

    public void setEvenColor(Color color) {
        rowColors[0] = color;
    }

    public void setOddColor(Color color) {
        rowColors[1] = color;
    }

    public StripeTree(Object[] value) {
        super(value);
    }

    public StripeTree(javax.swing.tree.TreeModel newModel) {
        super(newModel);
    }

    public StripeTree(javax.swing.tree.TreeNode root) {
        super(root);
    }

    public StripeTree(javax.swing.tree.TreeNode root,
            boolean asksAllowsChildren) {
        super(root, asksAllowsChildren);
    }

    public StripeTree(java.util.Vector<?> value) {
        super(value);
    }

    /** Add zebra stripes to the background. */
    public void paintComponent(java.awt.Graphics g) {
        if (!(drawStripes = isOpaque())) {
            super.paintComponent(g);
            return;
        }

// Paint zebra background stripes
//        updateZebraColors();
        final java.awt.Insets insets = getInsets();
        final int w = getWidth() - insets.left - insets.right;
        final int h = getHeight() - insets.top - insets.bottom;
        final int x = insets.left;
        int y = insets.top;
        int nRows = 0;
        int startRow = 0;
        int rowHeight = getRowHeight();
        if (rowHeight > 0) {
            nRows = h / rowHeight;
        } else {
// Paint non-uniform height rows first
            final int nItems = getRowCount();
            rowHeight = 17; // A default for empty trees
            for (int i = 0; i < nItems; i++, y += rowHeight) {
                rowHeight = getRowBounds(i).height;
                g.setColor(rowColors[i & 1]);
                g.fillRect(x, y, w, rowHeight);
            }
// Use last row height for remainder of tree area
            nRows = nItems + (insets.top + h - y) / rowHeight;
            startRow = nItems;
        }
        for (int i = startRow; i < nRows; i++, y += rowHeight) {
            g.setColor(rowColors[i & 1]);
            g.fillRect(x, y, w, rowHeight);
        }
        final int remainder = insets.top + h - y;
        if (remainder > 0) {
            g.setColor(rowColors[nRows & 1]);
            g.fillRect(x, y, w, remainder);
        }

// Paint component
        setOpaque(false);
        super.paintComponent(g);
        setOpaque(true);
    }

    /** Wrap cell renderer and editor to add zebra background stripes. */
    public class RendererEditorWrapper
            implements javax.swing.tree.TreeCellRenderer,
            javax.swing.tree.TreeCellEditor {

        public javax.swing.tree.TreeCellRenderer ren = null;
        public javax.swing.tree.TreeCellEditor ed = null;

        public java.awt.Component getTreeCellRendererComponent(
                javax.swing.JTree tree, Object value,
                boolean selected, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            final java.awt.Component c =
                    ren.getTreeCellRendererComponent(
                    tree, value, selected, expanded,
                    leaf, row, hasFocus);

            DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) c;
            if (iconProvider != null) {
                renderer.setIcon(iconProvider.provideFor(value));
            }

            if (selected || !drawStripes) {
                return c;
            }
            if (!(c instanceof javax.swing.tree.DefaultTreeCellRenderer)) {
                c.setBackground(rowColors[row & 1]);
            } else {

                ((javax.swing.tree.DefaultTreeCellRenderer) c).setBackgroundNonSelectionColor(rowColors[row & 1]);
            }

            return c;
        }

        public java.awt.Component getTreeCellEditorComponent(
                javax.swing.JTree tree, Object value,
                boolean selected, boolean expanded,
                boolean leaf, int row) {
            final java.awt.Component c =
                    ed.getTreeCellEditorComponent(
                    tree, value, selected, expanded, leaf, row);
            if (!selected && drawStripes) {
                c.setBackground(rowColors[row & 1]);
            }
            return c;
        }

        public void addCellEditorListener(
                javax.swing.event.CellEditorListener l) {
            ed.addCellEditorListener(l);
        }

        public void cancelCellEditing() {
            ed.cancelCellEditing();
        }

        public Object getCellEditorValue() {
            return ed.getCellEditorValue();
        }

        public boolean isCellEditable(
                java.util.EventObject anEvent) {
            return ed.isCellEditable(anEvent);
        }

        public void removeCellEditorListener(
                javax.swing.event.CellEditorListener l) {
            ed.removeCellEditorListener(l);
        }

        public boolean shouldSelectCell(
                java.util.EventObject anEvent) {
            return ed.shouldSelectCell(anEvent);
        }

        public boolean stopCellEditing() {
            return ed.stopCellEditing();
        }
    }
    private RendererEditorWrapper wrapper = null;

    /** Return the wrapped cell renderer. */
    public javax.swing.tree.TreeCellRenderer getCellRenderer() {
        final javax.swing.tree.TreeCellRenderer ren = super.getCellRenderer();
        if (ren == null) {
            return null;
        }
        if (wrapper == null) {
            wrapper = new RendererEditorWrapper();
        }
        wrapper.ren = ren;
        return wrapper;
    }

    /** Return the wrapped cell editor. */
    public javax.swing.tree.TreeCellEditor getCellEditor() {
        final javax.swing.tree.TreeCellEditor ed = super.getCellEditor();
        if (ed == null) {
            return null;
        }
        if (wrapper == null) {
            wrapper = new RendererEditorWrapper();
        }
        wrapper.ed = ed;
        return wrapper;
    }
}