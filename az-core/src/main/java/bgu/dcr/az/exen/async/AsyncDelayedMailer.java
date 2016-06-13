/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.async;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Hooks;
import bgu.dcr.az.api.Hooks.BeforeMessageSentHook;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.Mailer;
import bgu.dcr.az.api.exen.MessageQueue;
import bgu.dcr.az.api.exen.mdef.MessageDelayer;
import bgu.dcr.az.api.exp.UnRegisteredAgentException;
import bgu.dcr.az.api.tools.IdleDetector;
import bgu.dcr.az.exen.AbstractMailer;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author bennyl
 */
public class AsyncDelayedMailer extends AbstractMailer implements IdleDetector.Listener {

    MessageDelayer dman;
    AtomicLong time;
    IdleDetector timeForwardDetector;
    String[] agentActiveGroups;

    @SuppressWarnings("LeakingThisInConstructor")
    public AsyncDelayedMailer(MessageDelayer dman, int numberOfAgents) {
        this.dman = dman;
        this.time = new AtomicLong(dman.getInitialTime());
        this.agentActiveGroups = new String[numberOfAgents];
        this.timeForwardDetector = new IdleDetector(numberOfAgents, new DetectionMailer(), "TFD");
        this.timeForwardDetector.addListener(this);
    }

    /**
     * tells the mailer what group the agent is currently on - used for solving
     * the nested agents + message delays correlation where agents in different
     * levels of the nest has their messages delayed and because they are not on
     * the same level no empty boxes are detected - this method will update the
     * mail box group key that a given agent is looking on so that we will be
     * able to check the right message box.
     *
     * @param agent
     * @param activeGroup
     */
    public void updateAgentActiveGroup(int agent, String activeGroup) {
        if (agentActiveGroups[agent] != null && activeGroup != agentActiveGroups[agent]) { //agent was in leaving state, now is back...
            timeForwardDetector.notifyAgentWorking();
        }
        agentActiveGroups[agent] = activeGroup;
    }

    @Override
    protected MessageQueue generateNewMessageQueue(int agent, String groupKey) {
        return new DelayedMessageQueue(dman, this, timeForwardDetector, groupKey, agent);
    }

    @Override
    public void send(Message msg, int to, String groupKey) {
        for (Hooks.BeforeMessageSentHook h : beforeMessageSentHooks) {
            h.hook(msg.getSender(), to, msg);
        }

        msg = msg.copy(); //deep copying the message.
        long mtime = dman.extractTime(msg); //time before delay
        dman.addDelay(msg, msg.getSender(), to); //adding delay
        MessageQueue[] qus = takeQueues(groupKey); //all the queues from the current group

        ((DelayedMessageQueue) qus[to]).tryAdd(msg, time.get());//try to add the message to the queue, if the time after the delay < time

        if (mtime > time.get()) { //if the time before the delay is bigger then the current time
            long ctime; //try to update the current time
            while (true) {
                ctime = time.get();
                if (ctime >= mtime) {
                    return;
                }

                if (time.compareAndSet(ctime, mtime)) {
                    break; //this is me that updated the time so continue to the release code
                }
            }

            for (MessageQueue q : qus) { //release all queues by the known time.
                ((DelayedMessageQueue) q).release(mtime);
            }
        }

    }

    /**
     * @param groupKey
     * @return true if idle was resolved
     */
    public boolean forwardTime() {

        boolean found = false;
        long min = -1;
        try {
            mailBoxModifierKey.acquire();
            try {
                for (String groupKey : mailBoxes.keySet()) {
                    MessageQueue[] qus = takeQueues(groupKey); //all the queues from the current group
                    for (MessageQueue q : qus) {
                        Long v = ((DelayedMessageQueue) q).minimumMessageTime();
                        if (v == null) {
                            continue;
                        }
                        if (min > v || !found) {
                            min = v;
                            found = true;
                        }
                    }
                }

                if (found) {
                    for (String groupKey : mailBoxes.keySet()) {
                        MessageQueue[] qus = takeQueues(groupKey); //all the queues from the current group
                        for (MessageQueue q : qus) { //release all queues by the known time.
                            ((DelayedMessageQueue) q).release(min);
                        }
                    }
                    
                    mailBoxModifierKey.release();
                    releaseAllBlockingAgents();
                    mailBoxModifierKey.acquire();
                    return true;
                }
            } finally {
                mailBoxModifierKey.release();
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    @Override
    public void onIdleDetection() {
        forwardTime();
    }

    @Override
    public boolean tryResolveIdle() {
        return false;
    }

    @Override
    public void idleCannotBeResolved() {
    }

    @Override
    public void idleResolved() {
    }

    private class DetectionMailer implements Mailer {

        @Override
        public MessageQueue register(Agent agent, String groupKey) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void unregisterAll() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void send(Message msg, int to, String groupKey) throws UnRegisteredAgentException {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void broadcast(Message msg, String groupKey) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void broadcast(Message msg) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void unregister(int id, String groupKey) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public boolean isAllMailBoxesAreEmpty(String groupKey) {
            for (int i = 0; i < agentActiveGroups.length; i++) {
                if (takeQueues(agentActiveGroups[i])[i].availableMessages() > 0) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public void setExecution(Execution aThis) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void releaseAllBlockingAgents(String mailGroup) {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void releaseAllBlockingAgents() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public void hookIn(BeforeMessageSentHook hook) {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
