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
package bgu.dcr.az.api.agt;

import bgu.dcr.az.api.prob.ImmutableProblem;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.exen.Mailer;
import bgu.dcr.az.api.exp.UnRegisteredAgentException;
import java.util.Collection;
import java.util.Set;

/**
 * a middle way object used to send a message
 * you should never directly create an instance of this class
 * the usage of this class should be through the method send of simple agent 
 * - simpleAgent.send("Message", arg1,...,argn).to(recipient); 
 * @author bennyl
 *
 */
public class SendMediator {

    private Message msg;
    private Mailer mailer;
    private ImmutableProblem curp;
    private String agentGroupKey;

    public SendMediator(Message msg, Mailer mailer, ImmutableProblem curp, String agentGroupKey) {
        this.msg = msg;
        this.mailer = mailer;
        this.curp = curp;
        this.agentGroupKey = agentGroupKey;
    }

    /**
     * send the message to the given array of agents
     * @param agents
     */
    public void to(int... agents) {
        msg.getMetadata().put(Message.RECEPIENT_METADATA, agents);
        for (int a : agents) {
            try {
                mailer.send(msg, a, agentGroupKey);
            } catch (IndexOutOfBoundsException ex) {
                throw new UnRegisteredAgentException("the agent with the id " + a + " is not registered (" + agentGroupKey + ")", ex);
            }
        }
    }
    
    public void toFirstAgent(){
        to(0);
    }
    
    public void toLastAgent(){
        to(this.curp.getNumberOfVariables()-1);
    }

    /**
     * send the message to the next agent in the defined order 
     */
    public void toNextAgent() {
        to(msg.getSender() + 1);
    }

    /**
     * send the message to the previous agent in the defined order
     */
    public void toPreviousAgent() {
        to(msg.getSender() - 1);
    }

    /**
     * send the message to all the agents that came after the sending agent in the defined order
     */
    public void toAllAgentsAfterMe() {
        for (int i = msg.getSender() + 1; i < curp.getNumberOfVariables(); i++) {
            to(i);
        }
    }

    /**
     * send the message to the sending agent neighbores in the problem p
     * while neighbores are all the agents that constrainted with the sending agent
     * @param p
     * @deprecated use {@link toNeighbores()} instaed 
     * @supportUntil i6
     */
    @Deprecated
    public void toNeighbores(ImmutableProblem p) {
        Set<Integer> neighbors;
        neighbors = p.getNeighbors(msg.getSender());
        for (int n : neighbors) {
            to(n);
        }
    }
    
    /**
     * send the message to the sending agent neighbores in the problem p
     * while neighbores are all the agents that constrainted with the sending agent
     * @param p
     */
    public void toNeighbores() {
        Set<Integer> neighbors;
        neighbors = curp.getNeighbors(msg.getSender());
        for (int n : neighbors) {
            to(n);
        }
    }

    /**
     * attach metadata to the message pirior to sending it
     * usage: send("MESSAGE",arg1,...,argn).withMeta("META",data).to*
     * @param name
     * @param val
     * @return
     */
    public SendMediator withMeta(String name, Object val) {
        msg.getMetadata().put(name, val);
        return this;
    }

    /**
     * send the message to all the given variables
     * @param all 
     */
    public void toAll(Collection<Integer> all) {
        for (Integer i : all) {
            to(i);
        }
    }
}
