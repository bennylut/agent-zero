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

import bgu.dcr.az.api.ContinuationMediator;
import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.exen.mdef.Limiter;

/**
 *
 * @author bennyl
 */
public interface AgentRunner extends Runnable {
    
    /**
     * this method supports the nested agents feature, 
     * an nested agent can call this method to "change the skin" of the agent with $originalAgentId
     * as for this moment all the messages that will get sent by this type of agent (type = upper level class) 
     * will received by this new nested agent, you should only nest agent of the same type once - means 
     * let A, B, C be agent types , if A 'nested in' B 'nested in' C then A!=B, A!=C, B!=C (all the types are different).
     * Note: this method is blocking - means that the you will resume your code only after $nestedAgent will call finish. 
     *
     * if $nestedAgent called Panic (directly or indirectly by throwing an exception) all the nested agents will be discarded 
     * and the agent runner will stop.
     * 
     * in the current time no "non self stopeable" algorithms can be excuted as nested agents, 
     * the idle detector is still not designed to work with multipule nested agents - in next versions (if it will be requested)
     * we will add support for this feature (Implementor Comment: by adding mailGroupKey in the idle detector)
     * @param originalAgentId the original agent id (before the nesting)
     * @param nestedAgent the agent to nest
     * @param cmed mediator contains the continuation function 
     */
    public void nest(int originalAgentId, SimpleAgent nestedAgent, ContinuationMediator cmed);
    
    /**
     * this method will block the calling thread until this agent runner will finish it current work
     * this method is designed to not be depended on the thread join - so that the thread can be reused (and thus not get finished)
     * @throws InterruptedException 
     */
    public void join() throws InterruptedException;

    public void setLimiter(Limiter limiter);
}
