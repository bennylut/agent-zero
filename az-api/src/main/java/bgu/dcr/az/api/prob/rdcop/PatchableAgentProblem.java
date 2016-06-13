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

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.prob.ConstraintCheckResult;
import bgu.dcr.az.api.prob.ImmutableProblem;
import bgu.dcr.az.api.prob.KAryConstraint;
import bgu.dcr.az.api.prob.ProblemType;
import bgu.dcr.az.api.prob.cpack.KAryTreeConstraintPackage;
import bgu.dcr.az.api.tools.Assignment;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public class PatchableAgentProblem implements PossibleProblem {

    private Agent.AgentProblem underline;
    private KAryTreeConstraintPackage constraints;
    private Map<Integer, Set<Integer>> domains;
    private double probability = 0;

    public PatchableAgentProblem(Agent.AgentProblem underline) {
        this.underline = underline;
        this.constraints = new KAryTreeConstraintPackage(underline.getNumberOfVariables());
    }

    @Override
    public ProblemType type() {
        return underline.type();
    }

    @Override
    public int getConstraintCost(int var1, int val1, int var2, int val2) {
        final ConstraintCheckResult qt = underline.getQueryTemp();
        constraints.getConstraintCost(var1, var1, val1, var2, val2, qt);
        underline.increaseCC(qt.getCheckCost());
        return qt.getCost();
    }

    @Override
    public int getConstraintCost(Assignment ass) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public ImmutableSet<Integer> getDomainOf(int var) {
        final Set<Integer> v = domains.get(var);
        if (v == null) {
            throw new UnsupportedOperationException("variable " + var + " is not exists!");
        }
        return new ImmutableSet<>(v, true);
    }

    @Override
    public int getDomainSize(int var) {
        return getDomainOf(var).size();
    }

    @Override
    public HashMap<String, Object> getMetadata() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public Set<Integer> getNeighbors(int var) {
        return constraints.getNeighbores(var);
    }

    @Override
    public int getNumberOfVariables() {
        return domains.size();
    }

    @Override
    public boolean isConstrained(int var1, int var2) {
        return getNeighbors(var1).contains(var2);
    }

    @Override
    public int calculateCost(Assignment a) {
        final ConstraintCheckResult qt = underline.getQueryTemp();
        constraints.calculateCost(underline.getAgentId(), a, qt);
        underline.increaseCC(qt.getCheckCost());
        return qt.getCost();
    }

    public void createVariable(int variable) {
        if (domains.containsKey(variable)) {
            throw new UnsupportedOperationException("variable " + variable + " already exists");
        }

        domains.put(variable, new HashSet<>());
    }

    public boolean containsVariable(int variable) {
        return domains.containsKey(variable);
    }

    public void setVariableDomain(int variable, Set<Integer> domain) {
        domains.put(variable, domain);
    }

    public void addConstraint(int owner, KAryConstraint kAryConstraint) {
        constraints.setConstraintCost(owner, kAryConstraint);
    }

    @Override
    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

}
