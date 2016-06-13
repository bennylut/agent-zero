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
package bgu.dcr.az.exen;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Hooks;
import bgu.dcr.az.api.Hooks.BeforeMessageSentHook;
import bgu.dcr.az.api.exen.Mailer;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.exen.MessageQueue;
import bgu.dcr.az.api.exen.Execution;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Inna
 */
public abstract class AbstractMailer implements Mailer {

    private Execution exec;
    protected Map<String, MessageQueue[]> mailBoxes = new HashMap<String, MessageQueue[]>();
    protected Semaphore mailBoxModifierKey = new Semaphore(1);
    protected List<Hooks.BeforeMessageSentHook> beforeMessageSentHooks = new LinkedList<Hooks.BeforeMessageSentHook>();

    @Override
    public void releaseAllBlockingAgents(String mailGroup) {
        for (MessageQueue q : takeQueues(mailGroup)) {
            q.releaseBlockedAgent();
        }
    }

    @Override
    public void releaseAllBlockingAgents() {
        try {
            mailBoxModifierKey.acquire();
            for (String mailGroup : mailBoxes.keySet()) {
                releaseAllBlockingAgents(mailGroup);
            }
            mailBoxModifierKey.release();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    protected MessageQueue[] takeQueues(String groupKey) {
        try {
            MessageQueue[] qs = mailBoxes.get(groupKey);
            if (qs == null) {
                mailBoxModifierKey.acquire();
                if (!mailBoxes.containsKey(groupKey)) { //maybe someone already modified it..
                    final int numberOfVariables = exec.getGlobalProblem().getNumberOfVariables();
                    qs = new MessageQueue[numberOfVariables];
                    for (int i = 0; i < numberOfVariables; i++) {
                        qs[i] = generateNewMessageQueue(i, groupKey);
                    }
                    mailBoxes.put(groupKey, qs);
                } else {
                    qs = mailBoxes.get(groupKey);
                }
                mailBoxModifierKey.release();
            }

            return qs;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public AbstractMailer() {
    }

    @Override
    public void broadcast(Message msg, String groupKey) {
        int sender = msg.getSender();
        for (int i = 0; i < exec.getGlobalProblem().getNumberOfVariables(); i++) {
            if (i != sender) {
                send(msg, i, groupKey);
            }
        }
    }

    @Override
    public void broadcast(Message msg) {
        int sender = msg.getSender();
        for (String gkey  : mailBoxes.keySet()) {
            for (int i = 0; i < exec.getGlobalProblem().getNumberOfVariables(); i++) {
                if (i != sender) {
                    send(msg, i, gkey);
                }
            }
        }
    }

    protected abstract MessageQueue generateNewMessageQueue(int agent, String forGroup);

    public Map<String, MessageQueue[]> getMailBoxes() {
        return mailBoxes;
    }

    @Override
    public boolean isAllMailBoxesAreEmpty(String groupKey) {
        MessageQueue[] qs = takeQueues(groupKey);
        for (MessageQueue q : qs) {
            if (q.availableMessages() > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public MessageQueue register(Agent agent, String groupKey) {
        return takeQueues(groupKey)[agent.getId()];
    }

    @Override
    public void send(Message msg, int to, String groupKey) {
        MessageQueue q = takeQueues(groupKey)[to];

        for (BeforeMessageSentHook h : beforeMessageSentHooks) {
            h.hook(msg.getSender(), to, msg);
        }

        Message mcopy = msg.copy();
        q.add(mcopy);
    }

    @Override
    public void hookIn(BeforeMessageSentHook hook) {
        beforeMessageSentHooks.add(hook);
    }

    @Override
    public void setExecution(Execution exec) {
        this.exec = exec;
    }

    @Override
    public void unregister(int id, String groupKey) {
        MessageQueue[] qs = takeQueues(groupKey);
        qs[id] = null;
        for (MessageQueue q : qs) {
            if (q != null) {
                return;
            }
        }
        mailBoxes.remove(groupKey);
    }

    @Override
    public void unregisterAll() {
        mailBoxes.clear();
    }
}
