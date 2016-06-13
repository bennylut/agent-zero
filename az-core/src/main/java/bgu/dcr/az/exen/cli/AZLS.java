/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.cli;

import bgu.dcr.az.api.exen.escan.AlgorithmMetadata;
import bgu.dcr.az.api.exen.escan.Registery;
import bgu.dcr.az.exen.util.CLIs;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.args4j.Option;

/**
 *
 * @author Administrator
 */
public class AZLS {

    @Option(name = "-rf", usage = "file to which the information will be dropped into", required = true)
    File resultFile;

    public static void main(String[] args) {
        PrintStream out = null;
        try {
            AZLS command = new AZLS();
            CLIs.parseArgs(command, args);
            List<Class> agents = Registery.UNIT.getAllAgentTypes();
            out = new PrintStream(command.resultFile);
            for (Class agent : agents) {
                AlgorithmMetadata algo = new AlgorithmMetadata(agent);
                out.println(algo.getName() + "," + algo.getAgentClass().getName());
            }
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AZLS.class.getName()).log(Level.SEVERE, null, ex); // SHOULD NEVER HAPPENED...
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
