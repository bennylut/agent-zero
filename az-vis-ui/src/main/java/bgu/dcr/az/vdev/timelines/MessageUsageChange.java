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
public class MessageUsageChange implements Change<Map<String, Long>> {

    public static final String ID = MessageUsageChange.class.getName();
    Map<String, Long> musage;
    String singleChangeData;

    public MessageUsageChange(String messageName) {
        this.singleChangeData = messageName;
    }

    @Override
    public void mergeWith(Change<Map<String, Long>> change) {
        toMultiChange();
        final String schange = ((MessageUsageChange) change).singleChangeData;
        Long val = musage.get(schange);
        if (val == null) {
            musage.put(schange, 1L);
        } else {
            musage.put(schange, val + 1);
        }
    }

    @Override
    public Map<String, Long> reduce(Map<String, Long> prev, int frameNumber) {
        toMultiChange();

        for (Entry<String, Long> d : prev.entrySet()) {
            Long val = musage.get(d.getKey());
            if (val == null) {
                musage.put(d.getKey(), d.getValue());
            } else {
                musage.put(d.getKey(), val + d.getValue());
            }
        }
        
        return musage;
    }

    public static Map<String, Long> extract(Frame f) {
        return (Map<String, Long>) f.getData(ID);
    }

    public static void install(Execution ex, final VisualizationFrameBuffer buffer) {
        final HashMap<String, Long> initial = new HashMap<>();
        for (String msg : Agent.PlatformOperationsExtractor.extract(ex.getAgents()[0]).algorithmMessages()){
            initial.put(msg, 0L);
        }
        buffer.installDelta(ID, new TimeLine(initial));
        new Hooks.BeforeMessageSentHook() {

            @Override
            public void hook(int senderId, int recepientId, Message msg) {
                buffer.submitChange(ID, senderId, new MessageUsageChange(msg.getName()));
            }
        }.hookInto(ex);
    }

    private void toMultiChange() {
        if (musage == null) {
            musage = new HashMap<>();
            musage.put(singleChangeData, 1L);
        }
    }
}
