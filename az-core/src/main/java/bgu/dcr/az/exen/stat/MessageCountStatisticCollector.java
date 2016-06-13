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
import bgu.dcr.az.api.Hooks;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.api.exen.stat.DBRecord;
import bgu.dcr.az.api.exen.stat.Database;
import bgu.dcr.az.api.exen.stat.VisualModel;
import bgu.dcr.az.api.exen.stat.vmod.BarVisualModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
@Register(name = "msgc-sc")
public class MessageCountStatisticCollector extends AbstractStatisticCollector<MessageCountStatisticCollector.Record> {

    long[] counts;
    @Variable(name = "type", description = "type of the graph to show (BY_AGENT/BY_RUNVAR)", defaultValue = "BY_RUNVAR")
    Type graphType = Type.BY_RUNVAR;

    @Override
    public VisualModel analyze(Database db, Test r) {
        try {
            ResultSet res;
            BarVisualModel bv;
            switch (graphType) {
                case BY_AGENT:
                    bv = new BarVisualModel("Message Count", "Agent", "Avg(Message Sent)");
                    res = db.query(""
                            + "select ALGORITHM_INSTANCE, avg(messages) as m, agent "
                            + "from Message_count "
                            + "where test = '" + r.getName() + "' "
                            + "group by ALGORITHM_INSTANCE, agent "
                            + "order by agent");

                    bv.load("ALGORITHM_INSTANCE", "agent", "m", res);
                    return bv;

                case BY_RUNVAR:
                    String runVar = r.getRunningVarName();
                    bv = new BarVisualModel("Message Count", runVar, "Avg(Message Sent)");
                    res = db.query(""
                            + "select ALGORITHM_INSTANCE, avg(messages) as m, runvar "
                            + "from Message_count "
                            + "where test = '" + r.getName() + "' "
                            + "group by ALGORITHM_INSTANCE, runvar "
                            + "order by runvar");

                    bv.load("ALGORITHM_INSTANCE", "runvar", "m", res);
                    return bv;
            }
        } catch (SQLException ex) {
            Logger.getLogger(MessageCountStatisticCollector.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public long currentMessageCountOf(int agent){
        return counts[agent];
    }
    
    @Override
    public void hookIn(Agent[] a, final Execution ex) {
        counts = new long[a.length];

        new Hooks.BeforeMessageSentHook() {

            @Override
            public void hook(int sender, int recepiennt, Message msg) {
                counts[sender]++;
            }
        }.hookInto(ex);

        new Hooks.TerminationHook() {

            @Override
            public void hook() {
                for (int i = 0; i < counts.length; i++) {
                    submit(new Record(i, counts[i], test.getCurrentVarValue()));
                }
            }
        }.hookInto(ex);
    }

    @Override
    public String getName() {
        return "Message Count";
    }

    public static class Record extends DBRecord {

        double runVar;
        int agent;
        float messages;

        public Record(int agent, long messages, double runVar) {
            this.agent = agent;
            this.messages = messages;
            this.runVar = runVar;
        }

        @Override
        public String provideTableName() {
            return "Message_Count";
        }
    }

    public static enum Type {

        BY_AGENT,
        BY_RUNVAR
    }
}
