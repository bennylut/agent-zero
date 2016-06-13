/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.exen.dtp;

import bgu.dcr.az.exen.util.BufferedByteReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Agent Zero Data transfer protocol - 
 * 1. the connection is persistent by default
 * 2. the protocol is fully asynchronous - when client received data from the server he never expect a response
 * 3. every request formatted like follows:
 * 
 * SOURCE_OF_INFORMATION INFORMATION_TYPE REQ_ID CRLF <- MESSAGE START
 * PARAM1-NAME: VALUE1 CRLF <- textual parameter
 * B[SIZE]: PARAM2-NAME CRLF <- defining binary parameter
 * BINARY DATA OF SIZE SIZE
 * CRLF <- EOF MESSAGE
 * 
 * 4. the server can answer back (but dont have to - it depends on the inner protocol)
 * 
 * optionally the protocol can define timeout for server response`then when the server
 * 
 * @author Administrator
 */
public class AzDTPMessage {

    private static String defaultInfoSource = null;
    private static AtomicInteger nextId; 
    
    private String infoSource = defaultInfoSource;
    private String infoType = null;
    private String id = null;
    private Map<String, String> stringFields = new HashMap<String, String>();
    private Map<String, byte[]> binaryFields = new HashMap<String, byte[]>();

    public static void setDefaultInfoSource(String defaultInfoSource) {
        AzDTPMessage.defaultInfoSource = defaultInfoSource;
    }

    public static String getDefaultInfoSource() {
        return defaultInfoSource;
    }
    
    public String getId() {
        return id;
    }

    public AzDTPMessage setId(String id) {
        this.id = id;
        return this;
    }

    public String getInfoSource() {
        return infoSource;
    }

    public AzDTPMessage setInfoSource(String informationSource) {
        this.infoSource = informationSource;
        return this;
    }

    public String getInfoType() {
        return infoType;
    }

    public AzDTPMessage setInfoType(String informationType) {
        this.infoType = informationType;
        return this;
    }

    public AzDTPMessage addField(String name, String value) {
        this.stringFields.put(name, value);
        return this;
    }

    public AzDTPMessage addField(String name, byte[] value) {
        this.binaryFields.put(name, value);
        return this;
    }

    public String getStringField(String name) {
        return stringFields.get(name);
    }

    public byte[] getBinaryField(String name) {
        return binaryFields.get(name);
    }
    private static final byte[] CRLF = {'\r', '\n'};
    private static final byte[] SPACE = {' '};

    private static byte[] strictReadUntil(BufferedByteReader bbr, byte[] until, String error) throws AzDTPScanException, IOException {
        byte[] temp = bbr.readUntil(until);
        if (temp == null) {
            throw new AzDTPScanException(error);
        }

        return temp;
    }

    public static AzDTPMessage scan(BufferedByteReader bbr) throws AzDTPScanException {
        try {
            if (bbr.isStreamClosed()){
                throw new AzDTPScanException("closed stream");
            }
            
            byte[] temp;
            byte[] kv;
            AzDTPMessage ret = new AzDTPMessage();

            //source of information
            temp = strictReadUntil(bbr, SPACE, "cannot read source of information");
            ret.setInfoSource(new String(temp).trim());

            //information type
            temp = strictReadUntil(bbr, SPACE, "cannot read information type");
            ret.setInfoType(new String(temp).trim());

            //request id
            temp = strictReadUntil(bbr, CRLF, "cannot read request id");
            ret.setId(new String(temp).trim());

            //param/binary
            while (true) {

                kv = strictReadUntil(bbr, CRLF, "cannot read Field Definition");
                if (kv.length <= CRLF.length) {
                    break;
                }

                String[] skv = new String(kv).split(": ");

                if (skv.length != 2) {
                    throw new AzDTPScanException("field not formatted currently - '" + new String(kv) + "'");
                }

                if (skv[0].startsWith("B[")) {
                    try {
                        int size = Integer.valueOf(skv[0].substring(2, skv[0].length() - 1));
                        temp = bbr.readAmount(size);
                        if (temp == null) {
                            throw new AzDTPScanException("stream closed while reading binary data of field: " + skv[1]);
                        } else {
                            ret.addField(skv[1].trim(), temp);
                        }
                    } catch (NumberFormatException ex) {
                        throw new AzDTPScanException("binary field not defined correctly - size is not integeric - '" + skv[0] + "'");
                    } catch (IndexOutOfBoundsException ex) {
                        throw new AzDTPScanException("binary field not defined correctly - size defenition curropted - '" + skv[0] + "'");
                    }
                } else {
                    ret.addField(skv[0].trim(), skv[1].trim());
                }
            }

            return ret;
        } catch (IOException ex) {
            throw new AzDTPScanException("IO problem while scanning - see cause", ex);
        }
    }
    
    public byte[] toBytes(){
        StringBuilder sb = new StringBuilder();
        sb.append(infoSource).append(" ").append(infoType).append(" ").append(id).append("\r\n");
        for (Entry<String, String> field : stringFields.entrySet()){
            sb.append(field.getKey()).append(": ").append(field.getValue()).append("\r\n");
        }
        
        int bsize = 2; //ending CRLF
        HashMap<String, byte[]> headers = new HashMap<String, byte[]>();
        for (Entry<String, byte[]> field : binaryFields.entrySet()){
            byte[] header = ("B[" + field.getValue().length + "]: " + field.getKey() + "\r\n").getBytes();
            bsize += header.length;
            bsize += field.getValue().length;
            headers.put(field.getKey(), header);
        }
        
        final byte[] topBytes = sb.toString().getBytes();
        byte[] ret = new byte[bsize + topBytes.length];
        int pos = topBytes.length;
        System.arraycopy(topBytes, 0, ret, 0, topBytes.length);
        for (Entry<String, byte[]> header : headers.entrySet()){
            System.arraycopy(header.getValue(), 0, ret, pos, header.getValue().length);
            pos += header.getValue().length;
            final byte[] data = binaryFields.get(header.getKey());
            System.arraycopy(data, 0, ret, pos, data.length);
            pos += data.length;
        }
        ret[pos++] = '\r';
        ret[pos] = '\n';
        
        return ret;
    }

    @Override
    public String toString() {
        return new String(toBytes());
    }

//    public static AzDTPMessage req(String infoType, String stringFields){
//        
//    }
    
    public static void main(String[] args) throws AzDTPScanException {
        String msg = "server info 123\r\n"
                + "bla: bli\r\n"
                + "field: value\r\n"
                + "B[3]: testb\r\n"
                + "123"
                + "\r\n";

        String msg2 = "server info 1214\r\n"
                + "bla: bli\r\n"
                + "field: value\r\n"
                + "\r\n";
        
        String msg3 = "server info 123\r\n"
                + "bla: bli\r\n"
                + "field: value\r\n"
                + "B[3]: testb\r\n"
                + "123"
                + "B[4]: testb2\r\n"
                + "1234"
                + "\r\n";
        ByteArrayInputStream in = new ByteArrayInputStream((msg+msg2+msg3).getBytes());
        final BufferedByteReader buf = new BufferedByteReader(in);
        AzDTPMessage m = scan(buf);
        m = scan(buf);
        m = scan(buf);
        System.out.println("last is:");
        System.out.println(m.toString());
    }
}
