package bgu.dcr.az.api;

import bgu.dcr.az.api.exp.DeepCopyFailedException;
import bgu.dcr.az.utils.DeepCopyUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A base class for SimpleMessage and AbstractMessage it defines the most basic features that a message should have (a sender, a name and some more staff). 
 * Messages can contain metadata that can be used for passing data about the message like timestamp, statistics etc. – some metadata is filled automatically by the simulator and some can be added by the algorithm.
 * @author bennyl
 */
public class Message implements Serializable {

    /**
     * the message not contains the recepient in its fields this field is a metadata of the message 
     * and it is accessable via this key
     */
    public static final String RECEPIENT_METADATA = "RECEPIENTS";
    /**
     * TODO - > it will be faster if it is a field or an argument to the mailer.. 
     */
    public static final String RECEPIENT_TYPE_METADATA = "RECEPIENT TYPE";
    private String name; //the message name (= type)
    private int from; //the sender of the message
    /**
     * the attached metadata for the message 
     * you can use it to send any kind of data that is not part of the message fields 
     * think about it at the content on the envelop of the message - you may want to write the timestamp there etc.
     */
    private Map<String, Object> metadata;
    /**
     * collection of the message arguments 
     * the arguments are unnamed -> TODO: FIGUREOUT A WAY TO NAME THEM FOR DEBUGGING PORPUSE -> MAYBE COMPILE TIME PROCESSING USING APT.. 
     */
    private Object[] args;

    /**
     * @param name the message name / type
     * @param from the agent sending this message
     */
    public Message(String name, int from, Object[] args) {
        this.name = name;
        this.from = from;
        this.metadata = new HashMap<String, Object>();
        this.args = args;
    }

    /**
     * @return the arguments of this message
     * the args are ordered at the same way that the sender sent them / the receiver got them
     * which means that if you sent the mesasge using:
     * send("message", a, b, c).to(x) 
     * message.getArgs[0] = a
     * message.getArgs[0] = b
     * message.getArgs[0] = c
     * 
     * aside from arguments message can also contain metadata accesible via {@link getMetadata()}
     */
    public Object[] getArgs() {
        return args;
    }

    public Message copy() {
        Object[] cargs = new Object[this.args.length];

        for (int i = 0; i < args.length; i++) {
            Object a = args[i];
            if (a instanceof DeepCopyable) {
                cargs[i] = ((DeepCopyable) a).deepCopy();
            } else {
                try {
                    cargs[i] = DeepCopyUtil.deepCopy(a);
                } catch (Exception ex) {
                    if (ex instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                        Agt0DSL.throwUncheked(ex);
                    } else {
                        throw new DeepCopyFailedException("Cannot figure out how to deep copy " + a.getClass().getName() + " class please implement the DeepCopyable interface on this class.", ex);
                    }
                }
            }
        }


        Message ret = new Message(getName(), getSender(), cargs);
        ret.metadata = new HashMap<String, Object>(metadata); //metadata is not deep-copyed as it should be immutable
        return ret;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Object a : args) {
            sb.append(a.toString()).append(", ");
        }
        return "[" + getName() + (args.length > 0 ? ": " + sb.deleteCharAt(sb.length() - 2).toString() + "]" : "]");
        
    }

    /**
     * @return the name of this message (can be reffered as type)
     */
    public String getName() {
        return name;
    }

    /**
     * @return who is sending this message
     */
    public int getSender() {
        return from;
    }

    /**
     * set the sender
     * @param from
     */
    protected void setFrom(int from) {
        this.from = from;
    }

    /**
     * messages can have metadata attached to them - metadata is collection of key to immutable value pairs
     * metadata values assume to be immutable and are not getting deep copied upon sending along with the normal message fields
     * instaed it will get copied by reference to the new message object.
     * please take that into consideration if you going to use it
     * @return the metadata attached to this object (as a key value map)
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * sets the name of this message
     * @param name
     */
    protected void setName(String name) {
        this.name = name;
    }
}
