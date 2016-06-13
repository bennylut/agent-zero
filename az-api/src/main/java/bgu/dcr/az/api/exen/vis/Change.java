/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.vis;

/**
 *
 * @author Administrator
 */
public interface Change<T> {
    void mergeWith(Change<T> change);
    T reduce(T prev, int frameNumber);
}
