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
package bc.dsl;

import java.lang.String;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author bennyl
 */
public class ReflectionDSL {

    private static final Map<String, Class> PRIMITIVE_MAP = JavaDSL.<String, Class>cassoc(
            "float", Float.class,
            "int", Integer.class,
            "boolean", Boolean.class,
            "char", Character.class,
            "long", Long.class,
            "double", Double.class);

    public static Method methodWithAnnotation(Class c, Class<? extends Annotation> a) {
        for (Method m : c.getDeclaredMethods()) {

            if (m.isAnnotationPresent(a)) {
                m.setAccessible(true);
                return m;
            }
        }

        return null;
    }

    public static Set<Class> getClassGraph(Class c) {
        Set<Class> ret = new HashSet<Class>();
        LinkedList<Class> investigateQ = new LinkedList<Class>();
        investigateQ.add(c);
        while (!investigateQ.isEmpty()) {
            Class i = investigateQ.removeFirst();

            if (ret.contains(i)) {
                continue;
            }
            ret.add(i);

            Class[] interfaces = i.getInterfaces();
            Class superclass = i.getSuperclass();

            if (superclass != null && !ret.contains(superclass)) {
                investigateQ.add(superclass);
            }

            for (Class inter : interfaces) {
                if (!ret.contains(inter)) {
                    investigateQ.add(inter);
                }
            }
        }

        return ret;
    }

    public static List<Method> getAllMethodsWithAnnotation(Class aClass, Class<? extends Annotation> ano) {
        LinkedList<Method> ret = new LinkedList<Method>();
        for (Method m : aClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(ano)) {
                m.setAccessible(true);
                ret.add(m);
            }
        }

        return ret;
    }

    public static List<Field> getAllFieldsWithAnnotation(Class aClass, Class<? extends Annotation> ano) {
        LinkedList<Field> ret = new LinkedList<Field>();
        for (Field m : aClass.getDeclaredFields()) {
            if (m.isAnnotationPresent(ano)) {
                m.setAccessible(true);
                ret.add(m);
            }
        }

        return ret;
    }

    public static Method methodByName(Class aClass, String name) {
        LinkedList<Method> ret = new LinkedList<Method>();
        for (Method m : aClass.getDeclaredMethods()) {
            if (m.getName().equals(name)) {
                m.setAccessible(true);
                return m;
            }
        }

        return null;
    }

    public static Method methodByNameAndNArgs(Class aClass, String name, int nargs) {
        for (Method m : methodsByName(aClass, name)) {
            if (m.getParameterTypes().length == nargs) {
                return m;
            }
        }

        return null;
    }

    public static Method methodByNameAndArgs(Class aClass, String name, Class... args) {
        MAINLOOP:
        for (Method m : methodsByName(aClass, name)) {
            int i = 0;
            if (args.length == m.getParameterTypes().length) {

                for (Class p : m.getParameterTypes()) {
                    if (p != args[i]) {
                        continue MAINLOOP;
                    }
                }
                
                return m;
            }
        }

        return null;
    }

    public static List<Method> methodsByName(Class aClass, String name) {
        LinkedList<Method> ret = new LinkedList<Method>();
        for (Method m : aClass.getDeclaredMethods()) {
            if (m.getName().equals(name)) {
                m.setAccessible(true);
                ret.add(m);
            }
        }

        return ret;
    }
    
    public static <T> T valueOf(String s, Class<T> c) {
        if (String.class.isAssignableFrom(c)) {
            return (T) s;
        }

        if (c.isPrimitive()) {
            final String simpleName = c.getSimpleName();
            c = PRIMITIVE_MAP.get(simpleName);
            if (c == null) {
                System.err.println("NO PRIMITIVE FOUND WITH THE NAME " + simpleName);
                return null;
            }
        }

        Method m = methodByNameAndArgs(c, "valueOf", String.class);
        if (m == null) {
            System.err.println("cannot found value of " + c.getSimpleName());
            return null;
        }

        if (Modifier.isStatic(m.getModifiers())) {
            try {
                return (T) m.invoke(null, s);
            } catch (IllegalAccessException ex) {
//                Logger.getLogger(ReflectionDSL.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
//                Logger.getLogger(ReflectionDSL.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
//                Logger.getLogger(ReflectionDSL.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.err.println("cannot found value of " + c.getSimpleName());
            return null;

        } else {
            return null;
        }
    }
    
    
}
