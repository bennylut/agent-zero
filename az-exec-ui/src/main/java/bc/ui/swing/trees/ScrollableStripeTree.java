/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ScrollableStripeTree.java
 *
 * Created on 28/11/2011, 22:18:16
 */
package bc.ui.swing.trees;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.accessibility.AccessibleContext;
import javax.swing.DropMode;
import javax.swing.JFrame;
import javax.swing.JTree.DropLocation;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.plaf.TreeUI;
import javax.swing.text.Position.Bias;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author bennyl
 */
public class ScrollableStripeTree extends javax.swing.JPanel {

    /** Creates new form ScrollableStripeTree */
    public ScrollableStripeTree() {
        initComponents();
    }

    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(new ScrollableStripeTree());
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (tree != null){
            tree.updateUI();
        }
    }

    public void setIconProvider(IconProvider iconProvider) {
        tree.setIconProvider(iconProvider);
    }

    public IconProvider getIconProvider() {
        return tree.getIconProvider();
    }
    
    
    
    public void addTreeSelectionListener(TreeSelectionListener tsl) {
        tree.addTreeSelectionListener(tsl);
    }

    public void setSelectionModel(TreeSelectionModel selectionModel) {
        tree.setSelectionModel(selectionModel);
    }

    public TreeSelectionModel getSelectionModel() {
        return tree.getSelectionModel();
    }

    public void setModel(TreeModel newModel) {
        tree.setModel(newModel);
    }

    public void treeDidChange() {
        tree.treeDidChange();
    }

    public boolean stopEditing() {
        return tree.stopEditing();
    }

    public void startEditingAtPath(TreePath path) {
        tree.startEditingAtPath(path);
    }

    public void setVisibleRowCount(int newCount) {
        tree.setVisibleRowCount(newCount);
    }

    public void setUI(TreeUI ui) {
        tree.setUI(ui);
    }

    public void setToggleClickCount(int clickCount) {
        tree.setToggleClickCount(clickCount);
    }

    public void setShowsRootHandles(boolean newValue) {
        tree.setShowsRootHandles(newValue);
    }

    public void setSelectionRows(int[] rows) {
        tree.setSelectionRows(rows);
    }

    public void setSelectionRow(int row) {
        tree.setSelectionRow(row);
    }

    public void setSelectionPaths(TreePath[] paths) {
        tree.setSelectionPaths(paths);
    }

    public void setSelectionPath(TreePath path) {
        tree.setSelectionPath(path);
    }

    public void setSelectionInterval(int index0, int index1) {
        tree.setSelectionInterval(index0, index1);
    }

    public void setScrollsOnExpand(boolean newValue) {
        tree.setScrollsOnExpand(newValue);
    }

    public void setRootVisible(boolean rootVisible) {
        tree.setRootVisible(rootVisible);
    }


    public void setLeadSelectionPath(TreePath newPath) {
        tree.setLeadSelectionPath(newPath);
    }

    public void setLargeModel(boolean newValue) {
        tree.setLargeModel(newValue);
    }

    public void setInvokesStopCellEditing(boolean newValue) {
        tree.setInvokesStopCellEditing(newValue);
    }

    public void setExpandsSelectedPaths(boolean newValue) {
        tree.setExpandsSelectedPaths(newValue);
    }

    public void setEditable(boolean flag) {
        tree.setEditable(flag);
    }

    public final void setDropMode(DropMode dropMode) {
        tree.setDropMode(dropMode);
    }

    public void setDragEnabled(boolean b) {
        tree.setDragEnabled(b);
    }

    public void setCellRenderer(TreeCellRenderer x) {
        tree.setCellRenderer(x);
    }

    public void setCellEditor(TreeCellEditor cellEditor) {
        tree.setCellEditor(cellEditor);
    }

    public void setAnchorSelectionPath(TreePath newPath) {
        tree.setAnchorSelectionPath(newPath);
    }

    public void scrollRowToVisible(int row) {
        tree.scrollRowToVisible(row);
    }

    public void scrollPathToVisible(TreePath path) {
        tree.scrollPathToVisible(path);
    }

    public void removeTreeWillExpandListener(TreeWillExpandListener tel) {
        tree.removeTreeWillExpandListener(tel);
    }

    public void removeTreeSelectionListener(TreeSelectionListener tsl) {
        tree.removeTreeSelectionListener(tsl);
    }

    public void removeTreeExpansionListener(TreeExpansionListener tel) {
        tree.removeTreeExpansionListener(tel);
    }

    public void removeSelectionRows(int[] rows) {
        tree.removeSelectionRows(rows);
    }

    public void removeSelectionRow(int row) {
        tree.removeSelectionRow(row);
    }

    public void removeSelectionPaths(TreePath[] paths) {
        tree.removeSelectionPaths(paths);
    }

    public void removeSelectionPath(TreePath path) {
        tree.removeSelectionPath(path);
    }

    public void removeSelectionInterval(int index0, int index1) {
        tree.removeSelectionInterval(index0, index1);
    }

    public void makeVisible(TreePath path) {
        tree.makeVisible(path);
    }

    public boolean isVisible(TreePath path) {
        return tree.isVisible(path);
    }

    public boolean isSelectionEmpty() {
        return tree.isSelectionEmpty();
    }

    public boolean isRowSelected(int row) {
        return tree.isRowSelected(row);
    }

    public boolean isRootVisible() {
        return tree.isRootVisible();
    }

    public boolean isPathSelected(TreePath path) {
        return tree.isPathSelected(path);
    }

    public boolean isPathEditable(TreePath path) {
        return tree.isPathEditable(path);
    }

    public boolean isLargeModel() {
        return tree.isLargeModel();
    }

    public boolean isFixedRowHeight() {
        return tree.isFixedRowHeight();
    }

    public boolean isExpanded(int row) {
        return tree.isExpanded(row);
    }

    public boolean isExpanded(TreePath path) {
        return tree.isExpanded(path);
    }

    public boolean isEditing() {
        return tree.isEditing();
    }

    public boolean isEditable() {
        return tree.isEditable();
    }

    public boolean isCollapsed(int row) {
        return tree.isCollapsed(row);
    }

    public boolean isCollapsed(TreePath path) {
        return tree.isCollapsed(path);
    }

    public boolean hasBeenExpanded(TreePath path) {
        return tree.hasBeenExpanded(path);
    }

    public int getVisibleRowCount() {
        return tree.getVisibleRowCount();
    }

    public StripeTree getInnerTree() {
        return tree;
    }

    public TreeWillExpandListener[] getTreeWillExpandListeners() {
        return tree.getTreeWillExpandListeners();
    }

    public TreeSelectionListener[] getTreeSelectionListeners() {
        return tree.getTreeSelectionListeners();
    }

    public TreeExpansionListener[] getTreeExpansionListeners() {
        return tree.getTreeExpansionListeners();
    }

    public String getToolTipText(MouseEvent event) {
        return tree.getToolTipText(event);
    }

    public int getToggleClickCount() {
        return tree.getToggleClickCount();
    }

    public boolean getShowsRootHandles() {
        return tree.getShowsRootHandles();
    }

    public int[] getSelectionRows() {
        return tree.getSelectionRows();
    }

    public TreePath[] getSelectionPaths() {
        return tree.getSelectionPaths();
    }

    public TreePath getSelectionPath() {
        return tree.getSelectionPath();
    }


    public int getSelectionCount() {
        return tree.getSelectionCount();
    }

    public boolean getScrollsOnExpand() {
        return tree.getScrollsOnExpand();
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return tree.getScrollableUnitIncrement(visibleRect, orientation, direction);
    }

    public boolean getScrollableTracksViewportWidth() {
        return tree.getScrollableTracksViewportWidth();
    }

    public boolean getScrollableTracksViewportHeight() {
        return tree.getScrollableTracksViewportHeight();
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return tree.getScrollableBlockIncrement(visibleRect, orientation, direction);
    }

    public int getRowForPath(TreePath path) {
        return tree.getRowForPath(path);
    }

    public int getRowForLocation(int x, int y) {
        return tree.getRowForLocation(x, y);
    }

    public int getRowCount() {
        return tree.getRowCount();
    }

    public Rectangle getRowBounds(int row) {
        return tree.getRowBounds(row);
    }

    public Dimension getPreferredScrollableViewportSize() {
        return tree.getPreferredScrollableViewportSize();
    }

    public TreePath getPathForRow(int row) {
        return tree.getPathForRow(row);
    }

    public TreePath getPathForLocation(int x, int y) {
        return tree.getPathForLocation(x, y);
    }

    public Rectangle getPathBounds(TreePath path) {
        return tree.getPathBounds(path);
    }

    public TreePath getNextMatch(String prefix, int startingRow, Bias bias) {
        return tree.getNextMatch(prefix, startingRow, bias);
    }

    public TreeModel getModel() {
        return tree.getModel();
    }

    public int getMinSelectionRow() {
        return tree.getMinSelectionRow();
    }

    public int getMaxSelectionRow() {
        return tree.getMaxSelectionRow();
    }

    public int getLeadSelectionRow() {
        return tree.getLeadSelectionRow();
    }

    public TreePath getLeadSelectionPath() {
        return tree.getLeadSelectionPath();
    }

    public Object getLastSelectedPathComponent() {
        return tree.getLastSelectedPathComponent();
    }

    public boolean getInvokesStopCellEditing() {
        return tree.getInvokesStopCellEditing();
    }

    public boolean getExpandsSelectedPaths() {
        return tree.getExpandsSelectedPaths();
    }

    public Enumeration<TreePath> getExpandedDescendants(TreePath parent) {
        return tree.getExpandedDescendants(parent);
    }

    public TreePath getEditingPath() {
        return tree.getEditingPath();
    }

    public final DropMode getDropMode() {
        return tree.getDropMode();
    }

    public final DropLocation getDropLocation() {
        return tree.getDropLocation();
    }

    public boolean getDragEnabled() {
        return tree.getDragEnabled();
    }

    public int getClosestRowForLocation(int x, int y) {
        return tree.getClosestRowForLocation(x, y);
    }

    public TreePath getClosestPathForLocation(int x, int y) {
        return tree.getClosestPathForLocation(x, y);
    }

    public TreePath getAnchorSelectionPath() {
        return tree.getAnchorSelectionPath();
    }

    public AccessibleContext getAccessibleContext() {
        return tree.getAccessibleContext();
    }

    public void fireTreeWillExpand(TreePath path) throws ExpandVetoException {
        tree.fireTreeWillExpand(path);
    }

    public void fireTreeWillCollapse(TreePath path) throws ExpandVetoException {
        tree.fireTreeWillCollapse(path);
    }

    public void fireTreeExpanded(TreePath path) {
        tree.fireTreeExpanded(path);
    }

    public void fireTreeCollapsed(TreePath path) {
        tree.fireTreeCollapsed(path);
    }

    public void expandRow(int row) {
        tree.expandRow(row);
    }

    public void expandPath(TreePath path) {
        tree.expandPath(path);
    }

    public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        return tree.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
    }

    public void collapseRow(int row) {
        tree.collapseRow(row);
    }

    public void collapsePath(TreePath path) {
        tree.collapsePath(path);
    }

    public void clearSelection() {
        tree.clearSelection();
    }

    public void cancelEditing() {
        tree.cancelEditing();
    }

    public void addTreeWillExpandListener(TreeWillExpandListener tel) {
        tree.addTreeWillExpandListener(tel);
    }

    public void addTreeExpansionListener(TreeExpansionListener tel) {
        tree.addTreeExpansionListener(tel);
    }

    public void addSelectionRows(int[] rows) {
        tree.addSelectionRows(rows);
    }

    public void addSelectionRow(int row) {
        tree.addSelectionRow(row);
    }

    public void addSelectionPaths(TreePath[] paths) {
        tree.addSelectionPaths(paths);
    }

    public void addSelectionPath(TreePath path) {
        tree.addSelectionPath(path);
    }

    public void addSelectionInterval(int index0, int index1) {
        tree.addSelectionInterval(index0, index1);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scroll = new javax.swing.JScrollPane();
        tree = new bc.ui.swing.trees.StripeTree();

        setLayout(new java.awt.BorderLayout());

        scroll.setBorder(null);

        tree.setRowHeight(22);
        scroll.setViewportView(tree);

        add(scroll, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scroll;
    private bc.ui.swing.trees.StripeTree tree;
    // End of variables declaration//GEN-END:variables
}
