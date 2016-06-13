/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
