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

import java.util.ArrayList;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.JTextPane;
import javax.swing.text.Element;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

/**
 * DefaultDocument subclass that supports batching inserts.
 */
public class BatchDocument extends DefaultStyledDocument {

    private final DefaultStyledDocument SWAP_DOCUMENT = new DefaultStyledDocument();
    
    /**
     * EOL tag that we re-use when creating ElementSpecs
     */
    private static final char[] EOL_ARRAY = {'\n'};
    private int batchSize = 0;
    private boolean start = false;
    private JTextPane container;
    
    /**
     * Batched ElementSpecs
     */
    //private ArrayList batch = null;
    private LinkedBlockingQueue batch;

    public BatchDocument(JTextPane container) {
        //batch = new ArrayList();
        batch = new LinkedBlockingQueue();
        this.container = container;
    }

    
    
    public void insertln(String str, AttributeSet a){
        a = a == null ? a : a.copyAttributes();
        char[] chars = str.toCharArray();
        batch.add(new ElementSpec(
                a, ElementSpec.ContentType, chars, 0, str.length()));

        batchSize += str.length();
        
        batch.add(new ElementSpec(
                a, ElementSpec.ContentType, EOL_ARRAY, 0, 1));
        Element paragraph = getParagraphElement(0);
        AttributeSet pattr = paragraph.getAttributes();
        batch.add(new ElementSpec(null, ElementSpec.EndTagType));
        batch.add(new ElementSpec(pattr, ElementSpec.StartTagType));
        batchSize += EOL_ARRAY.length;

    }
    
    public synchronized void appendBatchString(String str,
            AttributeSet a){
        
        
        if (str.isEmpty()) return;
        
        
        
        int idx = -1;
        while (!str.isEmpty()) {
            idx = str.indexOf("\n");
            if (idx >= 0){
                String line = str.substring(0, idx);
                _appendBatchString(line, a);
                appendBatchLineFeed(null);
                str = str.substring(idx+1);
            }else {
                _appendBatchString(str, a);
                str = "";
            }
        }
        
    }
            
            
    /**
     * Adds a String (assumed to not contain linefeeds) for
     * later batch insertion.
     */
    protected synchronized void _appendBatchString(String str,
            AttributeSet a) {
        
        // We could synchronize this if multiple threads
        // would be in here. Since we're trying to boost speed,
        // we'll leave it off for now.

        // Make a copy of the attributes, since we will hang onto
        // them indefinitely and the caller might change them
        // before they are processed.
        
        a = a == null ? a : a.copyAttributes();
        char[] chars = str.toCharArray();
        batch.add(new ElementSpec(
                a, ElementSpec.ContentType, chars, 0, str.length()));

        batchSize += str.length();
        
    }

    /**
     * Adds a linefeed for later batch processing
     */
    public synchronized void appendBatchLineFeed(AttributeSet a) {
        // See sync notes above. In the interest of speed, this
        // isn't synchronized.

        // Add a spec with the linefeed characters
        batch.add(new ElementSpec(a, ElementSpec.ContentType, EOL_ARRAY, 0, 1));
        

        // Then add attributes for element start/end tags. Ideally
        // we'd get the attributes for the current position, but we
        // don't know what those are yet if we have unprocessed
        // batch inserts. Alternatives would be to get the last
        // paragraph element (instead of the first), or to process
        // any batch changes when a linefeed is inserted.
        Element paragraph = getParagraphElement(0);
        AttributeSet pattr = paragraph.getAttributes();
        batch.add(new ElementSpec(null, ElementSpec.EndTagType));
        batch.add(new ElementSpec(pattr, ElementSpec.StartTagType));
        batchSize += EOL_ARRAY.length;
    }

//    public void processBatchUpdates(int offs) throws
    public synchronized void processBatchUpdates() throws
            BadLocationException {
        // As with insertBatchString, this could be synchronized if
        // there was a chance multiple threads would be in here.
        //ElementSpec[] inserts = new ElementSpec[batch.size()];
        //batch.toArray(inserts);
//        container.setDocument(SWAP_DOCUMENT);
        this.appendBatchLineFeed(null);
        ArrayList<ElementSpec> l = new ArrayList<ElementSpec>(100);
        batchSize = 0;
        batch.drainTo(l);

        ElementSpec[] inserts = l.toArray(new ElementSpec[0]);
        // Process all of the inserts in bulk
//        super.insert(offs, inserts);
        super.insert(this.getLength() + 1, inserts);
        container.setDocument(this);
    }

    public int getBatchSize() {
        return batchSize;
    }
}
