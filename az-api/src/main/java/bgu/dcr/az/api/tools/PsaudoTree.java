/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
