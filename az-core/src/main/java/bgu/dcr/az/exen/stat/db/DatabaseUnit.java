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
package bgu.dcr.az.exen.stat.db;

import bc.utils.FileUtils;
import bgu.dcr.az.api.exp.ConnectionFaildException;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.api.exen.stat.DBRecord;
import bgu.dcr.az.api.exen.stat.Database;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.h2.tools.Csv;

/**
 *
 * @author bennyl
 */
public enum DatabaseUnit {

    UNIT;
    public static final int MAXIMUM_NUMBER_OF_INMEMORY_STATISTICS = 5000;
    public static String DATA_BASE_NAME = "agentzero";
    private DBConnectionHandler connection;
    private Thread writerThread = null;
    private ArrayBlockingQueue<SimpleEntry<DBRecord, Test>> dbQueue = new ArrayBlockingQueue<SimpleEntry<DBRecord, Test>>(MAXIMUM_NUMBER_OF_INMEMORY_STATISTICS);
    private Set<Class<? extends DBRecord>> knownRecords = new HashSet<Class<? extends DBRecord>>();
    private Map<Class<? extends DBRecord>, PreparedStatement> insertStatments = new HashMap<Class<? extends DBRecord>, PreparedStatement>();
    private Map<Signal, Signal> signals = new HashMap<Signal, Signal>();
    private List<SignalListner> signalListeners = new LinkedList<SignalListner>();
    private List<DataBaseChangedListener> databaseChangeListeners = new LinkedList<DataBaseChangedListener>();
    private boolean started = false;

    /**
     * will attempt to connect to the database (creating it if needed)
     *
     * @throws ConnectionFaildException
     */
    private void connect() throws ConnectionFaildException {
        try {
            String options = "LOG=0;CACHE_SIZE=65536;LOCK_MODE=0;UNDO_LOG=0";
            connection = new DBConnectionHandler("org.h2.Driver", "jdbc:h2:" + new File(DATA_BASE_NAME).getAbsolutePath() + ";" + options, "sa", "");
            connection.connect();
        } catch (SQLException ex) {
            throw new ConnectionFaildException("cannot connect to statistics database", ex);
        } catch (ClassNotFoundException ex) {
            throw new ConnectionFaildException("cannot connect to statistics database", ex);
        }
    }

    public void addSignalListener(SignalListner sl) {
        signalListeners.add(sl);
    }

    public void removeSignalListener(SignalListner sl) {
        signalListeners.remove(sl);
    }

    /**
     * will wait until all the submited statistics to the time of the call was
     * writen to the db
     */
    public synchronized void awaitStatistics() throws InterruptedException {
        final String sig = "AWAIT_STATISTICS";
        signal(sig);
        awaitSignal(sig);
    }

    /**
     * will return true if signal with that key exists in the system and the
     * requesting thread waited untill this signal received
     *
     * @param signal
     * @return
     * @throws InterruptedException
     */
    public synchronized boolean awaitSignal(Object signal) throws InterruptedException {
        final Signal fake = new Signal(signal);
        if (signals.containsKey(fake)) {
            Signal real = signals.get(fake);
            real.await();
            return true;
        } else {
            return false;
        }
    }

    public synchronized boolean isSignaled(Object signal) {
        Signal fake = new Signal(signal);
        return signals.containsKey(fake) && signals.get(fake).isSignaled();
    }

    public synchronized void signal(Object signal) {
        Signal s = new Signal(signal);
        signals.put(s, s);
        dbQueue.add(s);
    }

    public void disconnect() {
        try {
            awaitStatistics();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            Logger.getLogger(DatabaseUnit.class.getName()).log(Level.SEVERE, null, ex);
        }
        stopCollectorThread();
        knownRecords.clear();
        insertStatments.clear();
        dbQueue.clear();
        if (connection != null) {
            try {
                connection.disconnect();
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseUnit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * will attempt to delete the previous database
     */
    private void delete() {
        FileUtils.delete(new File(DATA_BASE_NAME + ".h2.db"));
        FileUtils.delete(new File(DATA_BASE_NAME + ".lock.db"));
        FileUtils.delete(new File(DATA_BASE_NAME + ".trace.db"));
    }

    public Database getDatabase() {
        return new H2Database();
    }

    public void insert(DBRecord record, Test test) throws SQLException {
        if (!knownRecords.contains(record.getClass())) {
            generateTable(record);
            insertStatments.put(record.getClass(), generatePreparedStatement(record));
            knownRecords.add(record.getClass());
        }
        PreparedStatement insertStatement = insertStatments.get(record.getClass());
        //AUTO FIELDS
        int i = 1;
        insertStatement.setObject(i++, record.getTestName());
        insertStatement.setObject(i++, record.getAlgorithmInstanceName());
        insertStatement.setObject(i++, record.getExecutionNumber());

        //DEFINED FIELDS
        for (Field f : record.getFields()) {
            try {
                insertStatement.setObject(i++, f.get(record));
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(DatabaseUnit.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(DatabaseUnit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        insertStatement.executeUpdate();
    }

    private void startBatch() {
        try {
            connection.startBatch();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseUnit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void dumpToCsv(File folder) {
        try {
            if (!folder.exists()) {
                folder.mkdirs();
            }

            Database db = getDatabase();
            String allTablesSQL = "SELECT TABLE_NAME  FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA = 'PUBLIC'";
            ResultSet res = db.query(allTablesSQL);

            while (res.next()) {
                String tableName = res.getString("TABLE_NAME");
                System.out.println("writing table " + tableName);
                ResultSet rs = db.query("select * from " + tableName);
                File ff = new File(folder.getAbsolutePath() + "/" + tableName + ".csv");
                ff.createNewFile();
                FileWriter fw = new FileWriter(ff);
                new Csv().write(fw, rs);
            }

        } catch (IOException ex) {
            Logger.getLogger(DatabaseUnit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseUnit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * this method designed to be used by the experiment - if there were a
     * limitation applied after the generation of several statistics then the
     * statistics that are belong to the problem that was limited need to be
     * cleaned - use this method to do so by providing all the problem numbers
     * that was limited this method will scan all the statistical data and
     * delete all the data that relevant to the given problem numbers
     *
     * @param problemNumbers
     */
    public void deleteAllStatisticsRelatedToProblems(List<Integer> problemNumbers, int numberOfAlgorithmsPerProblem) throws SQLException {
        if (problemNumbers.isEmpty()) {
            return;
        }

        Database db = getDatabase();
        String allTablesSQL = "SELECT TABLE_NAME  FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA = 'PUBLIC'";
        ResultSet res = db.query(allTablesSQL);
        StringBuilder deleteSQL = new StringBuilder();

        deleteSQL.append(" WHERE EXECUTION_NUMBER in (");
        for (Integer pn : problemNumbers) {
            final int pne = pn * numberOfAlgorithmsPerProblem;
            for (int i = 0; i < numberOfAlgorithmsPerProblem; i++) {
                deleteSQL.append(pne - i).append(",");
            }
        }
        deleteSQL.deleteCharAt(deleteSQL.length() - 1);
        deleteSQL.append(")");

        while (res.next()) {
            String tableName = res.getString("TABLE_NAME");
            connection.runUpdate("DELETE FROM " + tableName + deleteSQL.toString());
        }

        System.out.println("DELETE ALL STATISTICS" + deleteSQL);

    }

    public synchronized boolean isStarted() {
        return started;
    }

    public synchronized void start() throws ConnectionFaildException {
        if (started) {
            disconnect();
        } else {
            started = true;
        }

        DatabaseUnit.UNIT.delete();
        DatabaseUnit.UNIT.connect();
        DatabaseUnit.UNIT.startStatisticWriterThread();
    }

    public void endAndCommitBatch() throws SQLException {
        connection.endAndCommitBatch();
    }

    /**
     * will start the statistic writer thread
     */
    private void startStatisticWriterThread() {
        if (writerThread == null) {
            writerThread = new Thread(new StatisticWriter());
            writerThread.start();
        }
    }

    public void stopCollectorThread() {
        if (writerThread != null) {
            writerThread.interrupt();
            try {
                writerThread.join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                Logger.getLogger(DatabaseUnit.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        writerThread = null;
    }

    private PreparedStatement generatePreparedStatement(DBRecord record) throws SQLException {
        StringBuilder sb = new StringBuilder("INSERT INTO ").append(record.provideTableName());
        sb.append("(TEST,ALGORITHM_INSTANCE,EXECUTION_NUMBER");
        for (Field f : record.getFields()) {
            sb.append(", ").append(f.getName());
        }
        sb.append(") VALUES (?,?,?");
        for (Field f : record.getFields()) {
            sb.append(",?");
        }
        sb.append(");");

        return connection.prepare(sb.toString());
    }

    public PreparedStatement prepare(String statment) throws SQLException {
        return connection.prepare(statment);
    }

    public void insertLater(DBRecord record, Test test) {
        try {
            dbQueue.put(new SimpleEntry<DBRecord, Test>(record, test));
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private void generateTable(DBRecord record) throws SQLException {
        StringBuilder exe = new StringBuilder("CREATE TABLE ").append(record.provideTableName()).append(" (");
        exe.append("ID INTEGER NOT NULL AUTO_INCREMENT, TEST VARCHAR(50) NOT NULL, ALGORITHM_INSTANCE VARCHAR(50) NOT NULL, EXECUTION_NUMBER INTEGER NOT NULL ");
        for (Field f : record.getFields()) {
            exe.append(", ").append(f.getName());
            if (Boolean.class == f.getType() || boolean.class == f.getType()) {
                exe.append(" BOOLEAN");
            } else if (Double.class == f.getType() || double.class == f.getType()) {
                exe.append(" DECIMAL(20, 3)");
            } else if (Float.class == f.getType() || float.class == f.getType()) {
                exe.append(" DECIMAL(14, 3)");
            } else if (Integer.class == f.getType() || int.class == f.getType()) {
                exe.append(" INTEGER");
            } else if (Character.class == f.getType() || char.class == f.getType()) {
                exe.append(" CHAR");
            } else if (String.class == f.getType()) {
                exe.append(" VARCHAR(150)");
            } else if (Long.class == f.getType() || long.class == f.getType()) {
                exe.append(" BIGINT");
            }
        }
        exe.append(", PRIMARY KEY (ID));");
        connection.runUpdate(exe.toString());
        for (DataBaseChangedListener l : databaseChangeListeners) {
            l.onTableAdded(record.provideTableName());
        }
    }

    public class H2Database implements Database {

        @Override
        public ResultSet query(String query) throws SQLException {
            return connection.runQuery(query);
        }

        public DatabaseMetaData getMetadata() throws SQLException {
            return connection.conn.getMetaData();
        }

        public void addChangeListener(DataBaseChangedListener listener) {
            databaseChangeListeners.add(listener);
        }
    }

    private class Signal extends SimpleEntry<DBRecord, Test> {

        Object key;
        Semaphore lock;
        volatile boolean signaled;

        public Signal(Object key) {
            super(null, null);
            this.key = key;
            signaled = false;
            lock = new Semaphore(0);
        }

        public boolean isSignaled() {
            return signaled;
        }

        void signal() {
            signaled = true;
            lock.release(lock.getQueueLength() + 1 /*
                     * ADD ONE FOR THE POSIBILITY WHERE THERE ARE NO THREADS BUT
                     * 1 THAT JUST ENTERED
             */);
            for (SignalListner sl : signalListeners) {
                sl.onDataCollectedUpToSignal(UNIT, key);
            }
        }

        void await() throws InterruptedException {
            if (signaled) {
                return;
            }
            lock.acquire();
            lock.release();
        }

        @Override
        public boolean equals(Object o) {
            return o != null
                    && o instanceof Signal
                    && ((Signal) o).key != null
                    && ((Signal) o).key.equals(this.key);
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 67 * hash + (this.key != null ? this.key.hashCode() : 0);
            return hash;
        }
    }

    private class StatisticWriter implements Runnable {

        @Override
        public void run() {
            System.out.println("Statistics Collector Activated");
            SimpleEntry<DBRecord, Test> stat;
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    if (dbQueue.isEmpty() && Thread.currentThread().isInterrupted()) {
                        return;
                    }
                    List<SimpleEntry<DBRecord, Test>> multi = new LinkedList<SimpleEntry<DBRecord, Test>>();
                    if (dbQueue.isEmpty()) {
                        stat = dbQueue.take();
                        multi.add(stat);
                    } else {
                        dbQueue.drainTo(multi);
                    }

                    if (multi.size() > 1) {
                        UNIT.startBatch();
                    }

//                    long before = System.currentTimeMillis();
                    for (SimpleEntry<DBRecord, Test> m : multi) {
                        if (m instanceof Signal) {
                            ((Signal) m).signal();
                        } else {
                            try {
                                UNIT.insert(m.getKey(), m.getValue());
                            } catch (SQLException ex) {
                                Logger.getLogger(DatabaseUnit.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
//                    System.out.println("Writing Statistics took " + (System.currentTimeMillis() - before) + " milis");

                    if (multi.size() > 1) {
                        try {
                            UNIT.endAndCommitBatch();
                        } catch (SQLException ex) {
                            Logger.getLogger(DatabaseUnit.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static interface SignalListner {

        void onDataCollectedUpToSignal(DatabaseUnit source, Object signal);
    }

    public static interface DataBaseChangedListener {

        void onTableAdded(String name);
    }
}
