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
import bc.ui.swing.charts.BarChart;
import bc.ui.swing.models.DataExtractor;
import bc.ui.swing.models.GenericTableModel;
import bc.ui.swing.charts.LineChart;
import bc.ui.swing.listeners.SelectionListener;
import bc.ui.swing.lists.OptionList;
import bc.ui.swing.visuals.Visual;
import bgu.dcr.az.api.exen.Experiment;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.api.exen.escan.VariableMetadata;
import bgu.dcr.az.api.exen.mdef.StatisticCollector;
import bgu.dcr.az.api.exen.stat.VisualModel;
import bgu.dcr.az.api.exen.stat.vmod.BarVisualModel;
import bgu.dcr.az.api.exen.stat.vmod.LineVisualModel;
import bgu.dcr.az.exen.stat.db.DatabaseUnit;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JFrame;

/**
 *
 * @author bennyl
 */
public class StatisticsScreen extends javax.swing.JPanel {

    private OptionList availableStatisticsList;
    private OptionList testsList;
    private StatisticCollector selectedCollector = null;
    private Test selectedTest = null;
    private VisualModel showedVisualization = null;

    /** Creates new form StatisticsScreen */
    public StatisticsScreen() {
        initComponents();
        //STAT
        availableStatisticsList = new OptionList();
        statScroll.setViewportView(availableStatisticsList);

        //TEST
        testsList = new OptionList();
        testScroll.setViewportView(testsList);

        statScroll.getViewport().setOpaque(false);
        varscrolls.getViewport().setOpaque(false);
        testScroll.getViewport().setOpaque(false);
        
        //ADVANCED
        queryScr.addListener(new StatisticsQueryScreen.Listener() {

            @Override
            public void onSwitchClicked() {
                getCards().show(StatisticsScreen.this, "SimpleMode");
            }
        });
    }

    public void setModel(Experiment exp, DatabaseUnit.H2Database database) {
        queryScr.setModel(database);
        
        availableStatisticsList.getSelectionListeners().addListener(new SelectionListener() {

            @Override
            public void onSelectionChanged(Object source, List selectedItems) {
                varsDataPan.unSetData();
                selectedCollector = null;

                if (!selectedItems.isEmpty()) {
                    StatisticCollector sc = (StatisticCollector) ((Visual) selectedItems.get(0)).getItem();
                    selectedCollector = sc;
                    VariableMetadata[] scaned = VariableMetadata.scan(sc);
                    if (scaned.length > 0) {
                        vars.setModel(scaned);
                        varsDataPan.setData(varscrolls);
                    }
                }

            }
        });

        testsList.getSelectionListeners().addListener(new SelectionListener() {

            @Override
            public void onSelectionChanged(Object source, List selectedItems) {
                availableStatisticsList.clear();
                selectedTest = null;

                if (!selectedItems.isEmpty()) {

                    selectedTest = ((Test) ((Visual) selectedItems.get(0)).getItem());
                    availableStatisticsList.setItems(Visual.adapt(selectedTest.getStatisticCollectors(), new Visual.VisualGen() {

                        @Override
                        public Visual gen(Object it) {
                            StatisticCollector sc = (StatisticCollector) it;
                            return new Visual(it, sc.getName(), "", null);
                        }
                    }));
                }
            }
        });

        testsList.setItems(Visual.adapt(exp.getTests(), new Visual.VisualGen() {

            @Override
            public Visual gen(Object it) {
                Test r = (Test) it;
                return new Visual(it, r.getName(), "", null);
            }
        }));

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setContentPane(new StatisticsScreen());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
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

        resultsPan = new javax.swing.JSplitPane();
        chartResultPan = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        resultList = new bc.ui.swing.tables.ScrollableStripeTable();
        exportToCSVButton = new javax.swing.JPanel();
        jXHyperlink2 = new org.jdesktop.swingx.JXHyperlink();
        varscrolls = new javax.swing.JScrollPane();
        vars = new bc.ui.swing.configurable.VariablesEditor();
        jPanel13 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jXHyperlink3 = new org.jdesktop.swingx.JXHyperlink();
        jLabel10 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        testScroll = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        statScroll = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        varsDataPan = new bc.ui.swing.useful.DataPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jXHyperlink1 = new org.jdesktop.swingx.JXHyperlink();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        resultDataPan = new bc.ui.swing.useful.DataPanel();
        queryScr = new bgu.dcr.az.dev.ui.StatisticsQueryScreen();

        resultsPan.setBorder(null);
        resultsPan.setResizeWeight(0.6);

        chartResultPan.setLayout(new java.awt.BorderLayout());
        resultsPan.setLeftComponent(chartResultPan);

        jPanel14.setBackground(new java.awt.Color(153, 153, 153));
        jPanel14.setMinimumSize(new java.awt.Dimension(150, 10));
        jPanel14.setPreferredSize(new java.awt.Dimension(150, 10));
        jPanel14.setLayout(new java.awt.BorderLayout());

        resultList.setBackground(new java.awt.Color(255, 102, 102));
        resultList.setForeground(new java.awt.Color(218, 236, 255));
        resultList.setEvenRowColor(new java.awt.Color(173, 173, 173));
        resultList.setOddRowColor(new java.awt.Color(153, 153, 153));
        jPanel14.add(resultList, java.awt.BorderLayout.CENTER);

        exportToCSVButton.setBackground(new java.awt.Color(102, 102, 102));
        exportToCSVButton.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 0, 0, 3, new java.awt.Color(120, 120, 120)));
        exportToCSVButton.setLayout(new java.awt.GridBagLayout());

        jXHyperlink2.setForeground(new java.awt.Color(255, 255, 255));
        jXHyperlink2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/export-to-csv.png"))); // NOI18N
        jXHyperlink2.setText("Export To CSV");
        jXHyperlink2.setClickedColor(new java.awt.Color(255, 255, 255));
        jXHyperlink2.setFont(new java.awt.Font("Consolas", 0, 14));
        jXHyperlink2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jXHyperlink2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jXHyperlink2.setUnclickedColor(new java.awt.Color(255, 255, 255));
        jXHyperlink2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        exportToCSVButton.add(jXHyperlink2, gridBagConstraints);

        jPanel14.add(exportToCSVButton, java.awt.BorderLayout.SOUTH);

        resultsPan.setRightComponent(jPanel14);

        varscrolls.setBorder(null);
        varscrolls.setOpaque(false);
        varscrolls.setViewportView(vars);

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.CardLayout());

        jPanel13.setLayout(new java.awt.GridBagLayout());

        jPanel12.setBackground(new java.awt.Color(120, 120, 120));
        jPanel12.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 3));

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Simple Configuration Mode:  (");
        jPanel12.add(jLabel9);

        jXHyperlink3.setForeground(new java.awt.Color(212, 227, 255));
        jXHyperlink3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/arrow-switch.png"))); // NOI18N
        jXHyperlink3.setText("Switch to advanced mode");
        jXHyperlink3.setClickedColor(new java.awt.Color(212, 227, 255));
        jXHyperlink3.setUnclickedColor(new java.awt.Color(212, 227, 255));
        jXHyperlink3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink3ActionPerformed(evt);
            }
        });
        jPanel12.add(jXHyperlink3);

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText(")");
        jPanel12.add(jLabel10);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel13.add(jPanel12, gridBagConstraints);

        jPanel1.setBackground(new java.awt.Color(245, 245, 245));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(120, 120, 120), 3));
        jPanel1.setPreferredSize(new java.awt.Dimension(102, 180));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel2.setBackground(new java.awt.Color(245, 245, 245));
        jPanel2.setMinimumSize(new java.awt.Dimension(180, 10));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel6.setFont(new java.awt.Font("Consolas", 1, 12));
        jLabel6.setText("Select Test To Analayze");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel2.add(jLabel6, gridBagConstraints);

        testScroll.setBorder(null);
        testScroll.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel2.add(testScroll, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel2, gridBagConstraints);

        jPanel3.setMinimumSize(new java.awt.Dimension(38, 180));
        jPanel3.setLayout(new java.awt.BorderLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/lineWithNumber1.png"))); // NOI18N
        jPanel3.add(jLabel1, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel3, new java.awt.GridBagConstraints());

        jPanel4.setBackground(new java.awt.Color(232, 232, 232));
        jPanel4.setMinimumSize(new java.awt.Dimension(250, 180));
        jPanel4.setPreferredSize(new java.awt.Dimension(240, 30));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel7.setFont(new java.awt.Font("Consolas", 1, 12));
        jLabel7.setText("Select Statistic");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel4.add(jLabel7, gridBagConstraints);

        statScroll.setBorder(null);
        statScroll.setOpaque(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
        jPanel4.add(statScroll, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel4, gridBagConstraints);

        jPanel5.setMinimumSize(new java.awt.Dimension(38, 180));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/lineWithNumber2.png"))); // NOI18N
        jPanel5.add(jLabel2, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel5, new java.awt.GridBagConstraints());

        jPanel6.setBackground(new java.awt.Color(220, 220, 220));
        jPanel6.setMinimumSize(new java.awt.Dimension(194, 180));
        jPanel6.setLayout(new java.awt.GridBagLayout());

        jLabel8.setFont(new java.awt.Font("Consolas", 1, 12));
        jLabel8.setText("Configure Analyzer");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jLabel8, gridBagConstraints);

        varsDataPan.setNoDataText("No Configuration Needed");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel6.add(varsDataPan, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel6, gridBagConstraints);

        jPanel8.setMinimumSize(new java.awt.Dimension(38, 180));
        jPanel8.setLayout(new java.awt.BorderLayout());

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/lineWithNumber3.png"))); // NOI18N
        jPanel8.add(jLabel3, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel8, new java.awt.GridBagConstraints());

        jPanel7.setBackground(new java.awt.Color(210, 210, 210));
        jPanel7.setMinimumSize(new java.awt.Dimension(55, 180));
        jPanel7.setLayout(new java.awt.GridBagLayout());

        jXHyperlink1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/statistics-collection-view.png"))); // NOI18N
        jXHyperlink1.setText("Analyze");
        jXHyperlink1.setClickedColor(new java.awt.Color(0, 102, 204));
        jXHyperlink1.setFont(new java.awt.Font("Consolas", 1, 12));
        jXHyperlink1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jXHyperlink1.setUnclickedColor(new java.awt.Color(0, 102, 204));
        jXHyperlink1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jXHyperlink1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanel7.add(jXHyperlink1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jPanel7, gridBagConstraints);

        jPanel9.setLayout(new java.awt.BorderLayout());
        jPanel1.add(jPanel9, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel13.add(jPanel1, gridBagConstraints);

        jPanel10.setBackground(new java.awt.Color(245, 245, 245));
        jPanel10.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 3, 3, 3, new java.awt.Color(120, 120, 120)));
        jPanel10.setLayout(new java.awt.BorderLayout());

        jPanel11.setBackground(new java.awt.Color(120, 120, 120));
        jPanel11.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 3));

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Analyzing Results");
        jPanel11.add(jLabel4);

        jPanel10.add(jPanel11, java.awt.BorderLayout.PAGE_START);
        jPanel10.add(resultDataPan, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel13.add(jPanel10, gridBagConstraints);

        add(jPanel13, "SimpleMode");
        add(queryScr, "AdvancedMode");
    }// </editor-fold>//GEN-END:initComponents

    private void jXHyperlink1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink1ActionPerformed
        //TEST IF TEST IS READY:
        if (!DatabaseUnit.UNIT.isSignaled(selectedTest)) {
            MessageDialog.showFail("cannot produce statistics for this test", "the test has not been analyzed yet\n"
                    + "either it was not started yet or it is in the process of analyzing\n"
                    + "please try again later.");
            return;
        }

        //Assign Variables
        if (selectedCollector == null) {
            System.out.println("no statistic collector selected - TODO IN MSGBOX");
            return;
        }
        Map<String, Object> v = vars.getConfiguration();

        if (v == null) { //cannot produce configuration
            return;
        }

        VariableMetadata.assign(selectedCollector, v);
        chartResultPan.removeAll();

        final VisualModel vismodel = selectedCollector.analyze(DatabaseUnit.UNIT.getDatabase(), selectedTest);
        if (vismodel instanceof LineVisualModel) {
            showChart((LineVisualModel) vismodel);
        } else if (vismodel instanceof BarVisualModel) {
            showChart((BarVisualModel) vismodel);
        }
        resultDataPan.setData(resultsPan);
        GenericTableModel tableModel = new GenericTableModel(new DataExtractor("Algorithm", vismodel.getDomainAxisLabel(), vismodel.getRangeAxisLabel()) {

            @Override
            public Object getData(int idx, String dataName, Object from) {
                Object[] e = (Object[]) from;
//                Entry<Double, Double> e = (Entry<Double, Double>) from;
                if (dataName.equals("Algorithm")) {
                    return e[0].toString();
                } else if (vismodel.getDomainAxisLabel().equals(dataName)) {
                    return ""+e[1];
                } else {
                    return ""+e[2];
                }
            }
        });

        LinkedList<Object[]> all = new LinkedList<Object[]>();
        Entry vse;
        for (String a : vismodel.getAlgorithms()) {
            for (Object vs : vismodel.getValues(a).entrySet()) {
                vse = (Entry) vs;
                all.add(new Object[]{a, vse.getKey(), vse.getValue()});
            }
        }

        tableModel.setInnerList(all);
        resultList.setModel(tableModel);
        revalidate();
        repaint();
    }//GEN-LAST:event_jXHyperlink1ActionPerformed

    private void jXHyperlink2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink2ActionPerformed
        if (showedVisualization != null) {
            final File file = new File("temp.csv");
            showedVisualization.exportToCSV(file);
            SwingDSL.dopen(file);
        }
}//GEN-LAST:event_jXHyperlink2ActionPerformed

    private CardLayout getCards(){
        return (CardLayout) getLayout();
    }
    
    private void jXHyperlink3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink3ActionPerformed
        getCards().show(this, "AdvancedMode");
    }//GEN-LAST:event_jXHyperlink3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel chartResultPan;
    private javax.swing.JPanel exportToCSVButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink1;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink2;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink3;
    private bgu.dcr.az.dev.ui.StatisticsQueryScreen queryScr;
    private bc.ui.swing.useful.DataPanel resultDataPan;
    private bc.ui.swing.tables.ScrollableStripeTable resultList;
    private javax.swing.JSplitPane resultsPan;
    private javax.swing.JScrollPane statScroll;
    private javax.swing.JScrollPane testScroll;
    private bc.ui.swing.configurable.VariablesEditor vars;
    private bc.ui.swing.useful.DataPanel varsDataPan;
    private javax.swing.JScrollPane varscrolls;
    // End of variables declaration//GEN-END:variables

    private void showChart(LineVisualModel lineVisualModel) {
        LineChart chart = new LineChart();
        showedVisualization = lineVisualModel;
        chart.setModel(lineVisualModel);
        chartResultPan.add(chart, BorderLayout.CENTER);
    }

    private void showChart(BarVisualModel barVisualModel) {
        BarChart chart = new BarChart();
        showedVisualization = barVisualModel;
        chart.setModel(barVisualModel);
        chartResultPan.add(chart, BorderLayout.CENTER);
    }
}
