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
package bgu.dcr.az.exen.cli;

import bgu.dcr.az.api.exen.ExecutionSelector;
import bgu.dcr.az.exen.escan.ExperimentReader;
import bgu.dcr.az.exen.util.CLIs;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.kohsuke.args4j.Option;

/**
 *
 * @author Administrator
 */
public class AmZListExecutions {

    
    @Option(name = "-e", usage = "the experiment file name", required = true)
    File experimentFile;
    @Option(name = "-r", usage = "the file where to write the executions list", required = true)
    File resultFile;
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            AmZListExecutions command = new AmZListExecutions();
            CLIs.parseArgs(command, args);
            List<ExecutionSelector> execs = ExperimentReader.listExecutions(command.experimentFile);
            
            PrintWriter pw = new PrintWriter(command.resultFile);
            for (ExecutionSelector e : execs){
                pw.println(e.toString());
            }
            pw.close();
            
            System.exit(0);
        } catch (IOException ex) {
            CLIs.scream("Cannot find the experiment file specified", ex);
        } catch (InstantiationException ex) {
            CLIs.scream("Experiment contains modules that cannot be resolved", ex);
        } catch (IllegalAccessException ex) {
            CLIs.scream("Experiment curropted modules - contact the module developer", ex);
        }
    }
        
}
