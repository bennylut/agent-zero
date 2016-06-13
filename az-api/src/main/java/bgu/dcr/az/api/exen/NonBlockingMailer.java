/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
