/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * StatisticsScreen.java
 *
 * Created on 24/11/2011, 16:25:21
 */
package bgu.dcr.az.dev.ui;

import bc.dsl.SwingDSL;
import bc.ui.swing.models.DataExtractor;
import bc.ui.swing.models.GenericTableModel;
import bc.ui.swing.models.GenericTreeModel;
import bc.ui.swing.models.GenericTreeModel.Node;
import bc.ui.swing.trees.IconProvider;
import bgu.dcr.az.exen.stat.db.DatabaseUnit;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.plaf.basic.BasicComboBoxUI.KeyHandler;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.tree.TreePath;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 *
 * @author bennyl
 */
public class StatisticsQueryScreen extends javax.swing.JPanel implements DatabaseUnit.DataBaseChangedListener {

    DatabaseUnit.H2Database database;
    private RSyntaxTextArea sqlTextArea;
    List<Listener> listeners = new LinkedList<Listener>();
    private LinkedList<Object[]> results;
    private String[] columns;

    /** Creates new form StatisticsScreen */
    public StatisticsQueryScreen() {
        initComponents();
        initializeEditor();
        initializeTree();
        final BasicSplitPaneDivider devider = (BasicSplitPaneDivider) split.getComponent(0);
        devider.setBorder(null);
        devider.setBackground(new Color(120, 120, 120));

    }

    public void addListener(Listener lis) {
        listeners.add(lis);
    }

    private void clearQuery() {
        sqlTextArea.setText("");
    }

    private void executeQuery() {
        query(sqlTextArea.getText());
    }

    private void initializeEditor() {
        sqlTextArea = new RSyntaxTextArea();
        setFont(sqlTextArea, new java.awt.Font("Consolas", 0, 14));
        sqlTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);


        SyntaxScheme scheme = sqlTextArea.getSyntaxScheme();
        sqlTextArea.setForeground(Color.white);
        scheme.getStyle(Token.RESERVED_WORD).foreground = new Color(226, 137, 100);
        scheme.getStyle(Token.OPERATOR).foreground = new Color(226, 137, 100);
        scheme.getStyle(Token.LITERAL_STRING_DOUBLE_QUOTE).foreground = new Color(101, 176, 66);
        scheme.getStyle(Token.LITERAL_CHAR).foreground = new Color(101, 176, 66);
        scheme.getStyle(Token.COMMENT_MULTILINE).foreground = new Color(153, 153, 153);
        scheme.getStyle(Token.COMMENT_KEYWORD).foreground = new Color(153, 153, 153);
        scheme.getStyle(Token.COMMENT_DOCUMENTATION).foreground = new Color(153, 153, 153);
        scheme.getStyle(Token.COMMENT_MARKUP).foreground = new Color(153, 153, 153);
        sqlTextArea.setCaretColor(new Color(120, 120, 120));
        scheme.getStyle(Token.LITERAL_NUMBER_FLOAT).foreground = new Color(218, 208, 133);
        scheme.getStyle(Token.LITERAL_NUMBER_DECIMAL_INT).foreground = new Color(51, 135, 204);


        RTextScrollPane sp = new RTextScrollPane(sqlTextArea, false, Color.DARK_GRAY);
        sp.setVerticalScrollBarPolicy(sp.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.setHorizontalScrollBarPolicy(sp.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setBorder(null);

        sqlTextArea.setCurrentLineHighlightColor(new Color(71, 71, 71));
        sqlTextArea.setBackground(new Color(51, 51, 51));
        sqlEditorPanel.add(sp, BorderLayout.CENTER);
        
        sqlTextArea.addKeyListener(new KeyAdapter() {
        
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()){
                    case KeyEvent.VK_F5:
                        executeQuery();
                        break;
                    case KeyEvent.VK_F9:
                        clearQuery();
                        break;
                }
            }
            
        });
    }

    /**
     * Set the font for all token types.
     *
     * @param textArea The text area to modify.
     * @param font The font to use.
     */
    public static void setFont(RSyntaxTextArea textArea, Font font) {
        if (font != null) {
            SyntaxScheme ss = textArea.getSyntaxScheme();
            ss = (SyntaxScheme) ss.clone();
            for (int i = 0; i < ss.getStyleCount(); i++) {
                if (ss.getStyle(i) != null) {
                    ss.getStyle(i).font = font;
                }
            }
            textArea.setSyntaxScheme(ss);
            textArea.setFont(font);
        }
    }

    public void setModel(DatabaseUnit.H2Database database) {
        this.database = database;
        database.addChangeListener(this);
        refreshTree();
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

        resultList = new bc.ui.swing.tables.ScrollableStripeTable();
        errorViewScroll = new javax.swing.JScrollPane();
        errorView = new javax.swing.JTextArea();
        split = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        sqlEditorPanel = new javax.swing.JPanel();
        toolbar = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        resultDataPan = new bc.ui.swing.useful.DataPanel();
        jPanel13 = new javax.swing.JPanel();
        tree = new bc.ui.swing.trees.ScrollableStripeTree();
        jPanel15 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        toolbar1 = new javax.swing.JPanel();
        jXHyperlink6 = new org.jdesktop.swingx.JXHyperlink();
        jSeparator4 = new javax.swing.JSeparator();
        jXHyperlink1 = new org.jdesktop.swingx.JXHyperlink();
        jSeparator1 = new javax.swing.JSeparator();
        jXHyperlink3 = new org.jdesktop.swingx.JXHyperlink();
        jSeparator2 = new javax.swing.JSeparator();
        jXHyperlink4 = new org.jdesktop.swingx.JXHyperlink();
        jSeparator3 = new javax.swing.JSeparator();
        jXHyperlink5 = new org.jdesktop.swingx.JXHyperlink();

        resultList.setBackground(new java.awt.Color(255, 102, 102));
        resultList.setForeground(new java.awt.Color(218, 236, 255));
        resultList.setEvenRowColor(new java.awt.Color(173, 173, 173));
        resultList.setOddRowColor(new java.awt.Color(153, 153, 153));

        errorView.setBackground(new java.awt.Color(255, 204, 204));
        errorView.setColumns(20);
        errorView.setEditable(false);
        errorView.setFont(new java.awt.Font("Consolas", 0, 14));
        errorView.setForeground(new java.awt.Color(51, 51, 51));
        errorView.setRows(5);
        errorView.setText("you have an error in your query: \n\n");
        errorViewScroll.setViewportView(errorView);

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.BorderLayout());

        split.setBackground(new java.awt.Color(120, 120, 120));
        split.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 0, 0, 0, new java.awt.Color(120, 120, 120)));

        jPanel2.setLayout(new java.awt.GridBagLayout());

        sqlEditorPanel.setBackground(new java.awt.Color(102, 102, 102));
        sqlEditorPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 3, 3, 3, new java.awt.Color(120, 120, 120)));
        sqlEditorPanel.setFont(new java.awt.Font("Consolas", 0, 14));
        sqlEditorPanel.setPreferredSize(new java.awt.Dimension(102, 180));
        sqlEditorPanel.setLayout(new java.awt.BorderLayout());

        toolbar.setBackground(new java.awt.Color(120, 120, 120));
        toolbar.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(120, 120, 120)));
        toolbar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 3));

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Query");
        toolbar.add(jLabel11);

        sqlEditorPanel.add(toolbar, java.awt.BorderLayout.PAGE_START);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(sqlEditorPanel, gridBagConstraints);

        jPanel10.setBackground(new java.awt.Color(245, 245, 245));
        jPanel10.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 3, 3, 3, new java.awt.Color(120, 120, 120)));
        jPanel10.setLayout(new java.awt.BorderLayout());

        jPanel11.setBackground(new java.awt.Color(120, 120, 120));
        jPanel11.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 3, 0, new java.awt.Color(120, 120, 120)));
        jPanel11.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 3));

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Query Results");
        jPanel11.add(jLabel4);

        jPanel10.add(jPanel11, java.awt.BorderLayout.PAGE_START);
        jPanel10.add(resultDataPan, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        jPanel2.add(jPanel10, gridBagConstraints);

        split.setRightComponent(jPanel2);

        jPanel13.setBackground(new java.awt.Color(120, 120, 120));
        jPanel13.setPreferredSize(new java.awt.Dimension(150, 100));
        jPanel13.setLayout(new java.awt.BorderLayout());

        tree.setBorder(javax.swing.BorderFactory.createMatteBorder(3, 3, 3, 0, new java.awt.Color(120, 120, 120)));
        tree.setMinimumSize(new java.awt.Dimension(200, 22));
        tree.setPreferredSize(new java.awt.Dimension(200, 440));
        jPanel13.add(tree, java.awt.BorderLayout.CENTER);

        jPanel15.setBackground(new java.awt.Color(120, 120, 120));
        jPanel15.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 3));

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Statistics Database Structure");
        jPanel15.add(jLabel10);

        jPanel13.add(jPanel15, java.awt.BorderLayout.PAGE_START);

        split.setLeftComponent(jPanel13);

        add(split, java.awt.BorderLayout.CENTER);

        toolbar1.setBackground(new java.awt.Color(120, 120, 120));
        toolbar1.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(153, 153, 153)), javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(71, 71, 71))));
        toolbar1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 4));

        jXHyperlink6.setForeground(new java.awt.Color(255, 255, 255));
        jXHyperlink6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/arrow-circle-135.png"))); // NOI18N
        jXHyperlink6.setText("Refresh Database Structure");
        jXHyperlink6.setClickedColor(new java.awt.Color(255, 255, 255));
        jXHyperlink6.setUnclickedColor(new java.awt.Color(255, 255, 255));
        jXHyperlink6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink6ActionPerformed(evt);
            }
        });
        toolbar1.add(jXHyperlink6);

        jSeparator4.setBackground(new java.awt.Color(153, 153, 153));
        jSeparator4.setForeground(new java.awt.Color(71, 71, 71));
        jSeparator4.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator4.setPreferredSize(new java.awt.Dimension(5, 16));
        toolbar1.add(jSeparator4);

        jXHyperlink1.setForeground(new java.awt.Color(255, 255, 255));
        jXHyperlink1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/delete-all.png"))); // NOI18N
        jXHyperlink1.setText("Clear Query (F9)");
        jXHyperlink1.setClickedColor(new java.awt.Color(255, 255, 255));
        jXHyperlink1.setUnclickedColor(new java.awt.Color(255, 255, 255));
        jXHyperlink1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink1ActionPerformed(evt);
            }
        });
        toolbar1.add(jXHyperlink1);

        jSeparator1.setBackground(new java.awt.Color(153, 153, 153));
        jSeparator1.setForeground(new java.awt.Color(71, 71, 71));
        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setPreferredSize(new java.awt.Dimension(5, 16));
        toolbar1.add(jSeparator1);

        jXHyperlink3.setForeground(new java.awt.Color(255, 255, 255));
        jXHyperlink3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/PLAY.png"))); // NOI18N
        jXHyperlink3.setText("Execute Query (F5)");
        jXHyperlink3.setClickedColor(new java.awt.Color(255, 255, 255));
        jXHyperlink3.setIconTextGap(1);
        jXHyperlink3.setUnclickedColor(new java.awt.Color(255, 255, 255));
        jXHyperlink3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink3ActionPerformed(evt);
            }
        });
        toolbar1.add(jXHyperlink3);

        jSeparator2.setBackground(new java.awt.Color(153, 153, 153));
        jSeparator2.setForeground(new java.awt.Color(71, 71, 71));
        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setPreferredSize(new java.awt.Dimension(5, 16));
        toolbar1.add(jSeparator2);

        jXHyperlink4.setForeground(new java.awt.Color(255, 255, 255));
        jXHyperlink4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/report--pencil.png"))); // NOI18N
        jXHyperlink4.setText("Export To CSV");
        jXHyperlink4.setClickedColor(new java.awt.Color(255, 255, 255));
        jXHyperlink4.setUnclickedColor(new java.awt.Color(255, 255, 255));
        jXHyperlink4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink4ActionPerformed(evt);
            }
        });
        toolbar1.add(jXHyperlink4);

        jSeparator3.setBackground(new java.awt.Color(153, 153, 153));
        jSeparator3.setForeground(new java.awt.Color(71, 71, 71));
        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator3.setPreferredSize(new java.awt.Dimension(5, 16));
        toolbar1.add(jSeparator3);

        jXHyperlink5.setForeground(new java.awt.Color(255, 255, 255));
        jXHyperlink5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/img/arrow-switch.png"))); // NOI18N
        jXHyperlink5.setText("Switch To Simple Mode");
        jXHyperlink5.setClickedColor(new java.awt.Color(255, 255, 255));
        jXHyperlink5.setUnclickedColor(new java.awt.Color(255, 255, 255));
        jXHyperlink5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXHyperlink5ActionPerformed(evt);
            }
        });
        toolbar1.add(jXHyperlink5);

        add(toolbar1, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void jXHyperlink6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink6ActionPerformed
        refreshTree();
    }//GEN-LAST:event_jXHyperlink6ActionPerformed

    private void jXHyperlink3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink3ActionPerformed
        executeQuery();
    }//GEN-LAST:event_jXHyperlink3ActionPerformed

    private void jXHyperlink5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink5ActionPerformed
        for (Listener l : listeners) {
            l.onSwitchClicked();
        }
    }//GEN-LAST:event_jXHyperlink5ActionPerformed

    private void jXHyperlink1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink1ActionPerformed
        clearQuery();
    }//GEN-LAST:event_jXHyperlink1ActionPerformed

    private void jXHyperlink4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXHyperlink4ActionPerformed
        if (results == null) {
            MessageDialog.showFail("cannot export to csv - no results.", "there are no results available to export");
            return;
        }
        
        try {
            File loc = SwingDSL.chooseSaveLocation();
            if (!loc.getName().endsWith(".csv")) loc = new File(loc.getAbsolutePath() + ".csv");
            if (loc != null) {
                PrintWriter pw = new PrintWriter(loc);
                writeCSVLine(pw, columns);
                for (Object[] r : results){
                    writeCSVLine(pw, r);
                }
                pw.close();
                SwingDSL.dopen(loc);
            }
        } catch (FileNotFoundException ex) {
            MessageDialog.showFail("cannot export to csv ", ex.getMessage());
        }
    }

    private void writeCSVLine(PrintWriter pw, Object[] data) {
        pw.write(data[0].toString());
        for (int i=1; i<data.length; i++){
            pw.write("," + data[i]);
        }
        pw.write("\r\n");
    }//GEN-LAST:event_jXHyperlink4ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea errorView;
    private javax.swing.JScrollPane errorViewScroll;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink1;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink3;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink4;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink5;
    private org.jdesktop.swingx.JXHyperlink jXHyperlink6;
    private bc.ui.swing.useful.DataPanel resultDataPan;
    private bc.ui.swing.tables.ScrollableStripeTable resultList;
    private javax.swing.JSplitPane split;
    private javax.swing.JPanel sqlEditorPanel;
    private javax.swing.JPanel toolbar;
    private javax.swing.JPanel toolbar1;
    private bc.ui.swing.trees.ScrollableStripeTree tree;
    // End of variables declaration//GEN-END:variables

    private void refreshTree() {
        try {
            tree.setModel(new GenericTreeModel(new DatabaseNode(database.getMetadata())));
        } catch (SQLException ex) {
            Logger.getLogger(StatisticsQueryScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void query(String q) {
        try {
            final ResultSet res = database.query(q);
            final ResultSetMetaData metaData = res.getMetaData();
            columns = new String[metaData.getColumnCount()];
            for (int i = 0; i < columns.length; i++) {
                columns[i] = metaData.getColumnLabel(i + 1);
            }
            results = new LinkedList<Object[]>();
            while (res.next()) {
                Object[] cur = new Object[columns.length];
                for (int i = 0; i < columns.length; i++) {
                    cur[i] = res.getString(i + 1);
                }
                results.add(cur);
            }

            GenericTableModel<Object[]> rmodel = new GenericTableModel<Object[]>(new DataExtractor<Object[]>(columns) {

                @Override
                public Object getData(int idx, String dataName, Object[] from) {
                    return from[idx];
                }
            });

            rmodel.setInnerList(results);
            resultList.setModel(rmodel);

            resultDataPan.setData(resultList);

        } catch (SQLException ex) {
            results = null;
            errorView.setText("you have an error in your query:\n" + ex.getMessage() + "\nplease correct your query and try again.");
            resultDataPan.setData(errorViewScroll);
        }
    }

    private void pushSql(TreePath selPath) {
        sqlTextArea.append(((SqlRepresenter) selPath.getLastPathComponent()).represent());
        tree.expandPath(selPath);
        sqlTextArea.requestFocus();
    }

    private void initializeTree() {
        tree.getInnerTree().addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                int selRow = tree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    if (e.getClickCount() == 2) {
                        pushSql(selPath);
                    }
                }
            }
        });

        tree.getInnerTree().addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    TreePath selPath = tree.getSelectionPath();
                    pushSql(selPath);
                }
            }
        });

        tree.setIconProvider(new IconProvider() {

            private Icon TABLE_ICON = SwingDSL.resIcon("table");
            private Icon FIELD_ICON = SwingDSL.resIcon("asterisk-yellow");
            private Icon DATABASE_ICON = SwingDSL.resIcon("database");

            @Override
            public Icon provideFor(Object item) {
                if (item instanceof DatabaseTableNode) {
                    return TABLE_ICON;
                } else if (item instanceof DatabaseFieldNode) {
                    return FIELD_ICON;
                }

                return DATABASE_ICON;
            }
        });
    }

    @Override
    public void onTableAdded(String name) {
        refreshTree();
    }

    private static interface SqlRepresenter {

        public String represent();
    }

    private class DatabaseNode extends GenericTreeModel.Node implements SqlRepresenter {

        List<DatabaseTableNode> children;

        public DatabaseNode(DatabaseMetaData metadata) throws SQLException {
            super("Statistics Database", null);
            children = new LinkedList<DatabaseTableNode>();
            String[] type = {"TABLE"};
            ResultSet tabs = metadata.getTables(null, null, "%", type);
            while (tabs.next()) {
                children.add(new DatabaseTableNode(metadata, tabs.getString("TABLE_NAME"), this));
            }
        }

        @Override
        public List getChildren() {
            return children;
        }

        @Override
        public String represent() {
            return "";
        }
    }

    private class DatabaseFieldNode extends GenericTreeModel.Node implements SqlRepresenter {

        String name;
        String type;

        public DatabaseFieldNode(String name, String type, Node parent) {
            super(name + ": " + type, parent);
            this.name = name;
            this.type = type;
        }

        @Override
        public List getChildren() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public String represent() {
            return name;
        }
    }

    private class DatabaseTableNode extends GenericTreeModel.Node implements SqlRepresenter {

        String name;
        List<DatabaseFieldNode> children;

        public DatabaseTableNode(DatabaseMetaData metadata, String tableName, Node parent) throws SQLException {
            super(tableName, parent);
            this.name = tableName;
            ResultSet cols = metadata.getColumns(null, null, tableName, null);
            children = new LinkedList<DatabaseFieldNode>();
            while (cols.next()) {
                String n = cols.getString("COLUMN_NAME");
                String t = cols.getString("TYPE_NAME");
                children.add(new DatabaseFieldNode(n, t, this));
            }

        }

        @Override
        public List getChildren() {
            return children;
        }

        @Override
        public String represent() {
            return name;
        }
    }

    public static interface Listener {

        void onSwitchClicked();
    }
}
