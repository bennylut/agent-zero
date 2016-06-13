package bgu.dcr.az.exen.stat.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnectionHandler {

    public Connection conn;
    private String username;
    private String password;
    private String driver;
    private String connectionUrl;
    private boolean transactionStarted;

    /**
     * Class constructor
     * @param fileName configuration file name
     * @throws FileNotFoundException if file not found
     * @throws IOException if IO error occurred
     * @throws ClassNotFoundException if JDBC driver not found
     */
    public DBConnectionHandler(String driver, String url, String uname, String password) throws ClassNotFoundException {
        this.username = uname;
        this.password = password;
        this.driver = driver;
        this.connectionUrl = url;
        Class.forName(this.driver);

        this.transactionStarted = false;
    }//end of constructor

    /**
     * Connect to DB. If already connected - does nothing
     * @throws ClassNotFoundException error loading JDBC driver
     * @throws SQLException SQL error occurred
     */
    public void connect() throws SQLException {
        if (this.conn == null) {
            this.conn = DriverManager.getConnection(this.connectionUrl, this.username, this.password);
        }
    }//end of connect

    /**
     * Runs an update. Good for UPDATE,DELETE,INSERT
     * @param query the query to perform
     * @return row count affected
     * @throws SQLException SQL error occurred
     */
    public int runUpdate(String query) throws SQLException {
        this.connect();
        Statement stmt = this.conn.createStatement();;
        int ans = stmt.executeUpdate(query);
        stmt.close();
        this.transactionStarted = true;
        return ans;
    }//end of runUpdate

    /**
     * Runs a SELECT query
     * @param query the query
     * @return ResultSet returned by query
     * @throws SQLException SQL error occurred
     */
    public ResultSet runQuery(String query) throws SQLException {
        this.connect();
        Statement stmt = this.conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        this.transactionStarted = true;
        return rs;
    }//end of runQuery

    /**
     * Commit a transaction
     * @throws SQLException SQL error occurred
     */
    public void commit() throws SQLException {
        this.connect();
        if (this.transactionStarted) {
            this.conn.commit();
            this.transactionStarted = false;
        }
    }//end of commit

    /**
     * Disconnects from the DB
     * @throws SQLException SQL error occurred
     */
    public void disconnect() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
            this.conn = null;
        }
    }//end of disconnect

    public PreparedStatement prepare(String statment) throws SQLException {
        return conn.prepareStatement(statment);
    }

    public void startBatch() throws SQLException {
        conn.setAutoCommit(false);
    }

    public void endAndCommitBatch() throws SQLException {
        conn.commit();
        conn.setAutoCommit(true);
    }

    public void rollBackBatch() throws SQLException {
        conn.rollback();
        conn.setAutoCommit(true);
    }
}
