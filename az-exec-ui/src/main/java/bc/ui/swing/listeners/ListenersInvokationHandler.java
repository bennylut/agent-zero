/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.ui.swing.listeners;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author bennyl
 */
public class ListenersInvokationHandler<T> implements InvocationHandler, Listeners<T> {

    //PRE CACHED METHODS:
    private static Method addListenerMethod;
    private static Method removeListenerMethod;
    private static Method fireMethod;

    static {
        try {
            addListenerMethod = Listeners.class.getMethod("addListener", Object.class);
            removeListenerMethod = Listeners.class.getMethod("removeListener", Object.class);
            fireMethod = Listeners.class.getMethod("fire");
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }
    List registeredListeners = new LinkedList();

    private ListenersInvokationHandler() {
        //NEVER INSTANTIATE DIRECTLY!
    }

    public static <T> Listeners<T> newInstance(Class<T> listenerType) {
        return (Listeners<T>) java.lang.reflect.Proxy.newProxyInstance(
                ClassLoader.getSystemClassLoader(),
                new Class[]{Listeners.class, listenerType},
                new ListenersInvokationHandler());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.equals(addListenerMethod)){
            addListener((T)args[0]);
        }else if (method.equals(removeListenerMethod)){
            removeListener((T)args[0]);
        }else if (method.equals(fireMethod)){
            return proxy;
        }else {
            for (Object l : registeredListeners){
                method.invoke(l, args); 
            }
        }
        
        return null;
    }

    @Override
    public void addListener(T listener) {
        registeredListeners.add(listener);
    }

    @Override
    public void removeListener(T listener) {
        removeListener(listener);
    }

    @Override
    public T fire() {
        return (T) this;
    }
}
