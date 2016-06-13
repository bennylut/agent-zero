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
package bgu.dcr.az.api.exen;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Hooks;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.exp.UnRegisteredAgentException;

/**
 *
 * @author User
 */
public interface NonBlockingMailer {

    /**
     * broadcast a message to all agents (except the sending agent)
     * @param msg
     */
    void broadcast(Message msg, String groupKey);

    /**
     * broadcast the given message to all mail groups
     * @param msg
     */
    void broadcast(Message msg);

    /**
     * hook that will get called whenever the mailer will get new message
     */
    void hookIn(Hooks.BeforeMessageSentHook hook);

    /**
     * register new Agent
     * the mailbox can only send messages to registered agents
     * @param agent
     */
    NonBlockingMessageQueue register(Agent agent, String groupKey);

    /**
     * send a message to a registered agent
     * @param msg
     * @param to
     * @throws UnRegisteredAgentException
     */
    void send(Message msg, int to, String groupKey) throws UnRegisteredAgentException;

    /**
     * set the execution that this mailer is responsible for
     * @param aThis
     */
    void setExecution(Execution aThis);

    /**
     * remove agent with the given id from the registered list,
     * @param id
     */
    void unregister(int id, String groupKey);

    /**
     * remove all registered agents
     * usecase: reset mailer
     */
    void unregisterAll();
    
}
