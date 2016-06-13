/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.dtp;

/**
 *
 * @author Administrator
 */
public interface Handler {
    public void handle(Client c, AzDTPMessage message);
}
