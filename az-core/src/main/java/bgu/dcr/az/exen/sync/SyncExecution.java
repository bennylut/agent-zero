/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.sync;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Hooks;
import bgu.dcr.az.api.exen.AgentRunner;
import bgu.dcr.az.api.exen.Experiment;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.api.exen.escan.AlgorithmMetadata;
import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.api.exen.SystemClock;
import bgu.dcr.az.exen.AbstractExecution;

/**
 *
 * @author bennyl
 */
public class SyncExecution extends AbstractExecution {

    AgentRunner[] handlingAgentRunners;
    
    public SyncExecution(Problem p, AlgorithmMetadata a, Test r, Experiment exp) {
        super(p, new SyncMailer(), a, r, exp);
        handlingAgentRunners = new AgentRunner[p.getNumberOfVariables()];
    }
    
    @Override
    protected void configure() {
        DefaultSystemClock clock = new DefaultSystemClock();
        setSystemClock(clock);
        ((SyncMailer) getMailer()).setClock(clock);
        final int numberOfVariables = getGlobalProblem().getNumberOfVariables();
        final int numberOfCores = Runtime.getRuntime().availableProcessors();
        final int numberOfAgentRunners = Math.min(numberOfCores, numberOfVariables);

        /**
         * THIS EXECUTION MOD USES AGENT RUNNER IN POOL MODE
         */
        if (!tryGenerateAgents()) {
            return;
        }

        setAgentRunners(SyncAgentRunner.createAgentRunners(numberOfAgentRunners, getSystemClock(), this, getAgents()));
        clock.setExcution(this); //MUST BE CALLED AFTER THE AGENT RUNNERS HAVE BEEN ASSIGNED...
    }

    @Override
    public void setAgentRunnerFor(int id, AgentRunner aThis) {
        handlingAgentRunners[id] = aThis;
    }

    @Override
    public AgentRunner getAgentRunnerFor(Agent a) {
        return handlingAgentRunners[a.getId()];
    }

    @Override
    protected void finish() {
    }
}
