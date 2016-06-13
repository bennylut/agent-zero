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
package bc.ui.swing.consoles;

import bc.dsl.SwingDSL;
import bgu.dcr.az.api.prob.ImmutableProblem;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.api.tools.Assignment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author bennyl
 */
public class ConstraintCalcConsole extends javax.swing.JPanel {

    List<ConstraintShowListener> listeners = new LinkedList<ConstraintShowListener>();
    private static final Pattern COMPATEBILITY_PATTERN_1 = Pattern.compile("(\\s*((\\d+)\\s*=\\s*(\\d+))\\s*,*)*\\s*((\\d+)\\s*=\\s*(\\d+))\\s*");
    private static final Pattern SPLITER = Pattern.compile(",*\\s*\\d+\\s*=\\s*\\d+");
    private static final Pattern SEPERATOR = Pattern.compile(",*\\s*(\\d+)\\s*=\\s*(\\d+)");
    private static final Pattern COMPATEBILITY_PATTERN_2 = Pattern.compile("\\s*(\\d+)\\s*vs\\.{0,1}\\s*(\\d+)\\s*");
    private ImmutableProblem p;

    /** Creates new form ConstraintCalcConsole */
    public ConstraintCalcConsole() {
        initComponents();
       
    }

    public void setInitialConsoleText(String text) {
        cons.setText(text);
    }

    public String getInitialConsoleText() {
        return cons.getText();
    }

    public static void main(String[] args) {
        SwingDSL.configureUI();
        SwingDSL.showInFrame(new ConstraintCalcConsole());
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

        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        cons = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        searchLabel1 = new javax.swing.JLabel();
        text = new javax.swing.JTextField();

        setLayout(new java.awt.BorderLayout());

        jPanel2.setBackground(new java.awt.Color(189, 188, 188));
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 0, 3, 3, new java.awt.Color(102, 102, 102)));
        jPanel2.setMinimumSize(new java.awt.Dimension(10, 200));
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 200));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setBorder(null);

        cons.setBackground(new java.awt.Color(0, 0, 0));
        cons.setColumns(20);
        cons.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        cons.setForeground(new java.awt.Color(153, 255, 153));
        cons.setRows(5);
        cons.setText("CONSTRAINTS QUERY CONSOLE\n\nuse \" i vs. j \" for watching the i vs. j constraint table (ex. \"1 vs. 2\")\nuse \" i=vi, j=vj, ...\" for watching the cost of the given assignment (ex. \"1=0,2=4,3=2\")\n\n");
        jScrollPane1.setViewportView(cons);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(jScrollPane1, gridBagConstraints);

        jPanel4.setBackground(new java.awt.Color(0, 0, 0));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        searchLabel1.setBackground(new java.awt.Color(226, 225, 225));
        searchLabel1.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        searchLabel1.setForeground(new java.awt.Color(102, 102, 102));
        searchLabel1.setText("?> ");
        searchLabel1.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanel4.add(searchLabel1, gridBagConstraints);

        text.setBackground(new java.awt.Color(226, 225, 225));
        text.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        text.setForeground(new java.awt.Color(102, 102, 102));
        text.setBorder(null);
        text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 6;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        jPanel4.add(text, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel2.add(jPanel4, gridBagConstraints);

        add(jPanel2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textActionPerformed
        String t = this.text.getText();
        if (COMPATEBILITY_PATTERN_1.matcher(t).matches()) {
            calcCost(t);
        } else if (COMPATEBILITY_PATTERN_2.matcher(t).matches()) {
            calcConstraint(t);
        } else {
            //TODO crying DIMA
        }
        this.text.setText("");
        this.cons.setCaretPosition(this.cons.getDocument().getLength());
}//GEN-LAST:event_textActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea cons;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel searchLabel1;
    private javax.swing.JTextField text;
    // End of variables declaration//GEN-END:variables

    private void calcCost(String t) {
        Matcher m = SPLITER.matcher(t);
        HashMap<Integer, Integer> values = new HashMap<Integer, Integer>();
        while (m.find()) {
            String g0 = m.group();
            Matcher mm = SEPERATOR.matcher(g0);
            mm.find();
            values.put(Integer.valueOf(mm.group(1)), Integer.valueOf(mm.group(2)));
        }
        Assignment ass = new Assignment();
        StringBuilder output = new StringBuilder("Calculating cost for: ");
        for (Integer i : values.keySet()) {
            final Integer value = values.get(i);
            if (!p.getDomainOf(i).contains(value)) {
                this.cons.append("There is no value " + value + " in the domain of agent " + i + "\n");
            }
            output.append("[").append(i).append("=").append(value).append("],");
            ass.assign(i, value);
        }
//        String substring = output.substring(0, output.length()-1);
        output.deleteCharAt(output.length() - 1);
        int cost = ass.calcCost(p);
        output.append("\nCost is: ").append(cost).append("\n");
        this.cons.append(output.toString());
    }

    private void calcConstraint(String t) {
        Matcher m = COMPATEBILITY_PATTERN_2.matcher(t);
        m.find();
        String g0 = m.group();
//        mm.find();
        Integer i = Integer.valueOf(m.group(1));
        Integer j = Integer.valueOf(m.group(2));
        if (p.isConstrained(i, j)) {
            for (ConstraintShowListener ls : this.listeners) {
                ls.onConstraintShowRequested(i, j);
            }
            String output = "Showing constraint table for Agent " + i + " and agent " + j + "\n";
            this.cons.append(output);
            this.cons.setCaretPosition(this.cons.getDocument().getLength());
        } else {
            String output = "There is no constraint table for Agent " + i + " and agent " + j + "\n";
            this.cons.append(output);
            this.cons.setCaretPosition(this.cons.getDocument().getLength());
        }
    }

    public void setProblem(ImmutableProblem p) {
        this.p = p;
    }

    public void addListener(ConstraintShowListener ls) {
        this.listeners.add(ls);
    }
    
    public void startTyping(){
        this.text.requestFocusInWindow();
    }

    public static interface ConstraintShowListener {

        boolean onConstraintShowRequested(int i, int j);
    }
}
