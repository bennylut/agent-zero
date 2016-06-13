/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.mdef;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.api.exen.stat.DBRecord;
import bgu.dcr.az.api.exen.stat.Database;
import bgu.dcr.az.api.exen.stat.VisualModel;

/**
 *
 * @author bennyl
 */
public interface StatisticCollector<T extends DBRecord>{
    
    VisualModel analyze(Database db, Test r);
    
    void hookIn(Agent[] a, final Execution ex); //TODO - REPLACE WITH EXECUTION VIEW
    
    void submit(T record);
    
    String getName();
}
