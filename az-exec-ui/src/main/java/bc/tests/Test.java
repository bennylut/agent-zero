/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.tests;

import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.dev.Agent0Tester;
import bgu.dcr.az.exen.cli.AmZRun;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author bennyl
 */
public class Test {

    static File exp = new File("C:\\Users\\User\\workspace_az\\SBT\\test.xml");
    
    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {
        Agent0Tester.main(new String[]{"-f", exp.getAbsolutePath(), "--sfp", "problems"});
    }
}