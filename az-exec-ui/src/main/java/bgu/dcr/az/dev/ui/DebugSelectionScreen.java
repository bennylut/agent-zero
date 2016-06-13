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
import bc.ui.swing.listeners.Listeners;
import bc.ui.swing.visuals.Visual;
import bgu.dcr.az.api.exen.Experiment;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.exen.escan.ExperimentReader;
import bgu.dcr.az.exen.ExperimentImpl;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author bennyl
 */
public class DebugSelectionScreen extends javax.swing.JPanel implements TestView.DebugRequestListener {

    File problemsPath;
    List<Experiment> badExperiments;
    Map<Experiment, File> efMap;
    Listeners<DebugSelectionListener> debugListeners = Listeners.Builder.newInstance(DebugSelectionListener.class);

    /** Creates new form StatusScreen */
    @SuppressWarnings("LeakingThisInConstructor")
    public DebugSelectionScreen() {
        initComponents();
        testView.addDebugRequestListener(this);
    }

    void setProblemDir(File dir) {
        problemsPath = dir;
        badExperiments = new LinkedList<Experiment>();
        efMap = new HashMap<Experiment, File>();
        loadExperiments(dir);
        failList.setItems(Visual.adapt(badExperiments, new Visual.VisualGen() {

            @Override
            public Visual gen(Object it) {
                try {
                    ExperimentImpl ee = (ExperimentImpl) it;
                    SimpleDateFormat format = new SimpleDateFormat("dd'/'MM'/'yy' 'HH':'mm':'ss");
                    return new Visual(it, format.format(new Date(Long.valueOf(ee.getDebugInfo().getName()))), "", null);
                } catch (Exception ex) {
                    System.out.println("Cannot visualize " + it.toString() + ", ignoring. (err: " + ex.getMessage() + ")");
                    return new Visual(it, "cannot read", "cannot read", null);
                }
            }
        }));

        failList.addSelectionListner(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                testData.unSetData();
                if (!failList.getSelectedItems().isEmpty()) {
                    ExperimentImpl selected = (ExperimentImpl) ((Visual) failList.getSelectedItems().get(0)).getItem();
                    List<Test> tests = selected.getTests();
                    String testName = selected.getDebugInfo().getSelectedTest();

                    for (Test r : tests) {
                        if (r.getName().equals(testName)) {
                            testView.setModel(r);
                            testView.addFailureData(selected.getDebugInfo());
                            testData.setData(testViewScroll);
                            break;
                        }
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingDSL.configureUI();
        DebugSelectionScreen me = new DebugSelectionScreen();
        me.setProblemDir(new File("fail-problems"));
        SwingDSL.showInFrame(me);
    }

    private void deleteItem(Visual i) {
        Experiment e = (Experiment) i.getItem();
        File f = efMap.get(e);
        f.delete();
        efMap.remove(e);
        badExperiments.remove(e);
        failList.remove(i);
    }

    private void loadExperiments(File dir) {
        for (File f : dir.listFiles()) {
            try {
                Experiment e = ExperimentReader.read(f);
                badExperiments.add(e);
                efMap.put(e, f);
            } catch (Exception ex) {
                System.out.println("There was an error reading the file " + f.getName() + ", ignoring it. (err: " + ex.getMessage() + ")");
            }
        }
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

        testViewScroll = new javax.swing.JScrollPane();
        testView = new bgu.dcr.az.dev.ui.TestView();
        jPanel1 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        deleteSelected = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        failList = new bc.ui.swing.lists.StripeList();
        debugProblemButtonPan = new javax.swing.JPanel();
        jXHyperlink1 = new org.jdesktop.swingx.JXHyperlink();
        testData = new bc.ui.swing.useful.DataPanel();

        testViewScroll.setViewportView(testView);

        setOpaque(false);
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(120, 120, 120));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel11.setBackground(new java.awt.Color(120, 120, 120));
        jPanel11.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 3));

        deleteSelected.setBackground(null);
        deleteSelected.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/delete-selected.png"))); // NOI18N
        deleteSelected.setToolTipText("Delete the selected problems from the list");
        deleteSelected.setMinimumSize(new java.awt.Dimension(25, 25));
        deleteSelected.setOpaque(false);
        deleteSelected.setPreferredSize(new java.awt.Dimension(25, 25));
        deleteSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSelectedActionPerformed(evt);
            }
        });
        jPanel11.add(deleteSelected);

        jLabel4.setFont(new java.awt.Font("Consolas", 1, 12));
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Select one of the failed sessions to debug");
        jPanel11.add(jLabel4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel1.add(jPanel11, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        failList.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 3, new java.awt.Color(120, 120, 120)));
        failList.setMinimumSize(new java.awt.Dimension(200, 24));
        failList.setOddBackColor(new java.awt.Color(230, 230, 230));
        failList.setOddForeColor(new java.awt.Color(61, 61, 61));
        failList.setPreferredSize(new java.awt.Dimension(200, 194));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(failList, gridBagConstraints);

        debugProblemButtonPan.setBackground(new java.awt.Color(102, 102, 102));
        debugProblemButtonPan.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 0, 0, 3, new java.awt.Color(120, 120, 120)));
        debugProblemButtonPan.setLayout(new java.awt.GridBagLayout());

        jXHyperlink1.setForeground(new java.awt.Color(255, 255, 255));
        jXHyperlink1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/debug-all.png"))); // NOI18N
        jXHyperlink1.setText("Debug Full Experiment");
        jXHyperlink1.setClickedColor(new java.awt.Color(255, 255, 255));
        jXHyperlink1.setFont(new java.awt.Font("Consolas", 0, 14));
        jXHyperlink1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jXHyperlink1.setUnclickedColor(new java.awt.Color(255, 255, 255));
        jXHyperlink1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        debugProblemButtonPan.add(jXHyperlink1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        jPanel3.add(debugProblemButtonPan, gridBagConstraints);

        testData.setBackground(new java.awt.Color(153, 153, 153));
        testData.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(testData, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 3);
        jPanel1.add(jPanel3, gridBagConstraints);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void deleteSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSelectedActionPerformed
        for (Visual i : failList.getSelectedItems()) {
            deleteItem(i);
        }
    }//GEN-LAST:event_deleteSelectedActionPerformed

    private void jXHyperlink1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink1ActionPerformed
        debugListeners.fire().onFullExperimentDebugRequested();
    }//GEN-LAST:event_jXHyperlink1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel debugProblemButtonPan;
    private javax.swing.JButton deleteSelected;
    private bc.ui.swing.lists.StripeList failList;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel3;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink1;
    private bc.ui.swing.useful.DataPanel testData;
    private bgu.dcr.az.dev.ui.TestView testView;
    private javax.swing.JScrollPane testViewScroll;
    // End of variables declaration//GEN-END:variables

    @Override
    public void onDebugRequested() {
        debugListeners.fire().onSpecificExperimentDebugRequested((Experiment) ((Visual) failList.getSelectedItems().get(0)).getItem());
    }

    public Listeners<DebugSelectionListener> getDebugListeners() {
        return debugListeners;
    }

    public static interface DebugSelectionListener {

        void onFullExperimentDebugRequested();

        void onSpecificExperimentDebugRequested(Experiment exp);
    }
}
