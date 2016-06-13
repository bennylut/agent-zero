/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.api.prob.rdcop;

import bgu.dcr.az.api.prob.ImmutableProblem;

/**
 *
 * @author bennyl
 */
public interface PossibleProblem extends ImmutableProblem{
    double getProbability();
}
