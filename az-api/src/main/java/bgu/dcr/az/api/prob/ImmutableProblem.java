/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.prob;

import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.tools.Assignment;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public interface ImmutableProblem {

    /**
     * @return the problem type.
     */
    ProblemType type();

    /**
     *
     * @param var1
     * @param val1
     * @param var2
     * @param val2
     * @return the cost of assigning var1=val1 when var2=val2
     */
    int getConstraintCost(int var1, int val1, int var2, int val2);

    /**
     *
     * @param var1
     * @param val1
     * @return the cost of assigning var1=val1
     */
    default int getConstraintCost(int var1, int val1) {
        return getConstraintCost(var1, val1, var1, val1);
    }

    /**
     * return the cost of the k-ary constraint represented by the given
     * assignment
     *
     * @param ass
     * @return
     */
    int getConstraintCost(Assignment ass);

    /**
     * return the domain of the given variable
     *
     * @param var
     * @return
     */
    ImmutableSet<Integer> getDomainOf(int var);

    /**
     * return the domain size of the variable var
     *
     * @param var
     * @return
     */
    int getDomainSize(int var);

    /**
     * @return this problem metadata
     */
    HashMap<String, Object> getMetadata();

    /**
     * @param var
     * @return all the variables that costrainted with the given var
     */
    Set<Integer> getNeighbors(int var);

    /**
     * @return the number of variables defined in this problem
     */
    int getNumberOfVariables();

    /**
     * @param var1
     * @param val1
     * @param var2
     * @param val2
     * @return true if var1=val1 consistent with var2=val2
     */
    default boolean isConsistent(int var1, int val1, int var2, int val2) {
        return getConstraintCost(var1, val1, var2, val2) == 0;
    }

    /**
     * @param var1
     * @param var2
     * @return true if there is a constraint between var1 and var2 operation
     * cost: o(d^2)cc
     */
    boolean isConstrained(int var1, int var2);

    /**
     * return the cost of the given assignment (taking into consideration all
     * the constraints that apply to it)
     *
     * @param a
     * @return
     */
    int calculateCost(Assignment a);

}
