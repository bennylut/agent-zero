/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.correctness;

import bgu.dcr.az.api.prob.Problem;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public abstract class IterativeCSPSolver {

    public static final Logger log = Logger.getLogger(IterativeCSPSolver.class.getName());
    private boolean consistent;
    protected Status stat;
    private HashMap<Integer, Integer> assignments;
    private int currentWorkingVariable = 0;

    protected abstract int label(int i, Problem p);

    protected abstract int unLabel(int i, Problem p);

    public boolean isConsistent() {
        return consistent;
    }

    public void setConsistent(boolean consistent) {
        this.consistent = consistent;
    }

    /**
     * @param p
     * @return list of assignments if solution found or null if no solution.
     */
    public Status solve(Problem p) {

        assignments = new HashMap<Integer, Integer>();
        setConsistent(true);
        stat = Status.unknown;
        int i = 0;

        while (stat == Status.unknown) {
            currentWorkingVariable = i;
            if (isConsistent()) {
                //log.info("labaling var " + i);
                i = label(i, p);
            } else {
                //log.info("unlabaling var " + i);
                i = unLabel(i, p);
            }

            if (i >= p.getNumberOfVariables()) {
                stat = Status.solution;
            } else if (i == -1) {
                stat = Status.imposible;
            }
        }

        return stat;
    }

    protected boolean check(Problem p, int i, int vi, int j, int vj) {
        return p.isConsistent(i, vi, j, vj);
    }

    protected void assign(int var, int val) {

        assignments.put(var, val);
    }

    /**
     * @param var
     * @return if no such assignent return -1 else return the assignment
     */
    protected int getAssignment(int var) {
        Integer ret = assignments.get(new Integer(var));
        return (ret == null ? -1 : ret);
    }

    public Status getCurrentStatus() {
        return stat;
    }

    public Set<Entry<Integer, Integer>> getAssignments() {
        if (assignments == null) return Collections.EMPTY_SET;
        return assignments.entrySet();
    }

    public int getCurrentWorkingVariable() {
        return currentWorkingVariable;
    }

    public static enum Status {

        unknown,
        solution,
        imposible
    }

}
