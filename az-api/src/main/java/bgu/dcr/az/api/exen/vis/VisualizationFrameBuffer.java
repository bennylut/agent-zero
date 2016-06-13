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
package bgu.dcr.az.api.exen.vis;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Hooks;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.exen.Execution;
import static bgu.dcr.az.api.Agt0DSL.*;
import bgu.dcr.az.api.exen.stat.NCSCToken;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class VisualizationFrameBuffer {

    private Execution ex;
    private int[] ncsc;
    private int[] linkTime;
    private Frame frame = new Frame(this);
    private int currentFrame = 0;
    private boolean frameAutoIncreaseEnabled = true;
    private Map<String, TimeLine> deltas = new HashMap<String, TimeLine>();

    public VisualizationFrameBuffer(Execution ex) {
        this.ex = ex;
        linkTime = new int[ex.getAgents().length];
        hookIn();
    }

    private void hookIn() {
        Agent[] agents = ex.getAgents();
        ncsc = new int[agents.length];

        new Hooks.BeforeMessageProcessingHook() {
            @Override
            public void hook(Agent a, Message msg) {
//                createMessageDeliverFrames(a, msg);
                int newNcsc = (int) NCSCToken.extract(msg).getValue();
//                System.out.println("Agent " + a.getId() + "Before Interval submit: " + ncsc[a.getId()]);

//                System.out.println("ncsc update for agent " + a.getId() + " from " + ncsc[a.getId()] + " to " + (Math.max(newNcsc, ncsc[a.getId()]) + 1) + " because msg with " + newNcsc);
                ncsc[a.getId()] = Math.max(newNcsc, ncsc[a.getId()]);
                ncsc[a.getId()]++;
//                System.out.println("Agent "  + a.getId() + " Interval Submit: " + ncsc[a.getId()]);
            }
        }.hookInto(ex);

        new Hooks.BeforeMessageSentHook() {
            @Override
            public void hook(int sender, int recepient, Message msg) {
//                msg.getMetadata().put("exit-ncsc", ncsc[sender]);
                NCSCToken.extract(msg).setValue(ncsc[sender]);
                //msg.getMetadata().put("ncsc", ncsc[sender]);
                //msgInQueueAndOnLine[recepient].incrementAndGet();
            }
        }.hookInto(ex);

    }

    //TODO: switch to semaphore
    public synchronized void setMessageDelay(Message message, int recepient, int addition) {
        final NCSCToken ncsct = NCSCToken.extract(message);
        //        NCSCToken.extract(message).increaseValue(addition);
        int ntime = (int) Math.max(linkTime[recepient] + addition / 3, ncsc[message.getSender()] + addition);
        if (linkTime[recepient] < ntime) {
            linkTime[recepient] = ntime;
        }
        ncsct.setValue(ntime);
        //        System.out.println("after message delayed " + NCSCToken.extract(message).getValue());
        //        Long current = (Long) message.getMetadata().get("ncsc");
        //        message.getMetadata().put("ncsc", current + addition);
    }

    public int getRecordingFrameOf(int agent) {
        return ncsc[agent];
    }

    public void installDelta(String name, TimeLine d) {
        deltas.put(name, d);
    }

    public void submitChange(String delta, int agent, Change change) {
        //System.out.println("submit change at " + ncsc[agent] + " for agent " + agent + " => " + delta + ": " + change);
        deltas.get(delta).add(ncsc[agent], change);
    }

    public void submitChangeInFrame(String delta, int frame, Change change) {
//        System.out.println("submit change in " + ncsc[agent].get() + " in agent " + agent + " => " + delta);
        deltas.get(delta).add(frame, change);
    }

    public void stall(int agent, int frames) {
        ncsc[agent] += frames;
    }

    public Frame nextFrame() {
        if (currentFrame < numberOfFrames()) {
            frame.setFrameNumber(currentFrame);
            if (frameAutoIncreaseEnabled) {
                currentFrame++;
            }
            return frame;
        }

        return null;
    }

    public int numberOfFrames() {
        return ncsc[0];
    }

    public void gotoFrame(int frame) {
        currentFrame = min(frame, numberOfFrames());
    }

    public long getCurrentFrame() {
        return currentFrame;
    }

    public boolean isFrameAutoIncreaseEnabled() {
        return frameAutoIncreaseEnabled;
    }

    public void setFrameAutoIncreaseEnabled(boolean enable) {
        this.frameAutoIncreaseEnabled = enable;
    }

    public static void main(String[] args) {
        ArrayList<Interval<String>> ret = new ArrayList<Interval<String>>();
        ret.add(new Interval<String>(0, 12, "/0/12"));
        ret.add(new Interval<String>(24, 24, "/0/12"));
        ret.add(new Interval<String>(28, 36, "/0/12"));

        int pos = Collections.binarySearch(ret, 7L);
        System.out.println("pos 7 => " + pos);

        pos = Collections.binarySearch(ret, 24L);
        System.out.println("pos 24 => " + pos);

        pos = Collections.binarySearch(ret, 25L);
        System.out.println("pos 25 => " + pos);

        pos = Collections.binarySearch(ret, 28L);
        System.out.println("pos 28 => " + pos);

    }

    Object getData(int number, String delta) {
        return deltas.get(delta).get(number);
    }

    public TimeLine getTimeLine(String name) {
        return deltas.get(name);
    }

    public void prepereTimeLines() {
        int max = max(ncsc) + 1;
        for (int i = 0; i < ncsc.length; i++) {
            ncsc[i] = max;
        }

        for (TimeLine d : deltas.values()) {
            d.streach((int) max);
        }
    }

    private static class Interval<T> implements Comparable<Long> {

        public long start;
        public long end;
        public T data;

        public Interval(long start, long end, T data) {
            this.start = start;
            this.end = end;
            this.data = data;
        }

        @Override
        public int compareTo(Long t) {
            if (t < start) {
                return 1;
            }
            if (t > end) {
                return -1;
            }
            return 0;
        }

        @Override
        public String toString() {
            return "[" + data + ": " + start + "=>" + end + "]";
        }
    }
}
