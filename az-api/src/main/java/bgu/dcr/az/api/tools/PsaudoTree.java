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
package bgu.dcr.az.api.tools;

import bgu.dcr.az.api.Agent;
import java.util.List;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public interface PsaudoTree {

    public List<Integer> getChildren();

    public List<Integer> getPsaudoChildren();

    public Integer getParent();

    public List<Integer> getPsaudoParents();
    
    public List<Integer> getDescendants();

    public boolean isRoot();

    public List<Integer> getPseudoParentDepths();
    
    public boolean isLeaf();
    
    public int getDepth();


    /**
     * the separator of xi: the set of ancestors of xi which are directly connected
     * through an edge with xi or with descendants of xi.
     */
    public Set<Integer> getSeperator();

    public static class NotConnectivityGraphException extends RuntimeException {

        public NotConnectivityGraphException(Throwable cause) {
            super(cause);
        }

        public NotConnectivityGraphException(String message, Throwable cause) {
            super(message, cause);
        }

        public NotConnectivityGraphException(String message) {
            super(message);
        }

        public NotConnectivityGraphException() {
        }
    }
}
