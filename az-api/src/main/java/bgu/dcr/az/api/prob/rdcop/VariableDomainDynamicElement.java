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
