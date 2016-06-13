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
package bgu.dcr.az.api.tools;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Agent.PlatformOps;
import bgu.dcr.az.api.exen.AgentRunner;
import bgu.dcr.az.api.Continuation;
import bgu.dcr.az.api.ContinuationMediator;
import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.exen.Execution;

/**
 *
 * @author bennyl
 */
public abstract class NestableTool {

    private int finalAssignment = Integer.MIN_VALUE;
    private boolean hasAssignment = false;

    /**
     * flag the nested agent to be started - it will only start after the
     * current message handling ends (or if you are using it from within the
     * start function after it ends)
     *
     * you should use continuations (by calling
     * calculate(..).andWhenDoneDo(Continuation) to get notified when the nested
     * agent terminated.
     *
     * this method variant is the preferred variant if you use nested agents
     * more then once all the agents with the same group name will be executed
     * in the same environment for example if you want to run nested agent once
     * you can call this method with mail group "one" all the agents will do the
     * same and they will all be switched to "one" environment when they done
     * and you want to run other nested agent call this method with the group
     * name "two" then if there are any other agents that still not exit "one"
     * you will run isolated from them.
     *
     * @param groupName
     * @param callingAgent
     * @return
     */
    public ContinuationMediator calculate(final Agent callingAgent, String groupName) {
        return startCalculation(callingAgent, groupName);
    }

    /**
     * flag the nested agent to be started it will only start after the current
     * message handling ends (or if you are using it from within the start
     * function after it ends)
     *
     * you should use continuations (by calling
     * calculate(..).andWhenDoneDo(Continuation) to get notified when the nested
     * agent terminated.
     *
     * this method variant is the preferred variant if you use nested agents
     * more then once all the agents with the same group name will be executed
     * in the same environment for example if you want to run nested agent once
     * you can call this method with mail group "one" all the agents will do the
     * same and they will all be switched to "one" environment when they done
     * and you want to run other nested agent call this method with the group
     * name "two" then if there are any other agents that still not exit "one"
     * you will run isolated from them.
     *
     * ********************************************
     * * same as calling calculate(callingAgent, * *
     * callingAgent.getClass.getName())
     *
     *
     * ******************************************** @param groupName
     * @param callingAgent
     * @return
     */
    public ContinuationMediator calculate(final Agent callingAgent) {
        return startCalculation(callingAgent, null);
    }

    /**
     * flag the nested agent to be started. Notice that it will only start after
     * the agent done handling the current message (or if you are using it from
     * within the start function after it ends)
     *
     * you should use continuations (by calling
     * calculate(..).andWhenDoneDo(Continuation) to get notified when the nested
     * agent terminated.
     *
     *
     * @param callingAgent
     * @return
     */
    private ContinuationMediator startCalculation(final Agent callingAgent, String mailGroup) {
        final Execution exec = Agent.PlatformOperationsExtractor.extract(callingAgent).getExecution();
        ContinuationMediator ret = new ContinuationMediator() {
            @Override
            public void andWhenDoneDo(final Continuation c) {
                super.andWhenDoneDo(new Continuation() {
                    @Override
                    public void doContinue() {
                        final Assignment assignment = exec.getResult().getAssignment();
                        if (assignment != null) {
                            if (assignment.isAssigned(callingAgent.getId())) {
                                finalAssignment = assignment.getAssignment(callingAgent.getId());
                                hasAssignment = true;
                            }
                        }
                        c.doContinue();
                    }
                });
            }
        };

        AgentRunner runner = exec.getAgentRunnerFor(callingAgent);
        SimpleAgent nested = createNestedAgent();
        System.out.println( "Request to nest new Agent for algorithm " +  nested.getAlgorithmName() + " inside " + callingAgent.getId() + "@" + callingAgent.getAlgorithmName());
        final PlatformOps nestedOps = Agent.PlatformOperationsExtractor.extract(nested);
        final PlatformOps originalOps = Agent.PlatformOperationsExtractor.extract(callingAgent);

        nestedOps.setExecution(exec, originalOps.getProblem());
        if (mailGroup != null) {
            nestedOps.setMailGroupKey(mailGroup);
        }
        nestedOps.setId(callingAgent.getId());
        originalOps.copyHooksTo(nested);

        runner.nest(callingAgent.getId(), nested, ret);
        return ret;
    }

    private String cname(Object obj) {
        return (obj.getClass().getSimpleName().isEmpty()
                ? obj.getClass().getName()
                : obj.getClass().getSimpleName());
    }

    protected abstract SimpleAgent createNestedAgent();

    /**
     * @return the assignment that the nested agent exit with, before using the
     * value of this method check that the agent was actually exit with an
     * assignment using the method {@link hasAssignment}
     */
    public int getFinalAssignment() {
        return this.finalAssignment;
    }

    /**
     * @return true if the nested agent exit with an assignment by any of the
     * following methods: * call finish(int) * call finish(cpa) and the agent
     * has an assignment in that cpa * submit some assignment during its
     * execution and then call finish()
     */
    public boolean hasAssignment() {
        return hasAssignment;
    }
}
