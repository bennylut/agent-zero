/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.api.exen.stat;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bennyl
 */
public interface VisualModel {
    String getTitle();
    void exportToCSV(File csv);
    List<String> getAlgorithms();
    String getDomainAxisLabel();
    String getRangeAxisLabel();
    Map getValues(String algorithm);
}
