/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.sync;

import bgu.dcr.az.api.exen.MessageQueue;
import bgu.dcr.az.api.exen.SystemClock;
import bgu.dcr.az.exen.AbstractMailer;

/**
 *
 * @author Inna
 */
public class SyncMailer extends AbstractMailer{
    SystemClock clock;

    public void setClock(SystemClock clock) {
        this.clock = clock;
    }

    @Override
    protected MessageQueue generateNewMessageQueue(int agent, String groupId) {
       return new DoubleMessageQueue(clock);
    }

}
