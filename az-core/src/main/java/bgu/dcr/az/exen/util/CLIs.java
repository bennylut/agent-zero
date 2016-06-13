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
