/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.tools;

import bgu.dcr.az.api.Agent;

/**
 *
 * @author bennyl
 */
public interface RootSelectionAlgorithm {
    public int select(Agent a);
    
    public static class Default implements RootSelectionAlgorithm{

        @Override
        public int select(Agent a) {
            return 0;
        }
        
    }
}
