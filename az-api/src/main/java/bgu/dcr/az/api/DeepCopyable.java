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
