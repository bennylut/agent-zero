/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.utils;

import bgu.dcr.az.api.Agt0DSL;
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
public class ReflectionUtil {

    private static final Map<String, Class> PRIMITIVE_MAP = Agt0DSL.<String, Class>cassoc(
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

    public static List<Field> getRecursivelyFieldsWithAnnotation(Class aClass, Class<? extends Annotation> ano) {
        LinkedList<Field> ret = new LinkedList<Field>();
        while (true) {
            List<Field> l = getAllFieldsWithAnnotation(aClass, ano);
            ret.addAll(l);
            if (aClass == Object.class) {
                return ret;
            } else {
                aClass = aClass.getSuperclass();
            }
        }
    }

    public static List<Method> getRecursivelyMethodsWithAnnotation(Class aClass, Class<? extends Annotation> ano) {
        LinkedList<Method> ret = new LinkedList<Method>();
        while (true) {
            List<Method> l = getAllMethodsWithAnnotation(aClass, ano);
            ret.addAll(l);
            if (aClass == Object.class) {
                return ret;
            } else {
                aClass = aClass.getSuperclass();
            }
        }
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

    public static Object tryAdapt(Object what, Class to) {
        if (to.isAssignableFrom(what.getClass())) {
            return what;
        }

        if (to.isPrimitive()) {
            to = PRIMITIVE_MAP.get(to.getSimpleName());
            if (to.isAssignableFrom(what.getClass())) {
                return what;
            }
        }

        if (what instanceof Number && Integer.class.isAssignableFrom(to)) {
            return ((Number) what).intValue();
        }
        return valueOf("" + what, to);
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

        Method m = null;
        try {
            m = c.getMethod("valueOf", String.class);
        } catch (NoSuchMethodException ex) {
//            Logger.getLogger(ReflectionUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
//            Logger.getLogger(ReflectionUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

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
