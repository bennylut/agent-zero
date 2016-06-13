/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.stat;

import bgu.dcr.az.api.Message;

/**
 *
 * @author Administrator
 */
public class NCSCToken {
    private long value = 0;

    public void setValue(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }
    
    public void increaseValue(long with){
        value += with;
    }
    
    public static NCSCToken extract(Message m){
        NCSCToken ret = (NCSCToken) m.getMetadata().get("ncsc");
        if (ret == null){
            ret = new NCSCToken();
            m.getMetadata().put("ncsc", ret);
        }
        
        return ret;
    }
}
