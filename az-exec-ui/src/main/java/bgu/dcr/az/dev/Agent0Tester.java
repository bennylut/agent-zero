/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.dev;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import nu.xom.ParsingException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 *
 * @author bennyl
 */
public class Agent0Tester  {

    @Option(name = "--es", usage = "opening a [pooling] server that will transmit events in json form.")
    boolean useEventServer;
    @Option(name = "-f", usage = "the file contain the metadata of the test.", required = true)
    File test;
    @Option(name = "--sfp", usage = "suppling directory to save failed problems in", required = false)
    File failedProblemsDir;
    @Option(name = "--gui", usage = "attach gui to view execution status", required = false)
    boolean useGui;
    @Option(name = "--emode", usage = "execution mode (run/debug) default to 'run', debug automaticly use gui and will fail"
    + " if --sfp or --prob option was not given", required = false)
    String executionMode = "run";

    public void go() throws ParsingException, IOException, MalformedURLException, ClassNotFoundException, InterruptedException {

        ExperimentExecutionController.UNIT.setFailProblemStorage(failedProblemsDir);
        
        if (executionMode.equals("run")){
            ExperimentExecutionController.UNIT.run(test, true, false);
        }else {
            ExperimentExecutionController.UNIT.run(test, true, true);
        }
    }

    public static void main(String[] args) {
        Agent0Tester tester = new Agent0Tester();
        CmdLineParser parser = new CmdLineParser(tester);
        try {
            parser.parseArgument(args);
            tester.go();
        } catch (Exception ex) {
            System.err.print("Error: " + ex.getMessage() + "\n\n");

            System.err.print("usage: ");
            parser.setUsageWidth(80);
            parser.printSingleLineUsage(System.err);
            System.err.print("\nDescription:\n");
            parser.printUsage(System.err);

            ex.printStackTrace();
        }
    }
}
