/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.tools;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.DeepCopyable;
import bgu.dcr.az.api.Hooks;
import bgu.dcr.az.api.Hooks.BeforeMessageSentHook;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.agt.SimpleAgent;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Mostly taken from: Asynchronous Forward Bounding for Distributed COPs by:
 * Amir Gershman AMIRGER@CS.BGU.AC.IL Amnon Meisels AM@CS.BGU.AC.IL Roie Zivan
 * ZIVANR@CS.BGU.AC.IL Department of Computer Science, Ben-Gurion University of
 * the Negev, Beer-Sheva, 84-105, Israel
 *
 * http://www.bgu.ac.il/~zivanr/files/AFB_JAIR09.pdf
 *
 * The Time-Stamp Mechanism (Nguyen et al., 2004; Meisels & Zivan, 2007)
 * --------------------------------------------------------------------- The
 * requirements from this mechanism are that given two messages with two
 * different partial assignments, it must determine which one of them is
 * obsolete. An obsolete partial assignment is one that was abandoned by the
 * search process because one of the assigned agen ts has changed its
 * assignment. This requirement is accomplished by the time-stamping mechanism
 * in the following way. Each agent keeps a local running-assignment counter.
 * Whenever it performs an assignment it increments its local counter. Whenever
 * it sends a message containing its assignment, the agent copies its current
 * counter onto the message. Each message holds a vector containing the counters
 * of the agents it passed through. The i-th element of the vector corresponds
 * to Agent i’s counter. This vector is in fact the time-stamp. A
 * lexicographical comparison of two such vectors will reveal which time-stamp
 * is more up-to-date. Each agent saves a copy of what it knows to be the most
 * up-to-date time-stamp. When receiving a new message with a newer time-stamp,
 * the agent updates its local saved “latest” time-stamp. Suppose agent Ai
 * receives a message with a time-stamp that is lexicographically smaller than
 * the locally saved “latest”, by comparing the ﬁrst i − 1 elements of the
 * vector. This means that the message was based on a combination of assignments
 * which was already abandoned and thismessage is discarded. Only when the
 * message’s time-stamp in the ﬁrst i − 1 elemental is equal or greater than the
 * locally saved ”best” time-stamp is the message processed further. The
 * vector’s counters might appear to require a lot of space, as the number of
 * assignments can grow exponentially in the number of agents. However, if the
 * agent (Ai) resets its local counter to zero each time the assignments of
 * higher priority agents are altered, the counters will remain small (log of
 * the size of the value domain), and the mechanism will remain correct.
 *
 * Limitations: ------------ this time stamp mechanism is vulnerable to dynamic
 * variable ordering because of the lexicographic comparison, in vulnerable i
 * means it will not work! dont use it if this is one of the algorithm
 * requirements!
 *
 * @author bennyl
 */
public class TimeStamp implements Serializable, DeepCopyable {

    public static final String TIME_STAMP_METADATA = "TimeStamp.TIME_STAMP_METADATA";
    private int[] id;
    private int usingAgentId;

    /**
     *
     * @param a
     */
    public TimeStamp(SimpleAgent a) {
        this.id = new int[a.getNumberOfVariables()];
        this.usingAgentId = a.getId();
    }

    public void registerToSendInOutgoingMessages(final Agent a) {
        final int id1 = a.getId();
        new Hooks.BeforeMessageSentHook() {

            @Override
            public void hook(int sender, int recepient, Message msg) {
                if (id1 == sender) {
                    msg.getMetadata().put(TIME_STAMP_METADATA, TimeStamp.this);
                }
            }
        }.hookInto(
                Agent.PlatformOperationsExtractor.extract(a).getExecution());
    }

    public static TimeStamp extract(Message m) {
        return (TimeStamp) m.getMetadata().get(TIME_STAMP_METADATA);
    }

    private TimeStamp() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TimeStamp) {
            TimeStamp other = (TimeStamp) obj;
            return Arrays.equals(this.id, other.id);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Arrays.hashCode(this.id);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i : id) {
            sb.append(i).append('.');
        }

        return sb.toString();
    }

    /**
     *
     * @param o
     * @param asking
     * @return 1 if this > other, 0 if equals and -1 otherwise.
     */
    public int compare(TimeStamp o, Agent asking) {
        for (int i = 0; i < asking.getId(); i++) {
            if (this.id[i] > o.id[i]) {
                return 1;
            }
            if (this.id[i] < o.id[i]) {
                return -1;
            }
        }
        return 0;
    }

    /**
     * @return the local time of the usingAgent (the agent that created this
     * timeStamp)
     */
    public int getLocalTime() {
        return id[usingAgentId];
    }

    /**
     * set the local time of the creating agent
     *
     * @param localTime
     */
    public void setLocalTime(int localTime) {
        id[usingAgentId] = localTime;
    }

    /**
     * add 1 to the creating agent local time
     */
    public void incLocalTime() {
        id[usingAgentId]++;
    }

    /**
     * copying the timestamp from the given timestamp , the idea is that the
     * localtimes of all the agents up to the crating agent of this timestamp
     * are copyied (all the rest are irelevant for the given agent, the creating
     * agent stays as it was - no other modifications are occured)
     *
     * @param t
     */
    public void copyFrom(TimeStamp t) {
        for (int i = 0; i < t.id.length; i++) {
            if (i == usingAgentId) {
                continue;
            }
            this.id[i] = t.id[i];
        }
    }

    @Override
    public Object deepCopy() {
        TimeStamp deep = new TimeStamp();
        deep.id = new int[id.length];
        System.arraycopy(id, 0, deep.id, 0, id.length);
        deep.usingAgentId = usingAgentId;

        return deep;
    }
}
