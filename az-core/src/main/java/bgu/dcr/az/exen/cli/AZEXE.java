/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.cli;

import bgu.dcr.az.api.exp.ConnectionFaildException;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.Experiment;
import bgu.dcr.az.api.exen.Experiment.ExperimentListener;
import bgu.dcr.az.api.exen.Test;
import bgu.dcr.az.exen.dtp.AzDTPMessage;
import bgu.dcr.az.exen.dtp.Client;
import bgu.dcr.az.exen.escan.ExperimentReader;
import bgu.dcr.az.exen.stat.db.DatabaseUnit;
import bgu.dcr.az.exen.util.CLIs;
import java.io.File;
import java.io.IOException;
import org.kohsuke.args4j.Option;

/**
 *
 * @author Administrator
 */
public class AZEXE {

    
    @Option(name = "-e", usage = "the experiment file name", required = true)
    File experimentFile;
    @Option(name = "-rdir", usage = "the directory where to put the experiment results", required = true)
    File experimentResultsDir;
    @Option(name = "-cs", usage = "control server IP:PORT (ex. 127.0.0.1:7000)", required = false)
    String controlServer;
    @Option(name = "-id", usage = "the experiment id - will be used for the information source AzDTP field", required = true)
    String experimentId;
    
    Client client;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            AZEXE command = new AZEXE();
            CLIs.parseArgs(command, args);
            
            if (command.isUsingControlServer()){
                command.startConnectionToControlingServer();
            }
            
            Experiment runningExperiment = ExperimentReader.read(command.experimentFile);
            //DatabaseUnit.UNIT.start();

            //RECONFIGURE EXPERIMENT TO REMOVE ANY CORRECTNESS TESTERS
            for (Test t : runningExperiment.getTests()) {
                t.setCorrectnessTester(null);
            }

            if (command.isUsingControlServer()){
                runningExperiment.addListener(command.createAzDTPClient());
            }else {
                runningExperiment.addListener(command.createConsoleClient());
            }
            runningExperiment.run();
            
            System.out.println("Writing results...");
            DatabaseUnit.UNIT.dumpToCsv(command.experimentResultsDir);
            System.out.println("Writing results done.");
            
            System.out.println("Terminating...");
            System.exit(0);
        } catch (IOException ex) {
            CLIs.scream("Cannot find the experiment file specified", ex);
        } catch (InstantiationException ex) {
            CLIs.scream("Experiment contains modules that cannot be resolved", ex);
        } catch (IllegalAccessException ex) {
            CLIs.scream("Experiment curropted modules - contact the module developer", ex);
        }
    }
    
    private void startConnectionToControlingServer(){
        String[] sdata = controlServer.split(":");
        if (sdata.length != 2) CLIs.scream("server data not given in the format 'address:port'", null);
        client = Client.start(sdata[0], Integer.valueOf(sdata[1]));
    }
    
    private ExperimentListener createAzDTPClient() {
        final int[] idx = {0};
        
        return new ExperimentListener() {

            @Override
            public void onExpirementStarted(Experiment source) {
                client.send(new AzDTPMessage().setId("" + (idx[0]++)).setInfoSource(experimentId).setInfoType("experiment-start"));
            }

            @Override
            public void onExpirementEnded(Experiment source) {
                client.send(new AzDTPMessage().setId("" + (idx[0]++)).setInfoSource(experimentId).setInfoType("experiment-end"));
            }

            @Override
            public void onNewTestStarted(Experiment source, Test test) {
                client.send(new AzDTPMessage().setId("" + (idx[0]++)).setInfoSource(experimentId).setInfoType("new-test-start"));
            }

            @Override
            public void onNewExecutionStarted(Experiment source, Test test, Execution exec) {
                client.send(new AzDTPMessage().setId("" + (idx[0]++)).setInfoSource(experimentId).setInfoType("new-execution-start"));
            }

            @Override
            public void onExecutionEnded(Experiment source, Test test, Execution exec) {
                client.send(new AzDTPMessage().setId("" + (idx[0]++)).setInfoSource(experimentId).setInfoType("execution-end"));
            }
        };
    }

    private boolean isUsingControlServer() {
        return this.controlServer != null;
    }

    private ExperimentListener createConsoleClient() {
        return new ExperimentListener() {

            @Override
            public void onExpirementStarted(Experiment source) {
                System.err.println("Experiment Started");
            }

            @Override
            public void onExpirementEnded(Experiment source) {
                System.err.println("Experiment Ended");
            }

            @Override
            public void onNewTestStarted(Experiment source, Test test) {
                System.err.println("Test '" + test.getName() + "' started");
            }

            @Override
            public void onNewExecutionStarted(Experiment source, Test test, Execution exec) {
                System.err.println("Execution '" + test.getCurrentExecutionNumber() + "of " + (test.getTotalNumberOfExecutions()-1) + "' started");
            }

            @Override
            public void onExecutionEnded(Experiment source, Test test, Execution exec) {
                System.err.println("Execution '" + test.getCurrentExecutionNumber() + "of " + (test.getTotalNumberOfExecutions()-1) + "' ended");
            }
        };
    }
}
