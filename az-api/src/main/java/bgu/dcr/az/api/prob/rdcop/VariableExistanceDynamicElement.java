/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.prob.rdcop;

import bgu.dcr.az.api.prob.Problem;

/**
 *
 * @author bennyl
 */
public class VariableExistanceDynamicElement extends DynamicElement {

    int variable;
    double existanceProbability;

    public VariableExistanceDynamicElement(int variable, double existanceProbability) {
        this.variable = variable;
        this.existanceProbability = existanceProbability;
    }

    public int getVariable() {
        return variable;
    }

    @Override
    public int getNumberOfPossibilities() {
        return Double.compare(existanceProbability, 1.0) == 0 ? 1 : 2; //true or false;
    }

    @Override
    public double getProbabilityFor(int value) {
        if (value == 0) {
            return 1 - existanceProbability;
        }
        return existanceProbability;
    }

    @Override
    public void patch(int possibility, PatchableAgentProblem toPatch) {
        toPatch.createVariable(variable);
    }

    @Override
    public PatchOrder desidePatchOrder(DynamicElement o) {
        return PatchOrder.INDIFFERENT;
    }

}
