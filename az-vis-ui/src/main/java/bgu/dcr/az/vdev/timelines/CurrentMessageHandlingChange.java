/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
