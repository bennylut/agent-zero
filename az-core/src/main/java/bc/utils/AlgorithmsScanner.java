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
