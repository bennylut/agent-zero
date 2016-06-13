/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vdev.timelines;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Hooks;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.SystemClock;
import bgu.dcr.az.api.exen.vis.Change;
import bgu.dcr.az.api.exen.vis.Frame;
import bgu.dcr.az.api.exen.vis.TimeLine;
import bgu.dcr.az.api.exen.vis.VisualizationFrameBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Administrator
 */
public class TotalCcChange implements Change<Map<String, int[]>> {

    private static final String ID = TotalCcChange.class.getSimpleName();
    HashMap<String, int[]> total;

    public TotalCcChange(String msg, int agent, int total, int nagent) {
        this.total = new HashMap<>(1);
        final int[] v = new int[nagent];
        v[agent] = total;
        this.total.put(msg, v);
    }

    @Override
    public void mergeWith(Change<Map<String, int[]>> change) {
        Map<String, int[]> ctotal = ((TotalCcChange) change).total;
        for (Entry<String, int[]> e : ctotal.entrySet()) {
            if (total.containsKey(e.getKey())) {
                int[] etotal = e.getValue();
                int[] mytotal = total.get(e.getKey());
                for (int i = 0; i < etotal.length; i++) {
                    mytotal[i] = etotal[i] + mytotal[i];
                }
            } else {
                total.put(e.getKey(), e.getValue());
            }
        }
    }

    @Override
    public Map<String, int[]> reduce(Map<String, int[]> prev, int frameNumber) {
        Map<String, int[]> ctotal = prev;
        for (Entry<String, int[]> e : ctotal.entrySet()) {
            if (total.containsKey(e.getKey())) {
                int[] etotal = e.getValue();
                int[] mytotal = total.get(e.getKey());
                for (int i = 0; i < etotal.length; i++) {
                    mytotal[i] = etotal[i] + mytotal[i];
                }
            } else {
                total.put(e.getKey(), e.getValue());
            }
        }
        return total;
    }

    public static Map<String, int[]> extract(Frame f) {
        return (Map<String, int[]>) f.getData(ID);
    }

    public static void install(final Execution ex, final VisualizationFrameBuffer buffer) {
        final int nagent = ex.getAgents().length;
        final int[] delta = new int[nagent];

        buffer.installDelta(ID, new TimeLine(new HashMap<String, int[]>(0)));

        new Hooks.BeforeMessageProcessingHook() {
            @Override
            public void hook(Agent a, Message msg) {
                delta[a.getId()] = (int) a.getNumberOfConstraintChecks();
            }
        }.hookInto(ex);

        new Hooks.AfterMessageProcessingHook() {
            @Override
            public void hook(Agent a, Message msg) {
                if (msg != null) {
                    buffer.submitChange(ID, a.getId(), new TotalCcChange(msg.getName(), a.getId(), (int) a.getNumberOfConstraintChecks() - delta[a.getId()], nagent));
                    buffer.stall(a.getId(), 3);
                }

            }
        }.hookInto(ex);

        if (ex.getSystemClock() != null) {
            new Hooks.TickHook() {
                @Override
                public void hook(SystemClock clock) {
                    for (Agent a : ex.getAgents()) {
                        buffer.submitChange(ID, a.getId(), new TotalCcChange("__SYNC__", a.getId(), (int) a.getNumberOfConstraintChecks() - delta[a.getId()], nagent));
                        buffer.stall(a.getId(), 3);
                    }
                }
            }.hookInto(ex);
        }
    }
}
