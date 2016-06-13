/* 
 * The MIT License
 *
 * Copyright 2016 Benny Lutati.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
