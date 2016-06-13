/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen;

/**
 *
 * @author bennyl
 */
public interface LogListener {
    void onLog(int agent, String mailGroupKey, String log);
}
