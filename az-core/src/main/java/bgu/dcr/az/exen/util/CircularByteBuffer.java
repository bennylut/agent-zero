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
package bgu.dcr.az.exen.util;

import bgu.dcr.az.exen.exp.EmptyContainerException;
import bgu.dcr.az.exen.exp.FullContainerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Administrator
 */
public class CircularByteBuffer {
    int start; 
    int taken = 0;
    byte[] buffer;

    public CircularByteBuffer(int capacity) {
        buffer = new byte[capacity];
        reset();
    }
    
    public void reset(){
        start = 0;
        taken = 0;
    }
    
    /**
     * read from the given input stream into this buffer - this method can block.
     * ** notice that there is no guarantee that the buffer will successfully read any data!
     * @param in
     * @return the number of bytes read or -1 if eos reached
     * @throws IOException 
     */
    public int readFrom(InputStream in) throws IOException{
        if (isFull()) return 0;
        boolean fliped = isFlipped();
        int r;
        if (fliped){
            int s = (start + taken)%buffer.length;
            r = in.read(buffer, s, start - s);
        }else {
            r = in.read(buffer, start + taken, buffer.length - start - taken);
        }
        
        taken += (r > 0? r: 0);
        return r;
    }

    /**
     * @return true if this buffer is in flipped state (mean the start is after the end)
     */
    private boolean isFlipped() {
        return start + taken > buffer.length;
    }
    
    
    /**
     * @return true if this buffer is empty
     */
    public boolean isEmpty(){
        return taken == 0;
    }
    
    /**
     * @return the amount of bytes that can be inserted into this buffer
     */
    public int freeSpace(){
        return buffer.length - taken;
    }
    
    /**
     * @return the number of bytes that can be read from this buffer
     */
    public int taken(){
        return taken;
    }
    
    public int maximumCapacity(){
        return buffer.length;
    }
    
    public boolean isFull(){
        return freeSpace() == 0;
    }
    
    /**
     * if this buffer is empty will throw EmptyContainerException
     * @return 
     */
    public byte next() throws EmptyContainerException{
        if (isEmpty()) throw new EmptyContainerException();
        byte n = buffer[start];
        start = (start + 1) % buffer.length;
        taken--;
        return n;
    }
    
    /**
     * if container is full will throw FullContainerException
     * @param b 
     */
    public void insert(byte b) throws FullContainerException{
        if (isFull()) throw new FullContainerException();
        buffer[(start+taken)%buffer.length] = b;
        taken ++;
    }
    
    /**
     * copy  the given buffer into an array and return it - the buffer is then being reseted
     * @return 
     */
    public byte[] dump(){
        byte[] ret = new byte[taken];
        if (isFlipped()){
            System.arraycopy(buffer, start, ret, 0, buffer.length - start);
            System.arraycopy(buffer, 0, ret, buffer.length - start, taken - buffer.length + start);
        }else {
            System.arraycopy(buffer, start, ret, 0, taken);
        }
        
        reset();
        return ret;
    }
    
    public static void main(String[] args) throws IOException{
        CircularByteBuffer cbb = new CircularByteBuffer(3);
        System.out.println("Testing 3 insert");
        cbb.insert((byte)1);
        cbb.insert((byte)2);
        cbb.insert((byte)3);
        System.out.println("Testing 3 next");
        assertTrue(cbb.isFull(), "container should be full but is not!");
        assertTrue(cbb.next() == 1, "container return wrong data");
        assertTrue(!cbb.isFull(), "container should not be full but is!");
        assertTrue(cbb.next() == 2, "container return wrong data");
        assertTrue(cbb.next() == 3, "container return wrong data");
        System.out.println("Testing circulaity");
        cbb.insert((byte)1);
        cbb.insert((byte)2);
        assertTrue(cbb.next() == 1, "container return wrong data");
        cbb.insert((byte)3);
        cbb.insert((byte)4);
        assertTrue(cbb.next() == 2, "container return wrong data");
        assertTrue(cbb.next() == 3, "container return wrong data");
        assertTrue(cbb.next() == 4, "container return wrong data");
        System.out.println("Testing stream read");
        ByteArrayInputStream bout = new ByteArrayInputStream(new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20});
        cbb.readFrom(bout);
        assertTrue(cbb.next() == 1, "container return wrong data");
        assertTrue(cbb.next() == 2, "container return wrong data");
        cbb.readFrom(bout);
        assertTrue(cbb.next() == 3, "container return wrong data");
        assertTrue(cbb.next() == 4, "container return wrong data");
        assertTrue(cbb.next() == 5, "container return wrong data");
        cbb.readFrom(bout);
        assertTrue(cbb.next() == 6, "container return wrong data");
        cbb.readFrom(bout);
        assertTrue(cbb.next() == 7, "container return wrong data");
        assertTrue(cbb.next() == 8, "container return wrong data");
        cbb.readFrom(bout);
        cbb.readFrom(bout);
        System.out.println("Testing dump");
        byte[] a = cbb.dump();
        assertTrue(a[0]==9 && a[1]==10 && a[2]==11, "container return wrong data");
        cbb.insert((byte)1);
        cbb.insert((byte)2);
        a = cbb.dump();
        assertTrue(a.length == 2 && a[0]==1 && a[1]==2, "container return wrong data");
        cbb.insert((byte)1);
        cbb.insert((byte)2);
        cbb.next();
        cbb.insert((byte)3);
        cbb.insert((byte)4);
        a = cbb.dump();
        assertTrue(a.length == 3 && a[0]==2 && a[1]==3 && a[2]==4, "container return wrong data");
    }
    
    private static void assertTrue(boolean what, String ex){
        if (!what) throw new RuntimeException(ex);
    }
    
}
