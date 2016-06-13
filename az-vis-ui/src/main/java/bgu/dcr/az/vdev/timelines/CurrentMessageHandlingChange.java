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

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Hooks;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.vis.Change;
import bgu.dcr.az.api.exen.vis.TimeLine;
import bgu.dcr.az.api.exen.vis.Frame;
import bgu.dcr.az.api.exen.vis.VisualizationFrameBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Administrator
 */
public class CurrentMessageHandlingChange implements Change<Map<Integer, String>>{
    
    public static String ID = CurrentMessageHandlingChange.class.getSimpleName();
    public static String NO_HANDLE = "";
    
    private HashMap<Integer, String> handling = new HashMap<>();

    public CurrentMessageHandlingChange(int agent, String message) {
        handling.put(agent, message);
    }
    
    @Override
    public void mergeWith(Change<Map<Integer, String>> change) {
        for (Entry<Integer, String> c : ((CurrentMessageHandlingChange) change).handling.entrySet()){
            handling.put(c.getKey(), c.getValue());
        }
    }

    @Override
    public Map<Integer, String> reduce(Map<Integer, String> prev, int frameNumber) {
        HashMap<Integer, String> pprev = new HashMap<>(prev);
        for (Entry<Integer, String> c : handling.entrySet()){
            pprev.put(c.getKey(), c.getValue());
        }
        
        return pprev;
    }
    
    public static Map<Integer,String> extract(Frame f){
        return (Map<Integer,String>) f.getData(ID);
    }
    
    public static void install(Execution ex, final VisualizationFrameBuffer buffer, final int handlingStall){
        final HashMap<Integer, String> initial = new HashMap<>();
        for (int i=0; i<ex.getAgents().length; i++){
            initial.put(i, NO_HANDLE);
        }
        
        buffer.installDelta(ID, new TimeLine(initial));
        new Hooks.BeforeMessageProcessingHook() {

            @Override
            public void hook(Agent a, Message msg) {
                buffer.submitChange(ID, a.getId(), new CurrentMessageHandlingChange(a.getId(), msg.getName()));
                buffer.stall(a.getId(), handlingStall);
            }
        }.hookInto(ex);
        
        new Hooks.AfterMessageProcessingHook() {

            @Override
            public void hook(Agent a, Message msg) {
                buffer.submitChange(ID, a.getId(), new CurrentMessageHandlingChange(a.getId(), NO_HANDLE));
            }
        }.hookInto(ex);
    }
    
}
