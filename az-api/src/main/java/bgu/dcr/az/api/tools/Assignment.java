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
package bgu.dcr.az.api.tools;

import bgu.dcr.az.api.prob.ImmutableProblem;
import bgu.dcr.az.api.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.exp.UnassignedVariableException;
import bgu.dcr.az.api.prob.Problem;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public class Assignment implements Serializable, DeepCopyable {

    private LinkedHashMap<Integer, Integer> assignment;
    private transient int cachedCost = -1;

    /**
     * construction of a new empty assignment
     */
    public Assignment() {
        this.assignment = new LinkedHashMap<>();

    }

    public Assignment(int... of) {
        this();
        Agt0DSL.panicIf(of.length % 2 != 0, "for every variable you must supply a value which means that the number of parameters must be even while it: " + of.length);
        for (int i = 0; i < of.length; i += 2) {
            assign(of[i], of[i + 1]);
        }
    }

    private Assignment(Assignment a) {
        this.assignment = new LinkedHashMap<>();
        for (Entry<Integer, Integer> e : a.assignment.entrySet()) {
            this.assignment.put(e.getKey(), e.getValue());
        }
    }

    /**
     * assign val to var if var already been assign then its value will be
     * overwriten
     *
     * @param var
     * @param val
     */
    public void assign(int var, int val) {
        assignment.put(var, val);
        cachedCost = -1;
    }

    /**
     * remove var's assignment
     *
     * @param var
     */
    public void unassign(int var) {
        assignment.remove(var);
        cachedCost = -1;
    }

    /**
     * same as unassign(agt.getId());
     *
     * @param agt
     */
    public void unassign(Agent agt) {
        unassign(agt.getId());
    }

    /**
     * @param var
     * @return true if var is assigned
     */
    public boolean isAssigned(int var) {
        return assignment.containsKey(var);
    }

    /**
     * @param var
     * @return the value assigned to var or null if no such assignment
     */
    public Integer getAssignment(int var) {
        final Integer ass = assignment.get(var);
        if (ass == null) {
            throw new UnassignedVariableException("calling getAssignment with variable " + var + " while its is not assigned");
        }
        return ass;
    }

    public Set<Entry<Integer, Integer>> getAssignments() {
        return assignment.entrySet();
    }

    /**
     * @param p
     * @return the cost of this assignment - (increase cc checks)
     */
    public int calcCost(ImmutableProblem p) {
        if (p instanceof Agent.AgentProblem) {
            if (cachedCost < 0) {
                cachedCost = p.calculateCost(this);;
            }
            return cachedCost;
        } else {
            return ((Problem) p).calculateGlobalCost(this);
        }
    }

    /**
     * @param var
     * @param val
     * @param p
     * @return the cost that will be added (or all ready added) to this assignment by assigning  {@code var <- val} in the problem p
     *             this includes binary and unary costs
     * 
     * means: 
     * costOf(assignment + <var,val>) - costOf(assignment - <var, currentAssignmentOf(var)>)
     * 
     * * (increase cc checks)
     */
    public int calcAddedCost(int var, int val, ImmutableProblem p) {
        int oldCache = cachedCost;
        boolean assignend = isAssigned(var);
        int old = (assignend ? getAssignment(var) : 0);
        unassign(var);
        int withoutCost = calcCost(p);
        
        assign(var, val);
        int ans = calcCost(p) - withoutCost;
        if (assignend) {
            assign(var, old);
            cachedCost = oldCache;
        } else {
            unassign(var);
            cachedCost = withoutCost;
        }

        return ans;
    }

    /**
     * @param var
     * @param p
     * @return the cost of the assignment without the given variable assignment
     */
    public int calcCostWithout(int var, ImmutableProblem p) {
        int oldCache = cachedCost;
        boolean assignend = isAssigned(var);
        int old = (assignend ? getAssignment(var) : 0);
        
        unassign(var);
        int ans = calcCost(p);
        if (assignend) {
            assign(var, old);
        } 
        
        cachedCost = oldCache; //fix cache

        return ans;
    }

    /**
     * @param var
     * @param domain
     * @param p
     * @return from the given domain the value that assigning var to it will be
     * add the least to the assignment
     */
    public int findMinimalCostValue(int var, Collection<Integer> domain, ImmutableProblem p) {
        boolean first = true;
        int min = 0;
        int c;
        int minv = -1;
        for (Integer dval : domain) {
            c = calcAddedCost(var, dval, p);
            if (first) {
                min = c;
                minv = dval;
                first = false;
            } else {
                if (c < min) {
                    min = c;
                    minv = dval;
                }
            }
        }

        return minv;
    }

    /**
     * searches for the first value that is consistent with this assignment in the given domain of values
     * if no such value found then this function will return the defaultValue
     * @param var
     * @param domain
     * @param p
     * @param defaultValue
     * @return 
     */
    public int findConsistentValue(int var, Collection<Integer> domain, ImmutableProblem p, int defaultValue){
        for (Integer d : domain){
            if (isConsistentWith(var, d, p)){
                return d;
            }
        }
        
        return defaultValue;
    }
    
    /**
     * find the first assignment to variable - var that keeps the assignment
     * under the given upperbound returns -1 if none found..
     *
     * @param var
     * @param upperbound
     * @param domain
     * @param p
     * @return
     */
    public int findFirstAssignmentUnderUB(int upperbound, int var, Collection<Integer> domain, ImmutableProblem p) {
        int cost = calcCost(p);
        if (cost >= upperbound) {
            return -1;
        }
        for (Integer d : domain) {
            if (cost + calcAddedCost(var, d, p) < upperbound) {
                return d;
            }
        }
        return -1;
    }

    /**
     *
     * @return a deep copy of this assignment (same as calling deepCopy)
     */
    @Deprecated
    public Assignment copy() {
        return deepCopy();
    }

    /**
     * @return the assigned variables in this assignment
     */
    public ImmutableSet<Integer> assignedVariables() {
        return new ImmutableSet<>(assignment.keySet());
    }

    /**
     * @return the unassigned variables in this assignemt - same as calling
     */
    public ImmutableSet<Integer> unassignedVariables(ImmutableProblem p) {
        List<Integer> all = Agt0DSL.range(0, p.getNumberOfVariables() - 1);
        all.removeAll(assignment.keySet());
        return new ImmutableSet<>(all);
    }

    /**
     * @return the sum of the assigned variables
     */
    public int getNumberOfAssignedVariables() {
        return assignment.keySet().size();
    }

    public boolean isFull(ImmutableProblem problem) {
        return getNumberOfAssignedVariables() == problem.getNumberOfVariables();
    }

    /**
     * return true if the assignment is consistent with assigning var->val
     *
     * @param var
     * @param val
     * @param p
     * @return
     */
    public boolean isConsistentWith(int var, int val, ImmutableProblem p) {
        boolean assignend = isAssigned(var);
        int old = (assignend ? getAssignment(var) : 0);
        int oldCache = cachedCost;
        
        assign(var, val);
        boolean ans = calcCost(p) == 0;
        if (assignend) {
            assign(var, old);
        } else {
            unassign(var);
        }
        
        cachedCost = oldCache; //fix cache
        return ans;
    }

    public boolean isConsistent(ImmutableProblem p) {
        return calcCost(p) == 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Assignment)) {
            return false;
        } else {
            Assignment otherAssignment = (Assignment) obj;
            if (otherAssignment.assignment.size() != assignment.size()){
                return false;
            }
            
            for (Entry<Integer, Integer> e : assignment.entrySet()) {
                if (!otherAssignment.isAssigned(e.getKey()) || otherAssignment.getAssignment(e.getKey()) != e.getValue()) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.assignment != null ? this.assignment.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        for (Entry<Integer, Integer> e : assignment.entrySet()) {
            sb.append(", ").append(e.toString());
        }
        String str = sb.toString();
        if (str.length() > 2) {
            return "{" + sb.toString().substring(2) + "}";
        }
        return "{}";
    }

    @Override
    public Assignment deepCopy() {
        return new Assignment(this);
    }
}
