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

import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.prob.ProblemType;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ds.ImmutableSet;
import bgu.dcr.az.api.prob.Problem;
import java.util.Random;

/**
 *
 * @author bennyl
 */
@Register(name="adcop-unstructured")
public class UnstructuredADCOPGen extends UnstructuredDCOPGen {

    @Override
    public void generate(Problem p, Random rand) {
        p.initialize(ProblemType.ADCOP, n, new ImmutableSet<Integer>(Agt0DSL.range(0, d - 1)));
        for (int i = 0; i < p.getNumberOfVariables(); i++) {
            for (int j = 0; j < p.getNumberOfVariables(); j++) {
                if (rand.nextDouble() < p1) {
                    buildConstraint(i, j, p, false, rand);
                }
            }
        }
    }

}
