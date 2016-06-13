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
package bgu.dcr.az.api.prob;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author User
 */
public class VirtualAgentsMapper {

    private OwningMediator mediator;
    private int[] variablesToAgents;
    private int numberOfAgents = 0;

    public VirtualAgentsMapper(int numberOfVariables) {
        variablesToAgents = new int[numberOfVariables];
        for (int i = 0; i < numberOfVariables; i++) {
            variablesToAgents[i] = -1;
        }
    }

    public OwningMediator agent(int id) {
        numberOfAgents = Math.max(numberOfAgents, id + 1);
        mediator.agent = id;
        return mediator;
    }

    public void randomize(int numberOfAgents, Random random) {
        this.numberOfAgents = numberOfAgents;

        if (numberOfAgents > variablesToAgents.length) {
            throw new UnsupportedOperationException("Number Of Variables must be bigger or equal to the number of agents");
        }

        LinkedList<Integer> variables = new LinkedList<>();
        for (int i = 0; i < variablesToAgents.length; i++) {
            variables.add(i);
        }

        Collections.shuffle(variables, random);

        //ensure that each agent got atleast one variable
        for (int i = 0; i < numberOfAgents; i++) {
            variablesToAgents[variables.removeFirst()] = i;
        }

        while (!variables.isEmpty()) {
            variablesToAgents[variables.removeFirst()] = random.nextInt(numberOfAgents);
        }
    }

    public void apply(Problem problem) {

        //validation 
        if (numberOfAgents == 0) {
            throw new UnsupportedOperationException("Could not apply the given map - no agents exist in this map.");
        }

        boolean[] assigned = new boolean[numberOfAgents];
        for (int i = 0; i < variablesToAgents.length; i++) {
            if (variablesToAgents[i] == -1) {
                throw new UnsupportedOperationException("Could not apply the given map - variable " + i + " does not belongs to any agent.");
            }

            assigned[variablesToAgents[i]] = true;
        }

        for (int i = 0; i < assigned.length; i++) {
            if (!assigned[i]) {
                throw new UnsupportedOperationException("Could not apply the given map - there is no variable assigned to agent " + i + ".");
            }
        }

        //applying 
        problem.getMetadata().put(VirtualAgentsMapper.class.getCanonicalName(), this);
    }

    public static VirtualAgentsMapper extractFromProblem(ImmutableProblem p) {
        return (VirtualAgentsMapper) p.getMetadata().get(VirtualAgentsMapper.class.getCanonicalName());
    }

    public class OwningMediator {

        private int agent;

        public VirtualAgentsMapper own(int... variables) {
            for (int v : variables) {
                variablesToAgents[v] = agent;
            }
            return VirtualAgentsMapper.this;
        }
    }
}
