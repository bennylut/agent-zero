/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
