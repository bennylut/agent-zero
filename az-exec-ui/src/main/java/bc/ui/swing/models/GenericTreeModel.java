/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.ui.swing.models;

import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author BLutati
 */
public class GenericTreeModel<T> implements TreeModel, TreeSelectionListener {

    Node<T> root;
    List<TreeModelListener> listeners = new LinkedList<TreeModelListener>();
    T selectedItem;

    public GenericTreeModel(Node<T> root) {
        this.root = root;
    }

    public void setSelectedItem(T selectedItem) {
        this.selectedItem = selectedItem;
    }

    public T getSelectedItem() {
        return selectedItem;
    }

    protected void setRoot(Node<T> root) {
        this.root = root;
        fireTreeStractureChanged();
    }

    public void fireTreeStractureChanged() {
        for (TreeModelListener l : listeners) {
            l.treeStructureChanged(new TreeModelEvent(this, new Object[]{root}));
        }
    }

    public void fireNodeAdded(Node n) {
        List ptp = n.getPathToRoot();
        ptp.remove(n);
        Object[] aptp = ptp.toArray();
        final TreeModelEvent treeModelEvent = new TreeModelEvent(this, aptp, new int[]{n.getParent().getChildIndex(n)}, new Object[]{n});
        for (TreeModelListener l : listeners) {
            l.treeNodesInserted(treeModelEvent);
        }
    }
    
    public void fireNodeChanged(Node n) {
        List ptp = n.getPathToRoot();
        ptp.remove(n);
        Object[] aptp = ptp.toArray();
        final TreeModelEvent treeModelEvent = new TreeModelEvent(this, aptp, new int[]{n.getParent().getChildIndex(n)}, new Object[]{n});
        for (TreeModelListener l : listeners) {
            l.treeNodesChanged(treeModelEvent);
        }
    }

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        Node<T> nparent = (Node<T>) parent;
        return nparent.getChildren().get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        Node<T> nparent = (Node<T>) parent;
        return nparent.getChildren().size();
    }

    @Override
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        throw new UnsupportedOperationException("Imutable Tree Change Attempt");
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        Node<T> nparent = (Node<T>) parent;
        return nparent.getChildren().indexOf(child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    public void valueChanged(TreeSelectionEvent e) {
        selectedItem = ((Node<T>) e.getPath().getLastPathComponent()).getData();
    }

    public static abstract class Node<T> {

        private T data;
        private String string;
        private ImageIcon icon = null;
        private boolean selected;
        private Node parent;

        public Node(T data, Node parent) {
            this.data = data;
            this.parent = parent;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public List<Node> getPathToRoot() {
            if (parent == null) {
                LinkedList<Node> l = new LinkedList<Node>();
                l.add(this);
                return l;
            }
            List<Node> l = parent.getPathToRoot();
            l.add(this);
            return l;
        }

        public void setIcon(ImageIcon icon) {
            this.icon = icon;
        }

        public ImageIcon getIcon() {
            return icon;
        }

        public T getData() {
            return data;
        }

        public abstract List<Node<T>> getChildren();

        public int getChildIndex(Node child) {
            return getChildren().indexOf(child);
        }

        @Override
        public String toString() {
            return string == null ? data.toString() : string;
        }

        public boolean isLeaf() {
            return getChildren().isEmpty();
        }

        void setString(String string) {
            this.string = string;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public boolean isSelected() {
            return selected;
        }
    }

    public static class LeafNode<T> extends Node<T> {

        public LeafNode(T data, Node parent) {
            super(data, parent);
        }

        @Override
        public List<Node<T>> getChildren() {
            return new LinkedList<Node<T>>();
        }
    }

    public static class ListNode extends Node {

        List<Node> childs = new LinkedList<Node>();

        public ListNode(Object data, Node parent) {
            super(data, parent);
        }

        @Override
        public List<Node> getChildren() {
            return childs;
        }
    }
}
