package bgu.dcr.az.texen.base;

import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.exen.NonBlockingMessageQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author User
 */
public class SimpleMessageQueue implements NonBlockingMessageQueue {
    private final ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<>();
    
    @Override
    public void add(Message e) {
        queue.add(e);
    }

    @Override
    public int availableMessages() {        
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public Message take() throws InterruptedException {
        return queue.poll();
    }

}
