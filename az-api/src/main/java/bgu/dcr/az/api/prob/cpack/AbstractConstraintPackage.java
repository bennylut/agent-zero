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
import bgu.dcr.az.api.prob.NeighboresSet;
import bgu.dcr.az.api.tools.Assignment;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public abstract class AbstractConstraintPackage implements ConstraintsPackage {

    NeighboresSet[] neighbores;

    public AbstractConstraintPackage(int numvar) {
        this.neighbores = new NeighboresSet[numvar];
        for (int i = 0; i < this.neighbores.length; i++) {
            this.neighbores[i] = new NeighboresSet(numvar);
        }
    }

    @Override
    public Set<Integer> getNeighbores(int xi) {
        return neighbores[xi];
    }

    @Override
    public void addNeighbor(int to, int neighbor) {
        if (to != neighbor) { //you cannot be yourown neighbor!
            neighbores[to].add(neighbor);
        }else {
            Agt0DSL.panic("agent cannot be its own neighbor");
        }
        
    }

    protected int getNumberOfVariables() {
        return neighbores.length;
    }
}
