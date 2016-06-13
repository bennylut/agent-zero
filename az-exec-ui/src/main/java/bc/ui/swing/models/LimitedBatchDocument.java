/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.ui.swing.models;

import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;

/**
 *
 * @author bennyl
 */
public class LimitedBatchDocument extends BatchDocument{
    
    int maxDocSize;
    int minDelSize;

    public LimitedBatchDocument(JTextPane container) {
        super(container);
        this.maxDocSize = 2000000;
        this.minDelSize = 200000;
    }

    public LimitedBatchDocument(int maxDocSize, int minDelSize, JTextPane container) {
        super(container);
        this.maxDocSize = maxDocSize;
        this.minDelSize = minDelSize;
    }

    @Override
    public synchronized void processBatchUpdates() throws BadLocationException {
        super.processBatchUpdates();
        int len = this.getLength() ; 
        final int delta = len - maxDocSize;
        int toDelete = (delta>0? Math.max(delta, minDelSize): 0);

        this.remove(0, toDelete);
    }
    
}
