/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bgu.dcr.az.api.exen;

import bgu.dcr.az.api.Message;

/**
 *
 * @author User
 */
public interface NonBlockingMessageQueue {

    void add(Message e);

    int availableMessages();

    boolean isEmpty();

    Message take() throws InterruptedException;
    
}
