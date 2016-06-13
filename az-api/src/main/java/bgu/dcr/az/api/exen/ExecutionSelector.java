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
package bgu.dcr.az.api.exen;

import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;

/**
 * this class is used to specify a single execution selection 
 * when an experiment contains this element it will execute only the specific selected execution
 * and then finish
 * @author bennyl
 */
@Register(name = "execution-selector")
public class ExecutionSelector {
    @Variable(name="test-name", description="selected test", defaultValue="unnamed")
    String testName = "";
    @Variable(name="algorithm-name", description="selected algorithm", defaultValue="unnamed")
    String algName = "";
    //TODO - why is it needed?
    @Variable(name="name", description="identifier for this selector info", defaultValue="unnamed")
    String name = ""+ System.currentTimeMillis();
    @Variable(name="prob-number", description="number of the failing problem", defaultValue="-1")
    int pnumber = -1;
    @Variable(name="exec-number", description="the number of the failing execution", defaultValue="-1")
    int enumber = -1;
    @Variable(name="run-var", description="the run-var value of the execution", defaultValue="-1")
    double runvar = -1;
    
    public ExecutionSelector(String testName, String algName, int number, int enumber, double runVar) {
        this.testName = testName;
        this.algName = algName;
        this.pnumber = number;
        this.enumber = enumber;
        this.runvar = runVar;
    }

    public ExecutionSelector() {
    }
    
    public String getSelectedTest(){
        return testName;
    }
    public String getSelectedAlgorithmInstanceName(){
        return algName;
    }
    
    public int getSelectedProblemNumber(){
        return pnumber;
    }
    
    public String getName() {
        return name;
    }

    public double getRunvar() {
        return runvar;
    }

    
    @Override
    public String toString() {
        return "" + testName + "," + algName + "," + pnumber + "," + enumber + "," + runvar;
    }
    
    public int getExecutionNumber(){
        return enumber;
    }
    
}
