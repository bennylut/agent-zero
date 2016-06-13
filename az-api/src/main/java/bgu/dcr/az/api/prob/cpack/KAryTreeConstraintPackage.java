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
package bgu.dcr.az.api.prob.cpack;

import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.prob.ComposedKAryConstraint;
import bgu.dcr.az.api.prob.ConstraintCheckResult;
import bgu.dcr.az.api.prob.KAryConstraint;
import bgu.dcr.az.api.tools.Assignment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public class KAryTreeConstraintPackage extends AbstractConstraintPackage {

    private Node[] roots;

    public KAryTreeConstraintPackage(int numvar) {
        super(numvar);
        roots = new Node[numvar];
        for (int i = 0; i < numvar; i++) {
            roots[i] = new Node();
        }
    }

    @Override
    public void setConstraintCost(int owner, int x1, int v1, int x2, int v2, int cost) {
        Agt0DSL.panic("cannot use binary or unary version of constraints in k-ary problem, use the setConstraintCost(int, int[], KAryConstraint) method instead.");
    }

    @Override
    public void setConstraintCost(int owner, int x1, int v1, int cost) {
        Agt0DSL.panic("cannot use binary or unary version of constraints in k-ary problem, use the setConstraintCost(int, int[], KAryConstraint) method instead.");
    }

    @Override
    public void getConstraintCost(int owner, int x1, int v1, ConstraintCheckResult result) {
        getConstraintCost(owner, new Assignment(x1, v1), result);
    }

    @Override
    public void getConstraintCost(int owner, int x1, int v1, int x2, int v2, ConstraintCheckResult result) {
        getConstraintCost(owner, new Assignment(x1, v1, x2, v2), result);
    }

    @Override
    public void getConstraintCost(int owner, Assignment k, ConstraintCheckResult result) {

        KAryConstraint constraint = roots[owner].getConstraint(k);
        if (constraint == null) {
            result.set(0, 0);
        } else {
            constraint.getCost(k, result);
        }
    }

    private void insertKAryConstraint(int owner, KAryConstraint constraint, boolean replace){
        roots[owner].add(constraint, replace);

        //update neighbores
        for (int p : constraint.getParicipients()) {
            if (owner != p) {
                addNeighbor(owner, p);
            }
        }
    }
    
    @Override
    public void setConstraintCost(int owner, KAryConstraint constraint) {
        insertKAryConstraint(owner, constraint, true);
    }

    /**
     * calculate the cost for the given assignment
     * @param owner
     * @param a
     * @param result 
     */
    @Override
    public void calculateCost(int owner, Assignment a, ConstraintCheckResult result) {
        List<KAryConstraint> constraintsToConsider = roots[owner].collectAllSubConstraints(a);

        int cost = 0;
        int cc = 0;
        if (constraintsToConsider != null) {
            for (KAryConstraint constraint : constraintsToConsider) {
                constraint.getCost(a, result);
                cost += result.getCost();
                cc += result.getCheckCost();
            }
        }

        result.set(cost, cc);
    }

    @Override
    public int calculateGlobalCost(Assignment a) {
        ConstraintCheckResult res = new ConstraintCheckResult();
        int cost = 0;
        for (int i = 0; i < getNumberOfVariables(); i++) {
            calculateCost(i, a, res);
            cost += res.getCost();
        }
        return cost;
    }

    @Override
    public void addConstraintCost(int owner, KAryConstraint constraint) {
        insertKAryConstraint(owner, constraint, false);
    }

    private static class Node {

        ComposedKAryConstraint constraint = new ComposedKAryConstraint();
        Map<Integer, Node> childrens;

        public Node() {
            this.childrens = new HashMap<>();
        }

        public void add(KAryConstraint constraint, boolean replace) {
            int[] participients = constraint.getParicipients();
            Arrays.sort(participients);
            _add(constraint, participients, 0, replace);
        }

        private void _add(KAryConstraint constraint, int[] participients, int idx, boolean replace) {

            if (idx < participients.length) {
                Node children = childrens.get(participients[idx]);
                if (children == null) {
                    children = new Node();
                    childrens.put(participients[idx], children);
                }

                children._add(constraint, participients, idx + 1, replace);
            } else {
                if (replace) {
                    this.constraint.getComposition().clear();
                }
                this.constraint.getComposition().add(constraint);
            }
        }

        public KAryConstraint getConstraint(Assignment a) {
            List<Integer> participients = extractParticipients(a);
            Node currentNode = this;
            for (Integer p : participients) {
                currentNode = currentNode.childrens.get(p);
                if (currentNode == null) {
                    return null;
                }
            }

            return currentNode.constraint;
        }

        public List<KAryConstraint> collectAllSubConstraints(Assignment a) {
            LinkedList<KAryConstraint> ret = new LinkedList<>();
            _collectAllSubConstraints(a.assignedVariables(), ret);
            return ret;
        }

        private void _collectAllSubConstraints(Set<Integer> participients, List<KAryConstraint> result) {
            if (constraint != null) {
                result.add(constraint);
            }

            for (Entry<Integer, Node> children : childrens.entrySet()) {
                if (participients.contains(children.getKey())) {
                    children.getValue()._collectAllSubConstraints(participients, result);
                }
            }
        }

        private List<Integer> extractParticipients(Assignment a) {
            List<Integer> participients = new ArrayList<>(a.assignedVariables());
            Collections.sort(participients);
            return participients;
        }
    }
}
