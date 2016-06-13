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
