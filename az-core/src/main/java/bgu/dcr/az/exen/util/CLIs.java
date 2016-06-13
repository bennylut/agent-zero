/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.util;

import org.kohsuke.args4j.CmdLineParser;

/**
 *
 * @author Administrator
 */
public class CLIs {

    public static void parseArgs(Object command, String[] args) {
        CmdLineParser parser = new CmdLineParser(command);
        try {
            parser.parseArgument(args);
        } catch (Exception ex) {
            System.err.print("Error: " + ex.getMessage() + "\n\n");

            System.err.print("usage: ");
            parser.setUsageWidth(80);
            parser.printSingleLineUsage(System.err);
            System.err.print("\nDescription:\n");
            parser.printUsage(System.err);

            ex.printStackTrace();
            System.exit(42);
        }
    }

    public static void scream(String scream, Exception ex) {
        System.out.println(scream + " - " + ex.getMessage());
        if (ex != null) ex.printStackTrace();
        System.exit(42);
    }
}
