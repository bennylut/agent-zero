/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.tools;

import bgu.dcr.az.api.agt.SimpleAgent;
import bgu.dcr.az.api.tools.NestableTool;

/**
 *
 * @author bennyl
 */
public class SimpleNestableTool extends NestableTool{
    SimpleAgent nest;

    public SimpleNestableTool(SimpleAgent nest) {
        this.nest = nest;
    }
    
    @Override
    protected SimpleAgent createNestedAgent() {
        return nest;
    }

    public SimpleAgent getNestedAgent() {
        return nest;
    }
    
}
