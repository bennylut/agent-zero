/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vdev;

import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.api.exen.escan.AlgorithmMetadata;
import bgu.dcr.az.api.exen.escan.ConfigurationMetadata;
import bgu.dcr.az.api.exen.escan.VariableMetadata;
import bgu.dcr.az.exen.AbstractTest;
import bgu.dcr.az.exen.async.AsyncTest;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class ExecutionMetadata {
    
    private List<InnerMetadata> meta = new LinkedList<>();
    
    public ExecutionMetadata(Test test, int execNumber) {
        AbstractTest at = (AbstractTest) test;
        final int numberOfAlgorithms = at.getAlgorithms().size();
        AlgorithmMetadata alg = at.getAlgorithms().get(execNumber%numberOfAlgorithms);
        double rvarVal = at.getVarStart() + (execNumber / (numberOfAlgorithms*at.getRepeatCount()))*at.getTickSize();
        ConfigurationMetadata.bubbleDownVariable(at, at.getRunningVarName(), rvarVal);
        
        meta.add(new InnerMetadata("Execution Type", (test instanceof AsyncTest? "Asynchronous": "Synchronous")));
        meta.add(new InnerMetadata("Algorithm", alg.getName(), alg));
        meta.add(new InnerMetadata("Problem Generator", at.getProblemGenerator().getClass().getSimpleName(), at.getProblemGenerator()));
    }

    public List<InnerMetadata> getInnerMeta() {
        return meta;
    }
    
    public static class InnerMetadata{
        public String module;
        public String name;
        public Map<String, String> variables;

        public InnerMetadata(String module, String name, Map<String, String> variables) {
            this.module = module;
            this.name = name;
            this.variables = variables;
        }
        
        public InnerMetadata(String module, String name) {
            this.module = module;
            this.name = name;
            this.variables = new HashMap<>();
        }

        public InnerMetadata(String module, String name, Object varsSource) {
            this.module = module;
            this.name = name;
            this.variables = new HashMap<>();
            VariableMetadata[] vars = VariableMetadata.scan(varsSource);
            for (VariableMetadata v : vars){
                variables.put(v.getName(), ""+v.getCurrentValue());
            }
        }
    }
}
