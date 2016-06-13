/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.stat;

import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.api.exen.stat.DBRecord;
import bgu.dcr.az.api.exen.mdef.StatisticCollector;
import bgu.dcr.az.exen.stat.db.DatabaseUnit;

/**
 *
 * @author bennyl
 */
public abstract class AbstractStatisticCollector<T extends DBRecord> extends Agt0DSL implements StatisticCollector<T> {

    Test test;

    public void setTest(Test test) {
        this.test = test;
    }


    @Override
    public void submit(T record) {
        String ains = test.getCurrentExecutedAlgorithmInstanceName();
        record.setAlgorithmInstanceName(ains);
        record.setTestName(test.getName());
        record.setExecutionNumber(test.getCurrentExecutionNumber());
        DatabaseUnit.UNIT.insertLater(record, test);
    }
}
