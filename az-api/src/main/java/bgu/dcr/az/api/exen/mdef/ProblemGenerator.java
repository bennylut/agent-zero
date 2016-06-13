/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.mdef;

import bgu.dcr.az.api.prob.Problem;
import java.util.Random;

/**
 *
 * @author bennyl
 */
public interface ProblemGenerator {
    
    void generate(Problem p, Random rand);
}
