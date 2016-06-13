/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * StatusScreen.java
 *
 * Created on 24/11/2011, 16:25:11
 */
package bgu.dcr.az.dev.ui;

import bc.dsl.SwingDSL;
import bc.ui.swing.consoles.ConstraintCalcConsole.ConstraintShowListener;
import bc.ui.swing.trees.IconProvider;
import bc.ui.swing.useful.DataPanel;
import bc.ui.swing.visuals.Visual;
import bgu.dcr.az.api.prob.ImmutableProblem;
import bgu.dcr.az.api.exen.Experiment;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.exen.AbstractTest;
import java.awt.BorderLayout;
import java.util.Enumeration;
import javax.swing.Icon;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 *
 * @author bennyl
 */
public class ProblemViewScreen extends javax.swing.JPanel implements ConstraintShowListener {

    public static final String CONSTRAINT_MATRIX = "Constraints Matrix";
    private ImmutableProblem p;

    /**
     * Creates new form StatusScreen
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public ProblemViewScreen() {
        initComponents();
        calc.addListener(this);
        tree.setIconProvider(new IconProvider() {
            private Icon AGENT1_ICON = SwingDSL.resIcon("agent1");
            private Icon AGENT2_ICON = SwingDSL.resIcon("agent2");
            private Icon ALL_ICON = SwingDSL.resIcon("all-constraints");
            private Icon PROBLEM_ICON = SwingDSL.resIcon("problem");

            @Override
            public Icon provideFor(Object item) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) item;
                if (node.isLeaf()) {
                    if (((AgentInfo) node.getUserObject()).getName().equals(CONSTRAINT_MATRIX)) {
                        return ALL_ICON;
                    } else {
                        return AGENT2_ICON;
                    }
                } else if (node.isRoot()) {
                    return PROBLEM_ICON;
                } else {
                    return AGENT1_ICON;
                }
            }
        });

    }

    private void showProblem(final ImmutableProblem p) {
        if (p.type().isBinary()) {
            this.p = p;
            prepareTree();
            calc.setProblem(p);
//        this.calc.requestFocusInWindow();
            problemChangePan.setVisible(false);
        }
    }

    public void setModel(Experiment exp) {
        Visual.populate(testSelect, Visual.adapt(exp.getTests(), new Visual.VisualGen() {
            @Override
            public Visual gen(Object it) {
                Test r = (Test) it;
                return new Visual(it, r.getName(), "", null);
            }
        }));

        if (exp.getTotalNumberOfExecutions() == 0) {
            remove(contentPan);
            DataPanel msg = new DataPanel();
            msg.setNoDataText("There are no problems to view");
            add(msg, BorderLayout.CENTER);
        } else {
            pnumSelect.setValue(1);
            switchProblemView();
        }

        revalidate();
        repaint();
    }

    private void switchProblemView() {
        new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                Integer pnum = null;
                AbstractTest c = null;
                try {
                    c = (AbstractTest) Visual.getSelected(testSelect);
                    pnum = (Integer) pnumSelect.getValue();
                    Problem p = c.generateProblemForExternalUse(pnum);
                    showProblem(p);
                    problemViewingDescription.setText("Showing problem " + pnum + " of test " + c.getName());
                } catch (Exception ex) {
                    MessageDialog.showFail("cannot load problem (did you defined a problem generator?): ", ex.getMessage());
                }
                return null;
            }
        }.execute();

    }

    private void prepareTree() {


        DefaultMutableTreeNode r = (DefaultMutableTreeNode) tree.getModel().getRoot();
        r.removeAllChildren();
        r.setUserObject(new AgentInfo("Problem"));
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(new AgentInfo(CONSTRAINT_MATRIX), true);
        r.add(node);
//        System.out.println(r.getIndex(node));
        loadAgents(r);

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
//                System.out.println(node);
//                TreePath selectionPath = tree.getSelectionPath();
//                System.out.println(selectionPath.toString());

                if (node == null) { //Nothing is selected.
                    return;
                }
                Object nodeInfo = node.getUserObject();
                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) node.getParent();
                if (node.isLeaf()) {
                    AgentInfo agent = (AgentInfo) nodeInfo;
                    if (agent.getName().equals(CONSTRAINT_MATRIX)) {
                        prepareAll();
                    } else if (parentNode == tree.getModel().getRoot()) {
                        dataPane.unSetData();
                    } else {

                        Object parentInfo = parentNode.getUserObject();
                        AgentInfo parent = (AgentInfo) parentInfo;
                        prepareConstraints(parent.getId(), agent.getId());
                    }
                } else {
                    dataPane.unSetData();
                }
            }
        });
        dataPane.unSetData();
        tree.updateUI();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        constraintsTable = new bc.ui.swing.tables.ConstraintTable();
        contentPan = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        problemViewingDescription = new javax.swing.JLabel();
        jXHyperlink1 = new org.jdesktop.swingx.JXHyperlink();
        problemChangePan = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        testSelect = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        pnumSelect = new javax.swing.JSpinner();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        tree = new bc.ui.swing.trees.ScrollableStripeTree();
        dataPane = new bc.ui.swing.useful.DataPanel();
        calc = new bc.ui.swing.consoles.ConstraintCalcConsole();

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        contentPan.setBackground(new java.awt.Color(120, 120, 120));
        contentPan.setLayout(new java.awt.GridBagLayout());

        jPanel11.setBackground(new java.awt.Color(120, 120, 120));
        jPanel11.setLayout(new java.awt.GridBagLayout());

        problemViewingDescription.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        problemViewingDescription.setForeground(new java.awt.Color(255, 255, 255));
        problemViewingDescription.setText("Viewing Problem X of Test Y");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel11.add(problemViewingDescription, gridBagConstraints);

        jXHyperlink1.setForeground(new java.awt.Color(210, 233, 255));
        jXHyperlink1.setText("change?");
        jXHyperlink1.setClickedColor(new java.awt.Color(210, 233, 255));
        jXHyperlink1.setFont(new java.awt.Font("Consolas", 1, 12));
        jXHyperlink1.setUnclickedColor(new java.awt.Color(210, 233, 255));
        jXHyperlink1.setVerifyInputWhenFocusTarget(false);
        jXHyperlink1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        jXHyperlink1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jXHyperlink1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 3);
        jPanel11.add(jXHyperlink1, gridBagConstraints);

        problemChangePan.setOpaque(false);
        problemChangePan.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Test");
        problemChangePan.add(jLabel1);

        testSelect.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        problemChangePan.add(testSelect);

        jLabel2.setFont(new java.awt.Font("Consolas", 0, 12));
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText(", Problem Number");
        problemChangePan.add(jLabel2);

        pnumSelect.setPreferredSize(new java.awt.Dimension(50, 20));
        problemChangePan.add(pnumSelect);

        jButton1.setText("View");
        jButton1.setOpaque(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        problemChangePan.add(jButton1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel11.add(problemChangePan, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        contentPan.add(jPanel11, gridBagConstraints);

        jPanel3.setOpaque(false);
        jPanel3.setLayout(new java.awt.GridBagLayout());

        tree.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 3, new java.awt.Color(102, 102, 102)));
        tree.setMinimumSize(new java.awt.Dimension(200, 22));
        tree.setPreferredSize(new java.awt.Dimension(200, 440));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(tree, gridBagConstraints);

        dataPane.setBackground(new java.awt.Color(153, 153, 153));
        dataPane.setForeground(new java.awt.Color(255, 255, 255));
        dataPane.setNoDataForeColor(new java.awt.Color(255, 255, 255));
        dataPane.setNoDataText("No Constraint Table To Show");
        dataPane.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(dataPane, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel3.add(calc, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 3);
        contentPan.add(jPanel3, gridBagConstraints);

        add(contentPan, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jXHyperlink1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink1ActionPerformed
        problemChangePan.setVisible(true);
    }//GEN-LAST:event_jXHyperlink1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        switchProblemView();
        problemChangePan.setVisible(false);
        this.calc.startTyping();
    }//GEN-LAST:event_jButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private bc.ui.swing.consoles.ConstraintCalcConsole calc;
    private bc.ui.swing.tables.ConstraintTable constraintsTable;
    private javax.swing.JPanel contentPan;
    private bc.ui.swing.useful.DataPanel dataPane;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel3;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink1;
    private javax.swing.JSpinner pnumSelect;
    private javax.swing.JPanel problemChangePan;
    private javax.swing.JLabel problemViewingDescription;
    private javax.swing.JComboBox testSelect;
    private bc.ui.swing.trees.ScrollableStripeTree tree;
    // End of variables declaration//GEN-END:variables

    private void loadAgents(DefaultMutableTreeNode r) {
        int varNum = p.getNumberOfVariables();
        for (int i = 0; i < varNum; i++) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(new AgentInfo(i), true);
            for (Integer j : p.getNeighbors(i)) {
                final DefaultMutableTreeNode child = new DefaultMutableTreeNode(new AgentInfo(j), false);
                node.add(child);
//                System.out.println("index of agent " + j + " is " + node.getIndex(child));
            }
            r.add(node);
//            System.out.println("index of agent " + i + " is " + r.getIndex(node));
        }
    }

    private void prepareAll() {
        int numVars = p.getNumberOfVariables();
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnCount(numVars + 1);
        model.setRowCount(numVars + 1);
        for (int i = 0; i < numVars; i++) {
            model.setValueAt(i, 0, i + 1);
        }
        for (int j = 0; j < numVars; j++) {
            model.setValueAt(j, j + 1, 0);
            for (int i = 0; i < numVars; i++) {
                if (p.isConstrained(i, j)) {
                    model.setValueAt(1, j + 1, i + 1);
                } else {
                    model.setValueAt(0, j + 1, i + 1);
                }
            }
        }
        this.constraintsTable.getTable().setModel(model);
        this.constraintsTable.getTable().setTableHeader(null);
//        this.constraintsTablePane.setColumnHeaderView(null);
        this.dataPane.setData(constraintsTable);
        this.constraintsTable.updateUI();

    }

    private void prepareConstraints(int ai, int aj) {
        int domVars = p.getDomainSize(ai);
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnCount(domVars + 1);
        model.setRowCount(domVars + 1);
        String name = "" + aj + " / " + ai;
        model.setValueAt(name, 0, 0);
        for (int i = 0; i < domVars; i++) {
            model.setValueAt(i, 0, i + 1);
        }
        for (int j = 0; j < domVars; j++) {
            model.setValueAt(j, j + 1, 0);
            for (int i = 0; i < domVars; i++) {
                int cost = p.getConstraintCost(ai, i, aj, j);
                model.setValueAt(cost, j + 1, i + 1);
            }
        }

        this.constraintsTable.getTable().setModel(model);
        this.constraintsTable.getTable().setTableHeader(
                null);


//        this.constraintsTable.setColumnHeaderView(null);
        this.dataPane.setData(constraintsTable);


        this.constraintsTable.getTable().updateUI();
    }

    @Override
    public boolean onConstraintShowRequested(int i, int j) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        Enumeration parents = root.children();
        while (parents.hasMoreElements()) {
            DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parents.nextElement();
            if (((AgentInfo) parentNode.getUserObject()).getId() == i) {
                Enumeration children = parentNode.children();
                while (children.hasMoreElements()) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) children.nextElement();
                    if (((AgentInfo) node.getUserObject()).getId() == j) {
                        TreePath treePath = new TreePath(new Object[]{root, parentNode, node});
//                        System.out.println(treePath.toString());
                        tree.setSelectionPath(treePath);
                        tree.expandPath(treePath);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.calc.startTyping();
    }

    private static class AgentInfo {

        private final String name;
        private final int id;

        public AgentInfo(int id) {
            this.id = id;
            this.name = "Agent " + id;
        }

        public AgentInfo(String name) {
            this.name = name;
            this.id = -1;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
