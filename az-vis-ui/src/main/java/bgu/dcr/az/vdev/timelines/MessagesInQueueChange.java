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
