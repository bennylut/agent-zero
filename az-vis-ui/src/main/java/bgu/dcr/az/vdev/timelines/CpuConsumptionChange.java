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

/**
 *
 * @author Administrator
 */
public class CpuConsumptionChange implements Change<int[][]> {

    public static final String ID = CpuConsumptionChange.class.getSimpleName();
    public static final int CPU_POWER = 100;
    int[][] change;

    public CpuConsumptionChange(int agent, int cpu, int total, int anum) {
        change = new int[2][anum];
        change[0][agent] = cpu;
        change[1][agent] = total;
//        System.out.println("submit cpu of " + cpu);
    }

    @Override
    public void mergeWith(Change<int[][]> change) {
        int[][] ochange = ((CpuConsumptionChange) change).change;
        for (int i = 0; i < this.change[0].length; i++) {
            this.change[0][i] += ochange[0][i];
            this.change[1][i] = Math.max(ochange[1][i], this.change[1][i]);
        }
    }

    @Override
    public int[][] reduce(int[][] prev, int frameNumber) {
        for (int i = 0; i < this.change[0].length; i++) {
            this.change[1][i] = Math.max(prev[1][i], this.change[1][i]);
        }
        return change;
    }

    public static int[][] extract(Frame f) {
        return (int[][]) f.getData(ID);
    }

    public static void install(final Execution ex, final VisualizationFrameBuffer buffer) {
        final int nagent = ex.getAgents().length;
        final long[] delta = new long[nagent];

        buffer.installDelta(ID, new TimeLine(new int[2][nagent]));

        new Hooks.BeforeMessageSentHook() {
            @Override
            public void hook(int senderId, int recepientId, Message msg) {
                buffer.setMessageDelay(msg, recepientId, 10);
            }
        }.hookInto(ex);

        new Hooks.BeforeMessageProcessingHook() {
            @Override
            public void hook(Agent a, Message msg) {
                delta[a.getId()] = a.getNumberOfConstraintChecks();
            }
        }.hookInto(ex);

        new Hooks.AfterMessageProcessingHook() {
            @Override
            public void hook(Agent a, Message msg) {
                checkChanges(a, buffer, delta, nagent);
            }
        }.hookInto(ex);

        if (ex.getSystemClock() != null) {
            new Hooks.TickHook() {
                @Override
                public void hook(SystemClock clock) {
                    for (Agent a : ex.getAgents()) {
                        checkChanges(a, buffer, delta, nagent);
                    }
                }
            }.hookInto(ex);
        }
    }

    private static void checkChanges(Agent a, final VisualizationFrameBuffer buffer, final long[] delta, int nagent) {
        long change = a.getNumberOfConstraintChecks() - delta[a.getId()];
        while (change > 0) {
            long cchange = (change > CPU_POWER ? CPU_POWER : change);
            buffer.submitChange(ID, a.getId(), new CpuConsumptionChange(a.getId(), (int) (cchange * CPU_POWER / 100), (int) (delta[a.getId()] + cchange), nagent));
            delta[a.getId()] += cchange;
            change -= cchange;
            buffer.stall(a.getId(), 1);
        }

        buffer.submitChange(ID, a.getId(), new CpuConsumptionChange(a.getId(), 0, (int) a.getNumberOfConstraintChecks(), nagent));
        buffer.stall(a.getId(), 1);
    }
}
