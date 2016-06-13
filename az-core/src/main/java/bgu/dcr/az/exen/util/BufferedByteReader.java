/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
