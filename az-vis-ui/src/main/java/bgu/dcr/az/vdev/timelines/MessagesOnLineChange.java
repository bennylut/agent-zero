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
