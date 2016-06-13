/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.prob.rdcop;

import java.util.Set;

/**
 *
 * @author bennyl
 */
public class VariableDomainDynamicElement extends DynamicElement {

    private int variable;
    private Set<Integer>[] possibleDomains;
    private double[] probabilities;

    public VariableDomainDynamicElement(int variable, Set<Integer>[] possibleDomains, double[] probabilities) {
        this.variable = variable;
        this.possibleDomains = possibleDomains;
        this.probabilities = probabilities;
    }

    @Override
    public int getNumberOfPossibilities() {
        return probabilities.length;
    }

    @Override
    public double getProbabilityFor(int possibility) {
        return probabilities[possibility];
    }

    @Override
    public void patch(int possibility, PatchableAgentProblem toPatch) {
        if (toPatch.containsVariable(variable)) {
            toPatch.setVariableDomain(variable, possibleDomains[possibility]);
        }
    }

    @Override
    public PatchOrder desidePatchOrder(DynamicElement o) {
        if (o instanceof VariableExistanceDynamicElement) {
            if (((VariableExistanceDynamicElement) o).getVariable() == variable) {
                return PatchOrder.AFTER;
            }
        }

        return PatchOrder.INDIFFERENT;
    }

}
