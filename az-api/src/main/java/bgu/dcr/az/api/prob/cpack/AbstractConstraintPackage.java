/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
