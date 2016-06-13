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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

/**
 *
 * @author bennyl
 */
public class FCSolver extends IterativeCSPSolver {

    protected ArrayList<LinkedList<Integer>> currentDomain;
    protected ArrayList<Stack<HashSet<Integer>>> reductions;
    protected ArrayList<LinkedList<Integer>> past_fc;
    protected ArrayList<LinkedList<Integer>> future_fc;

    public ArrayList<LinkedList<Integer>> getCurrentDomain() {
        return currentDomain;
    }
    
    protected boolean checkForward(int i, int j, Problem p) {
        HashSet<Integer> reduction = new HashSet<Integer>();
        for (Integer vj : currentDomain.get(j)) {
            assign(j, vj);
            if (!check(p, i, getAssignment(i), j, vj)) {
                reduction.add(vj);
            }
        }

        if (!reduction.isEmpty()) {
            currentDomain.get(j).removeAll(reduction);
            reductions.get(j).push(reduction);
            future_fc.get(i).addFirst(j);
            past_fc.get(j).addFirst(i);
        }

        return !currentDomain.get(j).isEmpty();
    }

    protected void undoReductions(int i) {
        for (Integer j : future_fc.get(i)) {
            HashSet<Integer> reduction = reductions.get(j).pop();
            currentDomain.get(j).addAll(reduction);
            past_fc.get(j).removeFirst();
        }

        future_fc.get(i).clear();
    }

    protected void updateCurrentDomain(int i, Problem p) {
        LinkedList<Integer> cd = currentDomain.get(i);
        cd.clear();
        for (int j = 0; j < p.getDomainSize(0); j++) {
            cd.add(j);
        }

        for (HashSet<Integer> reduction : reductions.get(i)){
            currentDomain.get(i).removeAll(reduction);
        }
    }

    @Override
    public Status solve(Problem p) {
        currentDomain = new ArrayList<LinkedList<Integer>>();
        reductions = new ArrayList<Stack<HashSet<Integer>>>();
        past_fc = new ArrayList<LinkedList<Integer>>();
        future_fc = new ArrayList<LinkedList<Integer>>();

        for (int i = 0; i < p.getNumberOfVariables(); i++) {
            currentDomain.add(new LinkedList<Integer>());
            reductions.add(new Stack<HashSet<Integer>>());
            past_fc.add(new LinkedList<Integer>());
            future_fc.add(new LinkedList<Integer>());
        }

        for (LinkedList<Integer> cd : currentDomain) {
            for (int i = 0; i < p.getDomainSize(0); i++) {
                cd.addLast(i);
            }
        }

        return super.solve(p);
    }

    protected Status superSolve(Problem p) {
        return super.solve(p);
    }

    @Override
    protected int label(int i, Problem p) {
        setConsistent(false);
        for (Integer vi : currentDomain.get(i).toArray(new Integer[]{})) {
            if (isConsistent()) {
                break;
            }
            assign(i, vi);


            setConsistent(true);
            for (int j = i + 1; j < p.getNumberOfVariables(); j++) {
                if (!isConsistent()) {
                    break;
                }

                setConsistent(checkForward(i, j, p));
            }

            if (!isConsistent()) {
                currentDomain.get(i).remove(new Integer(vi));
                undoReductions(i);

            }

        }

        if (isConsistent()) {
            return i + 1;
        }
        return i;

    }

    @Override
    protected int unLabel(int i, Problem p) {
        int h = i - 1;
        if (h < 0) {
            setConsistent(false);
            return h; //nothing to do ...
        }
        undoReductions(h);
        updateCurrentDomain(i, p);
        //log.info("removing value " + getAssignment(h) + " from currentdomain[" + h + "]");
        currentDomain.get(h).remove(new Integer(getAssignment(h)));
        //log.info("after remove current domain contains " + getAssignment(h) + "? " + currentDomain.get(h).contains(new Integer(getAssignment(h))));
        setConsistent(!currentDomain.get(h).isEmpty());
        //System.out.println("after unlabling consistent = " + isConsistent());
        return h;
    }

}
