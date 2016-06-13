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
