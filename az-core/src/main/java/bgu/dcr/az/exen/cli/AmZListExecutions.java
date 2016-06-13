/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
