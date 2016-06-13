/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package bgu.dcr.az.exen.async;

import bgu.dcr.az.exen.AbstractMailer;
import bgu.dcr.az.exen.DefaultMessageQueue;

/**
 *
 * @author bennyl
 */
public class AsyncMailer extends AbstractMailer {
   
    @Override
    protected DefaultMessageQueue generateNewMessageQueue(int agent, String groupKey) {
        return new DefaultMessageQueue();
    }
}
