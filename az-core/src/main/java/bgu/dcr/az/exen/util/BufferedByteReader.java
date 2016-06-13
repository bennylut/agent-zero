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

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Administrator
 */
public class BufferedByteReader {

    CircularByteBuffer buffer = new CircularByteBuffer(1024 * 256);
    boolean streamClosed = false;
    InputStream in;

    public BufferedByteReader(InputStream in) {
        this.in = in;
    }

    /**
     * continue to read until the given sequence is found 
     * will return array containing all the read bytes (including the given sequence)
     */
    public byte[] readUntil(byte[] until) throws IOException {
        if (streamClosed) return null;
        
        CircularByteBuffer dump = new CircularByteBuffer(256);
        LinkedList<byte[]> ret = new LinkedList<byte[]>();
        int matched = 0;
        byte b;
        
        while (matched < until.length) {
            if (buffer.isEmpty()) {
                if (!buffer()) {
                    break;
                }
            } else {
                if (dump.isFull()) {
                    ret.add(dump.dump());
                }
                b = buffer.next();
                dump.insert(b);
                if (b == until[matched]) {
                    matched++;
                } else {
                    matched = 0;
                }
            }
        }

        ret.add(dump.dump());
        return squeeze(ret);
    }

    private boolean buffer() throws IOException {
        if (buffer.readFrom(in) == -1) {
            streamClosed = true;
            return false;
        }
        return true;
    }

    public boolean isStreamClosed() {
        return streamClosed;
    }

    private byte[] squeeze(List<byte[]> ret) {
        int size = 0;
        for (byte[] b : ret) {
            size += b.length;
        }

        byte[] out = new byte[size];
        int offset = 0;
        for (byte[] b : ret) {
            System.arraycopy(b, 0, out, offset, b.length);
            offset += b.length;
        }

        return out;
    }

    /**
     * read the given amount of bytes before return.
     */
    public byte[] readAmount(int amount) throws IOException {
        if (streamClosed) return null;
        
        byte[] ret = new byte[amount];
        int pos = 0;
        while (pos < amount) {
            if (buffer.isEmpty()) {
                if (!buffer()) {
                    byte[] _ret = new byte[pos];
                    System.arraycopy(ret, 0, _ret, 0, pos);
                    return _ret;
                }
            } else {
                ret[pos++] = buffer.next();
            }
        }

        return ret;
    }
}
