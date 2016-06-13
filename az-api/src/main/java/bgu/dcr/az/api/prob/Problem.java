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

import bgu.dcr.az.api.prob.cpack.ConstraintsPackage;
import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.tools.Assignment;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An abstract class for problems that should let you build any type of problem
 *
 * @author guyafe, edited by bennyl
 */
public class Problem implements ImmutableProblem {

    private HashMap<String, Object> metadata = new HashMap<>();
    protected int numvars;
    protected ImmutableSetOfIntegers[] domain;
    protected ConstraintsPackage constraints;
    protected ProblemType type;
    protected int maxCost = 0;
    protected boolean singleDomain = true;

    @Override
    public String toString() {
        return ProblemPrinter.toString(this);
    }

    /**
     * @param var1
     * @param var2
     * @return true if there is a constraint between var1 and var2 operation
     *         cost: o(d^2)cc
     */
    @Override
    public boolean isConstrained(int var1, int var2) {
        return getNeighbors(var1).contains(var2);
    }

    /**
     * @param var1
     * @param val1
     * @param var2
     * @param val2
     * @return true if var1=val1 consistent with var2=val2
     */
    @Override
    public boolean isConsistent(int var1, int val1, int var2, int val2) {
        return getConstraintCost(var1, val1, var2, val2) == 0;
    }

    /**
     * return the domain size of the variable var
     *
     * @param var
     * @return
     */
    @Override
    public int getDomainSize(int var) {
        return getDomainOf(var).size();
    }

    /**
     * @return this problem metadata
     */
    @Override
    public HashMap<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * @param var
     * @return all the variables that costrainted with the given var
     */
    @Override
    public Set<Integer> getNeighbors(int var) {
        return constraints.getNeighbores(var);
    }


    public ImmutableSet<Integer> getDomain() {
        if (!singleDomain) {
            throw new UnsupportedOperationException("calling get domain on a problem with domain that is unique to each variable is unsupported - call  getDomainOf(int) instaed.");
        }
        return domain[0];
    }

    @Override
    public int getNumberOfVariables() {
        return numvars;
    }

    protected void initialize(ProblemType type, List<? extends Set<Integer>> domain, boolean singleDomain) {
        this.singleDomain = singleDomain;
        this.domain = ImmutableSetOfIntegers.arrayOf(domain);
        this.numvars = domain.size();
        this.type = type;
        this.constraints = type.newConstraintPackage(numvars, domain.get(0).size());
    }

    /**
     * initialize the problem with multiple domains the number of variables is
     * the domain.size()
     *
     * @param type    the type of the problem
     * @param domains list of domains for each agent - this list also determines
     *                the number of variables that will be domains.size
     */
    public void initialize(ProblemType type, List<? extends Set<Integer>> domains) {
        initialize(type, domains, false);
    }

    /**
     * initialize the problem with a single domain
     *
     * @param type              the problem type
     * @param numberOfVariables number of variables in this problem
     * @param domain            the domain for all the variables.
     */
    public void initialize(ProblemType type, int numberOfVariables, Set<Integer> domain) {
        initialize(type, ImmutableSetOfIntegers.repeat(domain, numberOfVariables), true);
    }

    /**
     * initialize the problem with a single domain that its values are
     * 0..1-domainSize
     *
     * @param type
     * @param numberOfVariables
     * @param domainSize
     */
    public void initialize(ProblemType type, int numberOfVariables, int domainSize) {
        initialize(type, numberOfVariables, new HashSet<Integer>(Agt0DSL.range(0, domainSize - 1)));
    }

    /**
     * @return the type of the problem
     */
    @Override
    public ProblemType type() {
        return type;
    }

    /**
     * return the domain that belongs to variable var
     */
    @Override
    public ImmutableSet<Integer> getDomainOf(int var) {
        return domain[var];
    }

    @Override
    public int getConstraintCost(Assignment ass) {
        throw new UnsupportedOperationException("Not supported without providing owner. Please use getConstraintCost(int owner, Assignment ass)");
    }

    @Override
    public int calculateCost(Assignment a) {
        throw new UnsupportedOperationException("Not supported when not accessed from inside of an agent code - please use getGlobalCost");
    }

    /**
     * this class is required to allow array of this type as java cannot create
     * an array of generic types and we want to avoid uneccecery casting
     */
    protected static class ImmutableSetOfIntegers extends ImmutableSet<Integer> {

        public ImmutableSetOfIntegers(Collection<Integer> data) {
            super(data);
        }

        public static ImmutableSetOfIntegers[] arrayOf(List<? extends Set<Integer>> of) {
            ImmutableSetOfIntegers[] a = new ImmutableSetOfIntegers[of.size()];

            int i = 0;
            for (Set<Integer> o : of) {
                a[i++] = new ImmutableSetOfIntegers(o);
            }

            return a;
        }

        public static List<ImmutableSetOfIntegers> repeat(Set<Integer> set, int times) {
            ImmutableSetOfIntegers[] ret = new ImmutableSetOfIntegers[times];
            ImmutableSetOfIntegers is = new ImmutableSetOfIntegers(set);
            for (int i = 0; i < ret.length; i++) {
                ret[i] = is;
            }

            return Arrays.asList(ret);
        }
    }

    /**
     * tries to add new kary constraint - as can be seen from the owner eyes
     * replaces the k-ary constraint if it exists
     *
     * @param owner
     * @param constraint
     */
    public void setConstraint(int owner, KAryConstraint constraint) {
        constraints.setConstraintCost(owner, constraint);
    }

    /**
     * add into the existing constrains
     *
     * @param owner
     * @param constraint
     */
    public void addConstraint(int owner, KAryConstraint constraint) {
        constraints.addConstraintCost(owner, constraint);
    }

    /**
     * symmetrically adding the constraint to all of the participants
     *
     * @param constraint
     */
    public void addConstraint(KAryConstraint constraint) {
        for (int participant : constraint.getParicipients()) {
            constraints.addConstraintCost(participant, constraint);
        }
    }

    /**
     * symmetrically setting the constraint to all of the participants
     *
     * @see setConstraintCost
     *
     * @param constraint
     */
    public void setConstraint(KAryConstraint constraint) {
        for (int participant : constraint.getParicipients()) {
            constraints.setConstraintCost(participant, constraint);
        }
    }

    public void setConstraintCost(int owner, int x1, int v1, int x2, int v2, int cost) {
        constraints.setConstraintCost(owner, x1, v1, x2, v2, cost);
    }

    public void setConstraintCost(int owner, int x1, int v1, int cost) {
        constraints.setConstraintCost(owner, x1, v1, cost);
    }

    public void setConstraintCost(int x1, int v1, int x2, int v2, int cost) {
        constraints.setConstraintCost(x1, x1, v1, x2, v2, cost);
    }

    public void setConstraintCost(int x1, int v1, int cost) {
        constraints.setConstraintCost(x1, x1, v1, cost);
    }

    public void getConstraintCost(int owner, int x1, int v1, ConstraintCheckResult result) {
        constraints.getConstraintCost(owner, x1, v1, result);
    }

    public void getConstraintCost(int owner, int x1, int v1, int x2, int v2, ConstraintCheckResult result) {
        constraints.getConstraintCost(owner, x1, v1, x2, v2, result);
    }

    public int getConstraintCost(int owner, int x1, int v1) {
        ConstraintCheckResult result = new ConstraintCheckResult();
        constraints.getConstraintCost(owner, x1, v1, result);
        return result.getCost();
    }

    public int getConstraintCost(int owner, int x1, int v1, int x2, int v2) {
        ConstraintCheckResult result = new ConstraintCheckResult();
        constraints.getConstraintCost(owner, x1, v1, x2, v2, result);
        return result.getCost();
    }

    @Override
    public int getConstraintCost(int x1, int v1) {
        ConstraintCheckResult result = new ConstraintCheckResult();
        constraints.getConstraintCost(x1, x1, v1, result);
        return result.getCost();
    }

    @Override
    public int getConstraintCost(int x1, int v1, int x2, int v2) {
        ConstraintCheckResult result = new ConstraintCheckResult();
        constraints.getConstraintCost(x1, x1, v1, x2, v2, result);
        return result.getCost();
    }

    public void getConstraintCost(int owner, Assignment k, ConstraintCheckResult result) {
        constraints.getConstraintCost(owner, k, result);
    }

    public int getConstraintCost(int owner, Assignment k) {
        ConstraintCheckResult result = new ConstraintCheckResult();
        constraints.getConstraintCost(owner, k, result);
        return result.getCost();
    }

    public void addNeighbor(int to, int neighbor) {
        constraints.addNeighbor(to, neighbor);
    }

    /**
     * calculate the cost of the given assignment - taking into consideration
     * all the constraints that related to it, the method put the return value
     * in an array of 2 [cost, number of constraints checked] that is given to
     * the method as the parameter ans
     *
     * @param owner
     * @param assignment
     * @return
     */
    public void calculateCost(int owner, Assignment assignment, ConstraintCheckResult result) {
        constraints.calculateCost(owner, assignment, result);
    }

    public int calculateGlobalCost(Assignment a) {
        return constraints.calculateGlobalCost(a);
    }
}
