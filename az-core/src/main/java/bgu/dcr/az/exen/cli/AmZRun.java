/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.cli;

import bgu.dcr.az.api.exen.ExecutionSelector;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.exen.ExperimentImpl;
import bgu.dcr.az.exen.escan.ExperimentReader;
import bgu.dcr.az.exen.stat.db.DatabaseUnit;
import bgu.dcr.az.exen.util.CLIs;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.args4j.Option;

/**
 *
 * @author Administrator
 */
public class AmZRun {

    private static byte[] OK = "OK\r\n".getBytes();
    @Option(name = "-e", usage = "the experiment file name", required = true)
    File experimentFile;
    @Option(name = "-rdir", usage = "the directory where to put the experiment results", required = true)
    File experimentResultsDir;
    @Option(name = "-p", usage = "port to receive commands upon")
    int port;
    
    private static boolean DEBUG = false;
    private static String CMD1 = "SELECT AC-ANT,SBB,8,8,2.4";
    private static String CMD2 = "SELECT AC-ANT,SBB,9,9,5.7";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            AmZRun command = new AmZRun();
            if (!DEBUG) {
                CLIs.parseArgs(command, args);
            } else {
                command.experimentFile = new File("experiment.xml");
                command.experimentResultsDir = new File("results");
            }

            //CONFIGURE DATA BASE - FOR SIMULATION MODE 
            DatabaseUnit.DATA_BASE_NAME = "db" + command.port + "" + DatabaseUnit.DATA_BASE_NAME;

            ExperimentImpl runningExperiment = (ExperimentImpl) ExperimentReader.read(command.experimentFile);
            runningExperiment.setAllowReuse(true);

            //RECONFIGURE EXPERIMENT TO REMOVE ANY CORRECTNESS TESTERS
            for (Test t : runningExperiment.getTests()) {
                t.setCorrectnessTester(null);
            }


            if (DEBUG) {
                select(CMD1, runningExperiment);
                writeResultFile(command);
                //select(CMD2, runningExperiment);
                //writeResultFile(command);
            } else {
                Socket sock = new Socket("localhost", command.port);
                BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("SELECT ")) {
                        select(line, runningExperiment);
                        writeResultFile(command);

                        sock.getOutputStream().write(OK);
                        sock.getOutputStream().flush();
                    }
                }
            }

            System.exit(0);
        } catch (IOException ex) {
            CLIs.scream("Cannot find the experiment file specified", ex);
        } catch (InstantiationException ex) {
            CLIs.scream("Experiment contains modules that cannot be resolved", ex);
        } catch (IllegalAccessException ex) {
            CLIs.scream("Experiment curropted modules - contact the module developer", ex);
        }
    }

    public static void select(String line, ExperimentImpl runningExperiment) throws NumberFormatException {
        //EXTRACT REQUEST PARAMS
        String[] params = line.substring("SELECT ".length()).split(",");
        String testName = params[0];
        String algorithmInstanceName = params[1];
        int problemNumber = Integer.valueOf(params[2]);
        int execNumber = Integer.valueOf(params[3]);
        double rvar = Double.valueOf(params[4]);

        //SETUP SELECTOR 
        ExecutionSelector eSel = new ExecutionSelector(testName, algorithmInstanceName, problemNumber, execNumber, rvar);
        runningExperiment.setExecutionSelector(eSel);

        //RUN
        runningExperiment.run();
    }

    public static void writeResultFile(AmZRun command) {
        command.experimentResultsDir.mkdirs();
        try {
            //WRITE RESULTS
            DatabaseUnit.UNIT.awaitStatistics();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Writing results...");
        DatabaseUnit.UNIT.dumpToCsv(command.experimentResultsDir);
        System.out.println("Writing results done.");
    }
}
