/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.stat;

import bgu.dcr.az.api.exen.stat.NCSCToken;
import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Hooks;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.api.exen.stat.DBRecord;
import bgu.dcr.az.api.exen.stat.Database;
import bgu.dcr.az.api.exen.stat.VisualModel;
import bgu.dcr.az.api.exen.stat.vmod.LineVisualModel;
import bgu.dcr.az.exen.stat.NCSCStatisticCollector.NCSCRecord;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
@Register(name = "ncsc-sc")
public class NCSCStatisticCollector extends AbstractStatisticCollector<NCSCRecord> {

    long[] ncsc;

    @Override
    public VisualModel analyze(Database db, Test r) {
        String query = "select AVG(ncsc) as avg, rVar, ALGORITHM_INSTANCE from NCSC where TEST = '" + r.getName() + "' group by ALGORITHM_INSTANCE, rVar order by rVar";
        LineVisualModel line = new LineVisualModel(r.getRunningVarName(), "Avg(NCSC)", "NCSC");
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

    public long currentNcscOf(int agent) {
        return ncsc[agent];
    }

    @Override
    public void hookIn(final Agent[] agents, Execution ex) {
        System.out.println("NCSC Statistic Collector registered");

        ncsc = new long[agents.length];
        final double rvar = ex.getTest().getCurrentVarValue();

        new Hooks.BeforeMessageProcessingHook() {
            @Override
            public void hook(Agent a, Message msg) {
                long newNcsc = NCSCToken.extract(msg).getValue();//(Long) msg.getMetadata().get("ncsc");
                ncsc[a.getId()] = max(newNcsc, ncsc[a.getId()]);
                ncsc[a.getId()]++;
            }
        }.hookInto(ex);

        new Hooks.BeforeMessageSentHook() {
            @Override
            public void hook(int sender, int recepient, Message msg) {
                if (sender >= 0) {
                    NCSCToken.extract(msg).setValue(ncsc[sender]);
                }
            }
        }.hookInto(ex);

        new Hooks.TerminationHook() {
            @Override
            public void hook() {
                submit(new NCSCRecord(max(ncsc), rvar));
            }
        }.hookInto(ex);

    }

    @Override
    public String getName() {
        return "Number Of Concurrent Steps Of Computation";
    }

    public static class NCSCRecord extends DBRecord {

        float ncsc;
        double rVar;

        public NCSCRecord(float ncsc, double rVar) {
            this.ncsc = ncsc;
            this.rVar = rVar;
        }

        @Override
        public String provideTableName() {
            return "NCSC";
        }
    }
}
