/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Hooks.ReportHook;
import bgu.dcr.az.api.Hooks.TerminationHook;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.api.exen.mdef.StatisticCollector;
import bgu.dcr.az.api.exen.mdef.Limiter;
import bgu.dcr.az.api.tools.Assignment;
import java.util.List;

/**
 *
 * @author bennyl
 */
public interface Execution extends Process {

    /**
     * @return the test that this execution is running in
     */
    Test getTest();
    
    void report(String to, Agent a, Object[] args);
    
    void hookIn(ReportHook hook);
    
    void hookIn(TerminationHook hook);
    
    /**
     * @return the global problem -
     * each agent have its own "version" of problem that is based on the global problem
     * using the global problem is reserved to the execution environment or to the execution tools - do not use it inside
     * your algorithms - use Agents getProblem() instaed.
     */
    Problem getGlobalProblem();

    /**
     * @return the mailer attached to this execution.
     */
    Mailer getMailer();

    ExecutionResult getResult();
    
    AgentRunner getAgentRunnerFor(Agent a);

    /**
     * @return the system clock, 
     * if this execution is an asynchronus execution then it will return null.
     */
    SystemClock getSystemClock();
    
    int getNumberOfAgentRunners();
    
    void setStatisticCollectors(List<StatisticCollector> collectors);

    /**
     * cause the executed environment to log the given data
     * @param agent
     * @param data
     */
    void log(int agent, String mailGroupKey, String data);

    /**
     * will stop this execution
     * and add the error and the exception to result
     * @param ex
     * @param error
     */
    void terminateDueToCrush(Exception ex, String error);
    
    /**
     * will stop this execution and mark the result accordingly
     */
    void terminateDueToLimiter();

    void submitPartialAssignment(int var, int val);

    void reportFinalAssignment(Assignment answer);
    
    /**
     * will stop the execution - interupting all the agent runners!
     */
    @Override
    void stop();
    
    /**
     * @return the agents that executed
     */
    Agent[] getAgents();

    public void setLimiter(Limiter timer);
    
    public Limiter getLimiter();
        
    /**
     * @return true if for some reason this execution is interrupted, 
     * Agent Runners should check the status of the execution interruption allways and if it is true they should never continue executing.
     */
    public boolean isInterrupted();
}
