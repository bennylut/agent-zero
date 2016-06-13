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

import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.prob.Problem;
import java.util.Random;

/**
 *
 * @author bennyl
 */
@Register(name="dcop-connected")
public class ConnectedDCOPGen extends UnstructuredDCOPGen {

    @Override
    public void generate(Problem p, Random rand) {
        super.generate(p, rand);
        while (true) {
            boolean[] connections = new boolean[p.getNumberOfVariables()];
            calcConnectivity(p, 0, connections);
            if (allTrue(connections)) {
                return;
            }

            connect(0, getUnconnected(connections), p, rand);
        }
    }

    private int getUnconnected(boolean[] connections) {
        for (int i = 0; i < connections.length; i++) {
            if (!connections[i]) {
                return i;
            }
        }
        return -1;
    }

    private void connect(int var1, int var2, Problem p, Random rnd) {
        int val2 = rnd.nextInt(p.getDomainSize(var2));
        int val1 = rnd.nextInt(p.getDomainSize(var1));
        int cost = abs(rnd.nextInt()) % super.maxCost + 1;
        p.setConstraintCost(var1, val1, var2, val2, cost);
        p.setConstraintCost(var2, val2, var1, val1, cost);
    }

    private void calcConnectivity(Problem p, int root, boolean[] discovered) {
        discovered[root] = true;
        for (int n : p.getNeighbors(root)) {

            if (!discovered[n]) {
                calcConnectivity(p, n, discovered);
            } else if (allTrue(discovered)) {
                return;
            }
        }
    }

    private boolean allTrue(boolean[] a) {
        for (int i = 0; i < a.length; i++) {
            if (!a[i]) {
                return false;
            }
        }
        return true;
    }
}
