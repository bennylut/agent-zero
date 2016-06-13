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
