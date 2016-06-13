/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api;

/**
 * implement this interface to make sending this object via messages fast
 * the way current message sending procedure works is as follows
 * 
 * we are separating between message arguments and message user arguments :
 * message arguments are the basic message fields : name, metadata, sender etc.
 * message user arguments are the arguments that the user send on top of the message (like cpa, ub etc.)
 * the message is being duplicated and then passed to the recipient - mimicing real network
 * now all the message arguments are copied by hand - very fast..
 * message user arguments are tested to see if they implements DeepCopyable interface 
 * if so they also copied by hand (via the deepCopy method)
 * if not we use a generic Deep Copy framework to copy them - can be preaty slow but avg speed is ok..
 * @author bennyl
 */
public interface DeepCopyable{

    /**
     * @return an exact copy of the object with no shared fields or sub fields (unless it immuteable)
     */
    Object deepCopy();
}
