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
