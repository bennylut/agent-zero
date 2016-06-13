/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.stat;

import java.lang.reflect.Field;

/**
 *
 * @author bennyl
 */
public abstract class DBRecord {
    public abstract String provideTableName();
    private String algorithmInstanceName;
    private String testName;
    private int executionNumber;
    
    private Field[] fields;
    public DBRecord() {
        fields = getClass().getDeclaredFields();
        for (Field f : fields){
            f.setAccessible(true);
        }
    }

    public Field[] getFields() {
        return fields;
    }

    /**
     * do not use directly - only get filled upon submition 
     * @return 
     */
    public String getAlgorithmInstanceName() {
        return algorithmInstanceName;
    }
    
    /**
     * do not use directly - only get filled upon submition
     * @param AlgorithmInstanceName 
     */
    public void setAlgorithmInstanceName(String AlgorithmInstanceName) {
        this.algorithmInstanceName = AlgorithmInstanceName;
    }

    /**
     * do not use directly - only get filled upon submition
     * @return 
     */
    public String getTestName() {
        return testName;
    }

    /**
     * do not use directly - only get filled upon submition
     * @param roundName 
     */
    public void setTestName(String roundName) {
        this.testName = roundName;
    }

    /**
     * do not use directly - only get filled upon submition
     * @param executionNumber 
     */
    public void setExecutionNumber(int executionNumber) {
        this.executionNumber = executionNumber;
    }

    /**
     * do not use directly - only get filled upon submition
     * @return 
     */
    public int getExecutionNumber() {
        return executionNumber;
    }
    
}
