/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.utils;

/**
 *
 * @author Administrator
 */
public class CodeUtils {
    
    /**
     * @param camel phrase in camel case
     * @return the same phrase split to words. 
     */
    public static String camelToWords(String camel){
        StringBuilder sb = new StringBuilder();
        for (char c : camel.toCharArray()){
            if (c >= 'A' && c <= 'Z') sb.append(" ").append((char) (c - 'A' + 'a'));
            else sb.append(c);
        }
        
        if (sb.charAt(0) == ' ') sb.deleteCharAt(0);
        return sb.toString();
    }
}
