/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.utils;

import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.exen.mdef.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kdima85
 */
public class RTCodeScanner {
    public static final String AGENT_TYPE = "AGENT";
    public static final String CORRECTNESS_TESTER_TYPE = "CORRECTNESS_TESTER";
    public static final String ERROR_TYPE = "ERROR";
    public static final String LIMITER_TYPE = "LIMITER";
    public static final String MESSAGE_DELAYER_TYPE = "MESSAGE_DELAYER";
    public static final String PROBLEM_GENERATOR_TYPE = "PROBLEM_GENERATOR";
    public static final String STATISTIC_COLLECTOR_TYPE = "STATISTIC_COLLECTOR";
    public static final String UNKNOWN_TYPE = "UNKNOWN";

    //the first argument is the file to which write the output
    //rest of the arguments are the fully qualified class names 
    public static void main(String[] args) throws FileNotFoundException {
        File out = new File(args[0]);
        PrintWriter pw = new PrintWriter(out);

        for (int i = 1; i < args.length; i++) {
            try {
                Class<?> c = Class.forName(args[i]);
                if (c == null) {
                    pw.println(c.getCanonicalName() + " " + ERROR_TYPE);
                } else if (Agent.class.isAssignableFrom(c)) {
                    pw.println(c.getCanonicalName() + " " + AGENT_TYPE);
                } else if (ProblemGenerator.class.isAssignableFrom(c)) {
                    pw.println(c.getCanonicalName() + " " + PROBLEM_GENERATOR_TYPE);
                } else if (CorrectnessTester.class.isAssignableFrom(c)) {
                    pw.println(c.getCanonicalName() + " " + CORRECTNESS_TESTER_TYPE);
                } else if (MessageDelayer.class.isAssignableFrom(c)) {
                    pw.println(c.getCanonicalName() + " " + MESSAGE_DELAYER_TYPE);
                } else if (StatisticCollector.class.isAssignableFrom(c)) {
                    pw.println(c.getCanonicalName() + " " + STATISTIC_COLLECTOR_TYPE);
                } else if (Limiter.class.isAssignableFrom(c)) {
                    pw.println(c.getCanonicalName() + " " + LIMITER_TYPE);
                } else {
                    pw.println(c.getCanonicalName() + " " + UNKNOWN_TYPE);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(RTCodeScanner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        pw.close();
    }
    
}
