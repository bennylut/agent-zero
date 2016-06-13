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
package bgu.dcr.az.api.exen.stat.vmod;

import bgu.dcr.az.api.exen.stat.VisualModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bennyl
 */
public class LineVisualModel implements VisualModel {

    String xAxisName;
    String yAxisName;
    String title;
    Map<String, Map<Double, Double>> values = new HashMap<String, Map<Double, Double>>();

    public LineVisualModel(String xAxisName, String yAxisName, String title) {
        this.xAxisName = xAxisName;
        this.yAxisName = yAxisName;
        this.title = title;
    }

    public void setPoint(String algorithm, double x, double y) {
        Map<Double, Double> map = values.get(algorithm);
        if (map == null) {
            map = new LinkedHashMap<Double, Double>();
            values.put(algorithm, map);
        }

        map.put(x, y);
    }

    @Override
    public List<String> getAlgorithms() {
        return new LinkedList<String>(values.keySet());
    }

    @Override
    public Map<Double, Double> getValues(String algorithm) {
        return values.get(algorithm);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void exportToCSV(File csv) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(csv);
            pw.println("Algorithm, " + getDomainAxisLabel() + ", " + getRangeAxisLabel());
            for (Entry<String, Map<Double, Double>> v : values.entrySet()) {
                for (Entry<Double, Double> vin : v.getValue().entrySet()) {
                    pw.println(v.getKey() + ", " + vin.getKey() + ", " + vin.getValue());
                }
            }
            pw.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LineVisualModel.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            pw.close();
        }
    }

    @Override
    public String getDomainAxisLabel() {
        return xAxisName;
    }

    @Override
    public String getRangeAxisLabel() {
        return yAxisName;
    }
}
