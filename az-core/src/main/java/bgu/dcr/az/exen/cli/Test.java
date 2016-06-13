/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.cli;

import bgu.dcr.az.api.Agt0DSL;

/**
 *
 * @author Administrator
 */
public class Test extends Agt0DSL{
    public static void main(String[] args){
        //AZLS.main(array("-rf", "algos.csv"));
        AZEXE.main(array("-e", "exp-alon.xml", "-rdir", "results", "-id", "test-exp-id"));
    }
}
