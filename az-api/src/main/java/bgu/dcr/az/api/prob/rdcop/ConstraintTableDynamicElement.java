/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.prob.rdcop;

import bgu.dcr.az.api.prob.KAryConstraint;

/**
 *
 * @author bennyl
 */
public class ConstraintTableDynamicElement extends DynamicElement {

    private int v1, v2;
    private KAryConstraint[] possibleConstraints;
    private double[] probabilities;

    public ConstraintTableDynamicElement(int v1, int v2, KAryConstraint[] possibleConstraints, double[] probabilities) {
        this.v1 = v1;
        this.v2 = v2;
        this.possibleConstraints = possibleConstraints;
        this.probabilities = probabilities;
    }

    @Override
    public int getNumberOfPossibilities() {
        return probabilities.length;
    }

    @Override
    public double getProbabilityFor(int value) {
        return probabilities[value];
    }

    @Override
    public void patch(int possibility, PatchableAgentProblem toPatch) {
        if (toPatch.containsVariable(v1) && toPatch.containsVariable(v2)) {
            toPatch.addConstraint(v1, possibleConstraints[possibility]);
        }
    }

    @Override
    public PatchOrder desidePatchOrder(DynamicElement o) {
        if (o instanceof VariableExistanceDynamicElement) {
            VariableExistanceDynamicElement v = (VariableExistanceDynamicElement) o;
            if (v.getVariable() == v1 || v.getVariable() == v2) {
                return PatchOrder.AFTER;
            }
        } else if (o instanceof VariableDomainDynamicElement) {
            return PatchOrder.AFTER;
        }

        return PatchOrder.INDIFFERENT;
    }

}
