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
package bgu.dcr.az.vdev;

import bc.dsl.JavaDSL;
import bgu.dcr.az.api.Agt0DSL;
import bgu.dcr.az.api.exen.escan.Registery;
import bgu.dcr.az.api.exen.mdef.Visualization;
import bgu.dcr.az.api.exp.PanicException;
import bgu.dcr.az.utils.CodeUtils;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class VisualizationMetadata {

    private Visualization visualization;
    private String vname;

    public VisualizationMetadata(Class<? extends Visualization> vClass) {
        try {
            visualization = vClass.newInstance();
            vname = JavaDSL.chop(CodeUtils.camelToWords(visualization.getClass().getSimpleName()), " visualization");
        } catch (InstantiationException | IllegalAccessException ex) {
            Agt0DSL.panic("cannot load visualization: " + vClass.getSimpleName(), ex);
        }
    }

    public Visualization getVisualization() {
        return visualization;
    }

    public String getName() {
        return vname;
    }

    public static List<VisualizationMetadata> load(Collection<? extends String> visList) {
        LinkedList<VisualizationMetadata> ret = new LinkedList<>();
        for (Object v : visList) {
            try {
                ret.add(new VisualizationMetadata(Registery.UNIT.getXMLEntity(v.toString())));
            } catch (PanicException ex) {
                System.err.println(ex.getMessage());
            }
        }

        return ret;
    }

    public Visualization getNewVisualization() {
        try {
            return visualization.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            Agt0DSL.panic("cannot load visualization: " + visualization.getClass().getSimpleName(), ex);
        }
        
        return null;
    }
}
