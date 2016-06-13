/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.escan;

import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.ano.Variable;

/**
 *
 * @author bennyl
 */
@Register(name = "assign")
public class VarAssign {

    @Variable(name = "var", description = "the variable name", defaultValue="")
    String varName;
    @Variable(name = "val", description = "the variable value to assign", defaultValue="")
    String value;

    public VarAssign(String varName, String value) {
        this.varName = varName;
        this.value = value;
    }

    public VarAssign() {
    }

    public String getVarName() {
        return varName;
    }

    public String getValue() {
        return value;
    }
    
}
