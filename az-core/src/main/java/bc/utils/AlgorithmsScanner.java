/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.utils;

import bgu.dcr.az.api.exen.escan.AlgorithmMetadata;
import bgu.dcr.az.api.exen.escan.Registery;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

/**
 * this program read all the available algorithms that are in the class path
 * and print it to a file on the following CSV format:
 * ALGORITHM_NAME, CLASS_NAME
 * @author Administrator
 */
public class AlgorithmsScanner {
    
    /**
     * args[0] = file name
     * @param args 
     */
    public static void main(String[] args) throws FileNotFoundException{
        if (args.length == 0){
            args = new String[]{"algorithms.csv"};
        }
        File toFile = new File(args[0]);
        List<Class> agents = Registery.UNIT.getAllAgentTypes();
        
        PrintStream out = new PrintStream(toFile);
        for (Class agent : agents){
            AlgorithmMetadata algo = new AlgorithmMetadata(agent);
            out.println(algo.getName() + "," + algo.getAgentClass().getName());
        }
        
        out.close();
    }
}
