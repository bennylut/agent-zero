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
package bgu.dcr.az.exen.correctness;

import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.api.tools.Assignment;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Stack;

/**
 *
 * @author bennyl
 */
public class MACSolver extends FCSolver {

    //private Stack<HashSet<SimpleEntry<Integer, Integer>>> ac4_recudtions;
    private ArrayList<Stack<HashSet<Integer>>> ac4_reductions;
    private HashSet[][][] ac4_support;
    private List[][] ac4_supportList;

    /**
     * @param i
     * @param vi
     * @param j
     * @return true if assigning i->vi is not emptying the domain of j
     */
    boolean assignmentEmptyDomainOf(int i, int vi, int j) {
        HashSet temp = new HashSet(ac4_support[i][vi][j]);
        temp.retainAll(currentDomain.get(j));
        return temp.isEmpty();
    }

    void initializeAc4(Problem p) {
        ac4_support = new HashSet[p.getNumberOfVariables()][p.getDomainSize(0)][p.getNumberOfVariables()];
        ac4_supportList = new List[p.getNumberOfVariables()][p.getDomainSize(0)];
        ac4_reductions = new ArrayList<Stack<HashSet<Integer>>>();

        for (int i = 0; i < p.getNumberOfVariables(); i++) {
            for (int k = 0; k < p.getDomainSize(0); k++) {
                for (int j = 0; j < p.getNumberOfVariables(); j++) {
                    ac4_support[i][k][j] = new HashSet();
                }
                ac4_supportList[i][k] = new LinkedList();
            }
            ac4_reductions.add(new Stack<HashSet<Integer>>());
        }

        for (int i = 0; i < p.getNumberOfVariables(); i++) {
            for (int j = i + 1; j < p.getNumberOfVariables(); j++) {
                for (int vi = 0; vi < p.getDomainSize(0); vi++) {
                    for (int vj = 0; vj < p.getDomainSize(0); vj++) {
                        if (p.isConsistent(i, vi, j, vj)) {
                            ac4_support[i][vi][j].add(vj);
                            ac4_supportList[i][vi].add(new SimpleEntry<Integer, Integer>(j, vj));

                            ac4_support[j][vj][i].add(vi);
                            ac4_supportList[j][vj].add(new SimpleEntry<Integer, Integer>(i, vi));
                        }
                    }
                }
            }
        }
    }

    boolean ac4reduceAssignment(int i, int vi) {
        currentDomain.get(i).remove(new Integer(vi));
        ac4_reductions.get(i).peek().add(vi);

        if (currentDomain.get(i).isEmpty()) {
            return false;
        }

        return true;
    }

    Queue<SimpleEntry<Integer, Integer>> buildAc4Queue(Problem p, int subProblemPos) {
        Queue<SimpleEntry<Integer, Integer>> ret = new LinkedList<SimpleEntry<Integer, Integer>>();

        for (int i = subProblemPos; i < p.getNumberOfVariables(); i++) {
            for (int j = i + 1; j < p.getNumberOfVariables(); j++) {
                for (Integer vi : currentDomain.get(i).toArray(new Integer[]{})) {
                    if (assignmentEmptyDomainOf(i, vi, j)) {
                        SimpleEntry<Integer, Integer> entry = new SimpleEntry<Integer, Integer>(i, vi);
                        ret.add(entry);
                        if (!ac4reduceAssignment(i, vi)) {
                            return null; //cannot continue as the problem already not arcconsistent
                        }
                    }
                }
            }
        }

        return ret;
    }

    void buildNewReductionSet(int fromVar, Problem p) {
        for (int i = fromVar; i < p.getNumberOfVariables(); i++) {
            ac4_reductions.get(i).push(new HashSet<Integer>());
        }
    }

    boolean subProblemIsArcConsistent(Problem p, int subProblemPos) {
        buildNewReductionSet(subProblemPos, p);
        Queue<SimpleEntry<Integer, Integer>> q = buildAc4Queue(p, subProblemPos);

        if (q == null) {
            return false;
        }

        while (!q.isEmpty()) {
            SimpleEntry<Integer, Integer> badAssignment = q.poll();
            Integer i = badAssignment.getKey();
            Integer vi = badAssignment.getValue();


            for (Object _supportingAssignment : ac4_supportList[i][vi]) {
                SimpleEntry<Integer, Integer> supportingAssignment = (SimpleEntry<Integer, Integer>) _supportingAssignment;
                Integer j = supportingAssignment.getKey();
                Integer vj = supportingAssignment.getValue();

                if (j < i || !currentDomain.get(j).contains(vj)) {
                    continue;
                }

                if (currentDomain.get(j).contains(vj)) {
                    if (assignmentEmptyDomainOf(j, vj, i)) {
                        q.add(new SimpleEntry<Integer, Integer>(j, vj));
                        if (!ac4reduceAssignment(j, vj)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    void undoAc4Reductions(int fromVar, Problem p) {
        for (int i = fromVar; i < p.getNumberOfVariables(); i++) {
            HashSet<Integer> reduction = ac4_reductions.get(i).pop();
            for (Integer vi : reduction) {
                currentDomain.get(i).add(vi);
            }
        }
    }

    @Override
    protected void updateCurrentDomain(int i, Problem p) {
        super.updateCurrentDomain(i, p);

        for (HashSet<Integer> reduction : ac4_reductions.get(i)){
            currentDomain.get(i).removeAll(reduction);
        }
    }

    @Override
    protected int label(int i, Problem p) {
        int ret = super.label(i, p);
        if (ret == -1) {
            return ret;
        }

        if (isConsistent()) {
            setConsistent(subProblemIsArcConsistent(p, ret));
        }

        return ret;
    }

    @Override
    protected int unLabel(int i, Problem p) {
        if (i > 0) {
            undoAc4Reductions(i, p);
        }
        return super.unLabel(i, p);
    }

    @Override
    public Status solve(Problem p) {
        initializeAc4(p);
        return super.solve(p);
    }

    public Assignment getAssignment() {
        Assignment ret = new Assignment();
        for (Entry<Integer, Integer> a : getAssignments()){
            ret.assign(a.getKey(), a.getValue());
        }
        
        return ret;
    }
}
