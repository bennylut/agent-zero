/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.stat;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Hooks.TerminationHook;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.api.exen.stat.DBRecord;
import bgu.dcr.az.api.exen.stat.Database;
import bgu.dcr.az.api.exen.stat.VisualModel;
import bgu.dcr.az.api.exen.stat.vmod.LineVisualModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
@Register(name = "cc-sc")
public class CCStatisticCollector extends AbstractStatisticCollector<CCStatisticCollector.CCRecord> {

    @Override
    public VisualModel analyze(Database db, Test r) {
        String query = "select AVG(cc) as avg, rVar, ALGORITHM_INSTANCE from CC where TEST = '" + r.getName() + "' group by ALGORITHM_INSTANCE, rVar order by rVar";
        LineVisualModel line = new LineVisualModel(r.getRunningVarName(), "Avg(CC)", "CC");
        try {
            ResultSet rs = db.query(query);
            while (rs.next()) {
                line.setPoint(rs.getString("ALGORITHM_INSTANCE"), rs.getFloat("rVar"), rs.getFloat("avg"));
            }
            return line;
        } catch (SQLException ex) {
            Logger.getLogger(NCCCStatisticCollector.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public void hookIn(final Agent[] agents, final Execution ex) {

        new TerminationHook() {

            @Override
            public void hook() {
                long sum = 0;
                for (Agent ag : agents) {
                    sum += ag.getNumberOfConstraintChecks();
                }

                submit(new CCRecord(ex.getTest().getCurrentVarValue(), sum, agents[0].getAlgorithmName()));
            }
        }.hookInto(ex);
    }

    @Override
    public String getName() {
        return "Constraint Checks";
    }

    public static class CCRecord extends DBRecord {

        double rVar;
        double cc;
        String algorithm;

        public CCRecord(double rVal, double cc, String algorithm) {
            this.rVar = rVal;
            this.cc = cc;
            this.algorithm = algorithm;
        }

        @Override
        public String provideTableName() {
            return "CC";
        }
    }
}
