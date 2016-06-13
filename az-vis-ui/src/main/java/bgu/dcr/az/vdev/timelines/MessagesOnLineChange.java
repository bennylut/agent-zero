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
import bgu.dcr.az.api.exen.stat.NCSCToken;
import bgu.dcr.az.api.exen.vis.TimeLine;
import bgu.dcr.az.api.exen.vis.Frame;
import bgu.dcr.az.api.exen.vis.VisualizationFrameBuffer;
import bgu.dcr.az.vdev.timelines.MessagesOnLineChange.MessageData;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Administrator
 */
public class MessagesOnLineChange implements Change<List<MessageData>> {

    public static final String ID = MessagesOnLineChange.class.getSimpleName();
    private static AtomicLong nextMessageId = new AtomicLong(0);
    List<MessageData> messagesOnLine;

    public MessagesOnLineChange(Message m, int sentInFrame, int receiver) {
        messagesOnLine = new LinkedList<>();
        messagesOnLine.add(new MessageData(nextMessageId.incrementAndGet(), m.getName(), sentInFrame, NCSCToken.extract(m), m.getSender(), receiver));

    }

    @Override
    public void mergeWith(Change<List<MessageData>> change) {
        messagesOnLine.addAll(((MessagesOnLineChange) change).messagesOnLine);
    }

    @Override
    public List<MessageData> reduce(List<MessageData> prev, int frameNumber) {
        for (MessageData p : prev) {
            if (p.receivedInFrame.getValue() >= frameNumber) {
                messagesOnLine.add(p);
            }
        }

        return messagesOnLine;
    }

    public static List<MessageData> extract(Frame f) {
        return (List<MessageData>) f.getData(ID);
    }

    public static void install(Execution ex, final VisualizationFrameBuffer buffer) {
        buffer.installDelta(ID, new TimeLine(new LinkedList<>()));
        
        new Hooks.BeforeMessageSentHook() {
            @Override
            public void hook(int senderId, int recepientId, Message msg) {
                buffer.submitChange(MessagesOnLineChange.ID, senderId, new MessagesOnLineChange(msg, buffer.getRecordingFrameOf(senderId), recepientId));
                buffer.setMessageDelay(msg, recepientId, 20);
                buffer.stall(senderId, 5);
            }
        }.hookInto(ex);
    }

    public static class MessageData {

        public long messageId;
        public String name;
        public int sentInFrame;
        public NCSCToken receivedInFrame;
        public int sender;
        public int receiver;

        public MessageData(long messageId, String name, int sentInFrame, NCSCToken receivedInFrame, int sender, int receiver) {
            this.messageId = messageId;
            this.name = name;
            this.sentInFrame = sentInFrame;
            this.receivedInFrame = receivedInFrame;
            this.sender = sender;
            this.receiver = receiver;
        }
    }
}
