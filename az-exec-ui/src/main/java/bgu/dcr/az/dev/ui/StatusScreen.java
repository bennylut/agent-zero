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
package bgu.dcr.az.dev.ui;

import bc.dsl.SwingDSL;
import bc.ui.swing.visuals.Visual;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.Experiment;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.dev.ExperimentExecutionController;
import com.sun.java.swing.plaf.motif.MotifProgressBarUI;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BoundedRangeModel;
import javax.swing.Icon;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author bennyl
 */
public class StatusScreen extends javax.swing.JPanel {

    private static final Icon TEST_WAIT_ICON = SwingDSL.resIcon("test-wait");
    private static final Icon TEST_DONE_ICON = SwingDSL.resIcon("test-done");
    private static final Icon TEST_PLAY_ICON = SwingDSL.resIcon("test-play");

    
    private LinkedList<Visual> visualTests;

    /** Creates new form StatusScreen */
    public StatusScreen() {
        initComponents();
        execProgress.setUI(new MotifProgressBarUI());
    }

    void setModel(Experiment experiment) {
        visualTests = Visual.adapt(experiment.getTests(), new Visual.VisualGen() {

            @Override
            public Visual gen(Object it) {
                Test r = (Test) it;
                return new Visual(it, r.getName(), "", TEST_WAIT_ICON);
            }
        });
        testList.setItems(visualTests);

        testList.addSelectionListner(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                List<Visual> items = testList.getSelectedItems();
                if (items.isEmpty()) {
                    testData.unSetData();
                } else {
                    testView.setModel((Test) items.get(0).getItem());
                    testData.setData(testDataScroll);
                }
            }
        });
        // EXECUTION PROGRESS BAR
        final BoundedRangeModel mod = execProgress.getModel();
        mod.setMinimum(0);

        final int expLength = experiment.getTotalNumberOfExecutions();

        mod.setMaximum(expLength);
        progressLabel.setText("Execution 1 " + " of " + expLength);
        ExperimentExecutionController.UNIT.addExperimentListener(new Experiment.ExperimentListener() {

            @Override
            public void onExpirementStarted(Experiment source) {
            }

            @Override
            public void onExpirementEnded(Experiment source) {
                for (Visual v : visualTests) {
                    v.setIcon(TEST_DONE_ICON);
                }

                mod.setValue(mod.getMaximum());
                progressLabel.setText("Execution " + (mod.getValue()) + " of " + expLength);
                
                testList.revalidate();
                testList.repaint();
            }

            @Override
            public void onNewTestStarted(Experiment source, Test test) {
                for (Visual v : visualTests) {
                    if (v.getItem().equals(test)) {
                        v.setIcon(TEST_PLAY_ICON);
                        break;
                    } else {
                        v.setIcon(TEST_DONE_ICON);
                    }
                }

                testList.revalidate();
                testList.repaint();
            }

            @Override
            public void onNewExecutionStarted(Experiment source, Test test, Execution exec) {
                mod.setValue(test.getCurrentExecutionNumber());
                progressLabel.setText("Execution " + (mod.getValue()) + " of " + expLength);
            }

            @Override
            public void onExecutionEnded(Experiment source, Test test, Execution exec) {
                
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        testDataScroll = new javax.swing.JScrollPane();
        testView = new bgu.dcr.az.dev.ui.TestView();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        progressLabel = new javax.swing.JLabel();
        execProgress = new javax.swing.JProgressBar();
        jPanel1 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        testList = new bc.ui.swing.lists.StripeList();
        testData = new bc.ui.swing.useful.DataPanel();

        testDataScroll.setBorder(null);
        testDataScroll.setViewportView(testView);

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));
        jPanel2.setOpaque(false);
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/monitor.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jLabel1, gridBagConstraints);

        progressLabel.setFont(new java.awt.Font("Consolas", 1, 14));
        progressLabel.setForeground(new java.awt.Color(255, 255, 255));
        progressLabel.setText("Execution 0 of 16");
        progressLabel.setDoubleBuffered(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jPanel2.add(progressLabel, gridBagConstraints);

        execProgress.setBackground(null);
        execProgress.setForeground(new java.awt.Color(255, 255, 255));
        execProgress.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(71, 71, 71), 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 0);
        jPanel2.add(execProgress, gridBagConstraints);

        add(jPanel2, java.awt.BorderLayout.NORTH);

        jPanel1.setBackground(new java.awt.Color(120, 120, 120));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel11.setBackground(new java.awt.Color(120, 120, 120));
        jPanel11.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 3));

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Executing Tests");
        jPanel11.add(jLabel4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel1.add(jPanel11, gridBagConstraints);

        jPanel3.setBackground(new java.awt.Color(102, 102, 102));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        testList.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 3, new java.awt.Color(153, 153, 153)));
        testList.setFont(new java.awt.Font("Consolas", 0, 14));
        testList.setMinimumSize(new java.awt.Dimension(200, 24));
        testList.setOddBackColor(new java.awt.Color(230, 230, 230));
        testList.setOddForeColor(new java.awt.Color(61, 61, 61));
        testList.setPreferredSize(new java.awt.Dimension(200, 194));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(testList, gridBagConstraints);

        testData.setNoDataForeColor(new java.awt.Color(255, 255, 255));
        testData.setNoDataText("No Test Selected");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        jPanel3.add(testData, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 3);
        jPanel1.add(jPanel3, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar execProgress;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel progressLabel;
    private bc.ui.swing.useful.DataPanel testData;
    private javax.swing.JScrollPane testDataScroll;
    private bc.ui.swing.lists.StripeList testList;
    private bgu.dcr.az.dev.ui.TestView testView;
    // End of variables declaration//GEN-END:variables
}
