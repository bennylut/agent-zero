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
package bgu.dcr.az.api.exen.escan;

import bgu.dcr.az.api.ano.Variable;
import bgu.dcr.az.api.exp.InternalErrorException;
import bgu.dcr.az.utils.ReflectionUtil;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public class VariableMetadata {

    private String name;
    private String description;
    private Object currentValue;
    private Class type;
    private Field field;

    public VariableMetadata(String name, String description, Object defaultValue, Class type, Field field) {
        this.name = name;
        this.description = description;
        this.currentValue = defaultValue;
        this.type = type;
        this.field = field;
    }

    public Object getCurrentValue() {
        return currentValue;
    }

    public void updateValue(Object obj) {
        try {
            currentValue = field.get(obj);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(VariableMetadata.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(VariableMetadata.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }

    /**
     * if there is a field in the given object that annotated with Variable annotation
     * and the annotation name function is the same as 'var' then this function will try to assign 
     * it with the given value 
     * 
     * if no such field - nothing will happend (aside of some wasted cpu cycles :) )
     * @param obj
     * @param var
     * @param value 
     */
    public static void tryAssign(Object obj, String var, Object value) {
        Variable ano;
        try {
            for (Field field : ReflectionUtil.getRecursivelyFieldsWithAnnotation(obj.getClass(), Variable.class)) {
                ano = field.getAnnotation(Variable.class);
                if (ano.name().equals(var)) {
                    field.set(obj, ReflectionUtil.tryAdapt(value, field.getType()));
                }
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(VariableMetadata.class.getName()).log(Level.SEVERE, null, ex);
            throw new InternalErrorException();
        } catch (IllegalAccessException ex) {
            Logger.getLogger(VariableMetadata.class.getName()).log(Level.SEVERE, null, ex);
            throw new InternalErrorException();
        }

    }

    public static void assign(Object obj, Map<String, Object> variables) {
        try {
            for (Field field : ReflectionUtil.getRecursivelyFieldsWithAnnotation(obj.getClass(), Variable.class)) {
                Object val = variables.get(field.getAnnotation(Variable.class).name());
                
                if (val != null && !field.getType().isAssignableFrom(val.getClass())){
                    val = ReflectionUtil.valueOf(val.toString(), field.getType());
                }
                
                if (val != null ) {
                    field.set(obj, val);
                } 
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(VariableMetadata.class.getName()).log(Level.SEVERE, null, ex);
            throw new InternalErrorException();
        } catch (IllegalAccessException ex) {
            Logger.getLogger(VariableMetadata.class.getName()).log(Level.SEVERE, null, ex);
            throw new InternalErrorException();
        }

    }

    public static Map<String, VariableMetadata> map(Object from) {
        VariableMetadata[] s = scan(from);
        HashMap<String, VariableMetadata> ret = new HashMap<String, VariableMetadata>();
        for (VariableMetadata v : s) {
            ret.put(v.getName(), v);
        }

        return ret;
    }

    public static VariableMetadata[] scan(Class c) {
        List<Field> fields = ReflectionUtil.getRecursivelyFieldsWithAnnotation(c, Variable.class);
        VariableMetadata[] ret = new VariableMetadata[fields.size()];
        int i = 0;

        try {
            for (Field field : fields) {
                Variable v = field.getAnnotation(Variable.class);
                Object defaultValue = ReflectionUtil.valueOf(v.defaultValue(), field.getType());
                Class type = field.getType();

                VariableMetadata metadata = new VariableMetadata(v.name(), v.description(), defaultValue, type, field);
                ret[i++] = metadata;
            }

            return ret;
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(VariableMetadata.class.getName()).log(Level.SEVERE, null, ex);
            throw new InternalErrorException("problem reading variables of class: " + c.getSimpleName(), ex);
        }
    }

    public static VariableMetadata[] scan(Object from) {
        VariableMetadata[] temp = scan(from.getClass());
        for (VariableMetadata t : temp) {
            t.updateValue(from);
        }

        return temp;
    }
}
