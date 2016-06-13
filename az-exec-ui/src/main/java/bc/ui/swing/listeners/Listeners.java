/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.ui.swing.listeners;

/**
 *
 * @author bennyl
 */
public interface Listeners<T> {
    public void addListener(T listener);
    public void removeListener(T listener);
    public T fire();
    
    public static class Builder{
        public static <T> Listeners<T> newInstance(Class<T> type){
            return ListenersInvokationHandler.newInstance(type);
        }
    }
}
