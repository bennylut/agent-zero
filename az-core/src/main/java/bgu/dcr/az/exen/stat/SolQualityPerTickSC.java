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

import bgu.dcr.az.api.tools.Assignment;
import java.sql.ResultSet;
import java.sql.SQLException;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Hooks;
import bgu.dcr.az.api.exen.SystemClock;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.api.exen.stat.DBRecord;
import bgu.dcr.az.api.exen.stat.Database;
import bgu.dcr.az.api.exen.stat.VisualModel;
import bgu.dcr.az.api.exen.stat.vmod.LineVisualModel;

/**
 * @author alongrub
 *
 */
@Register(name = "sqpt-sc")
public class SolQualityPerTickSC extends AbstractStatisticCollector<SolQualityPerTickSC.Record> {

    @Variable(name = "sample-rate", description = "The sampling rate for solution quality", defaultValue = "1")
    private int samplingRate = 1;
    
    private double lastCost = -1;
    private int ticksPerCycle = 1;

    public static class Record extends DBRecord {

        public final float solQuality;
        public final long tickNum;
        public long cycles;
        public int execution;
        public float prevSolutionQuality;

        public Record(float solQuality, long tickNum, int tpc, int execution, float prevSolutionQuality) {
            super();
            this.solQuality = solQuality;
            this.prevSolutionQuality = prevSolutionQuality;
            this.tickNum = tickNum;
            this.cycles = tickNum / tpc;
            this.execution = execution;
        }

        @Override
        public String provideTableName() {
            return "Solution_Quality";
        }
    }

    @Override
    public VisualModel analyze(Database db, Test r) {
        LineVisualModel lvm = new LineVisualModel("Time", "Solution Quality", "Solution Quality Progress");
        try {
            ResultSet res = db.query("SELECT AVG (solQuality) AS s, tickNum, ALGORITHM_INSTANCE FROM Solution_Quality where TEST = '" + r.getName() + "' GROUP BY ALGORITHM_INSTANCE, tickNum ORDER BY tickNum");
            while (res.next()) {
                lvm.setPoint(res.getString("ALGORITHM_INSTANCE"), res.getLong("tickNum"), res.getDouble("s"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lvm;
    }

    @Override
    public void hookIn(final Agent[] a, final Execution e) {

        lastCost = -1;

        new Hooks.TickHook() {

            @Override
            public void hook(SystemClock clock) {
                if (clock.time() % samplingRate == 0) {
                    final Assignment assignment = e.getResult().getAssignment();

                    float cost = 0;
                    if (assignment != null) {
                        cost = (float) assignment.calcCost(e.getGlobalProblem());
                    }
                    //System.out.println("in tick " + clock.time() + " the cost was " + cost);
                    submit(new Record(cost, clock.time(), ticksPerCycle, e.getTest().getCurrentExecutionNumber(), (float) lastCost));
                    lastCost = cost;
                }
            }
        }.hookInto(e);

        new Hooks.ReportHook("ticksPerCycle") {

            @Override
            public void hook(Agent a, Object[] args) {
                ticksPerCycle = (Integer) args[0];
            }
        }.hookInto(e);
    }

    @Override
    public String getName() {
        return "Solution Quality Per Ticks";
    }
}
