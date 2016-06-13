/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.runner;

import bgu.dcr.az.dev.ExperimentExecutionController;
import java.io.File;

/**
 *
 * @author bennyl
 */
public class RunExecutionUI {

    public static void main(String[] args) throws InterruptedException {
        ExperimentExecutionController.UNIT.run(new File("test.xml"), true, false);
    }
}
