/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vdev.timelines;

import bgu.dcr.az.api.exen.vis.Change;
import bgu.dcr.az.api.exen.vis.TimeLine;
import bgu.dcr.az.api.exen.vis.Frame;
import bgu.dcr.az.api.exen.vis.VisualizationFrameBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class MessagesInQueueChange implements Change<int[]> {
    public static final String ID = MessagesInQueueChange.class.getSimpleName();
    int[] changes;

    @Override
    public String toString() {
        return Arrays.toString(changes);
    }

    public MessagesInQueueChange(int numberOfAgents, int agent, int change) {
        changes = new int[numberOfAgents];
        changes[agent] = change;
    }
    
    @Override
    public void mergeWith(Change<int[]> change) {
        for (int i=0; i<changes.length; i++){
            changes[i]+= ((MessagesInQueueChange) change).changes[i];
        }
    }

    @Override
    public int[] reduce(int[] prev, int frameNumber) {
        for (int i=0; i<changes.length; i++){
            changes[i] += prev[i];
        }
        
        return changes;
    }
    
    
    public static void store(VisualizationFrameBuffer buf, int numVars){
        buf.installDelta(ID, new TimeLine(new int[numVars]));
    }
    
    public static int[] extract(Frame f){
        return (int[]) f.getData(ID);
    }
    
}
