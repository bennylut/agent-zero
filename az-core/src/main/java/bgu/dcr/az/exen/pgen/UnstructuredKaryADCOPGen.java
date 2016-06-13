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
package bgu.dcr.az.exen.pgen;

import java.util.Random;

import bgu.dcr.az.api.prob.Problem;
import bgu.dcr.az.api.prob.ProblemType;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.prob.RandomKAryConstraint;
import java.util.ArrayList;
import java.util.Arrays;

@Register(name = "kary-adcop-unstructured")
public class UnstructuredKaryADCOPGen extends AbstractProblemGenerator {

    @Variable(name = "n", description = "number of variables", defaultValue = "2")
    int n = 2;
    @Variable(name = "d", description = "domain size", defaultValue = "2")
    int d = 2;
    @Variable(name = "max-cost", description = "maximal cost of constraint", defaultValue = "10")
    int maxCost = 10;
    @Variable(name = "p1", description = "probablity of constraint between two variables", defaultValue = "0.6f")
    float p1 = 0.6f;
    @Variable(name = "max-k", description = "maximum constraint size", defaultValue = "2")
    int maxK = 2;

    @Override
    public void generate(Problem p, Random rand) {
        int[][] agentPermutations = agentGroupPermutations(n, maxK);
        p.initialize(ProblemType.K_ARY_ADCOP, n, d);


        for (int[] apr : agentPermutations) {
            for (int owner : apr) {
                if (rand.nextDouble() < p1) {
                    p.setConstraint(owner, new RandomKAryConstraint(n, d, maxCost, rand.nextInt(), apr));
                }
            }
        }
    }

    private static int[][] agentGroupPermutations(int numAgt, int maxAgentsInGroup) {
        ArrayList<boolean[]> encodedGroups = new ArrayList<boolean[]>();
        _agentGroupPermutations(maxAgentsInGroup, encodedGroups, new boolean[numAgt], 0);
        int[] decodedGroup = new int[maxAgentsInGroup];
        int[][] r = new int[encodedGroups.size()][];

        for (int i = 0; i < encodedGroups.size(); i++) {
            boolean[] a = encodedGroups.get(i);
            int inGroup = 0;
            for (int j = 0; j < numAgt; j++) {
                if (a[j]) {
                    decodedGroup[inGroup++] = j;
                }
            }

            r[i] = Arrays.copyOf(decodedGroup, inGroup);
        }

        return r;
    }

    private static int _agentGroupPermutations(int maxGroupSize, ArrayList<boolean[]> result, boolean[] current, int idx) {
        if (maxGroupSize < 1 || idx >= current.length) {
            result.add(Arrays.copyOf(current, current.length));
            return 1;
        }

        current[idx] = false;
        int sum = _agentGroupPermutations(maxGroupSize, result, current, idx + 1);
        current[idx] = true;
        sum += _agentGroupPermutations(maxGroupSize - 1, result, current, idx + 1);
        current[idx] = false;

        return sum;
    }
}
