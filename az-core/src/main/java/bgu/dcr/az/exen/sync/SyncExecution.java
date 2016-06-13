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
