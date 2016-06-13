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
