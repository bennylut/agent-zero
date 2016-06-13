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
package bgu.dcr.az.api.prob;

/**
 *
 * @author bennyl
 */
public class ProblemPrinter {

    public static String toString(Problem p) {
        StringBuilder sb = new StringBuilder();
        constraintsToString(p, sb);
        problemToString(p, sb);
        return sb.toString();
    }

    private static void problemToString(Problem p, StringBuilder sb) {

        int maxCostSize = 0;
        int domainSize = 0;
        if (p.maxCost == 1) {
            maxCostSize = 1;
        } else {
            maxCostSize = (int) Math.log10(p.maxCost);
        }
        if (p.getDomainSize(0) == 1) {
            domainSize = 1;
        } else {
            domainSize = (int) Math.log10(p.getDomainSize(0) - 1);
        }
        String tmpForMaxCost[] = new String[maxCostSize + 2];
        String tmpForDomainSize[] = new String[domainSize + 2];
        String tmpLineForMaxCost = "";
        String tmpLineForDomainSize = "";
        String line = "";
        for (int i = 0; i < tmpForMaxCost.length; i++) {
            tmpForMaxCost[i] = "";
        }
        for (int i = 0; i < tmpForDomainSize.length; i++) {
            tmpForDomainSize[i] = "";
        }
        for (int i = 0; i < tmpForMaxCost.length; i++) {
            for (int j = 0; j < i; j++) {
                tmpForMaxCost[i] += " ";
            }
            tmpLineForMaxCost += "-";
        }
        for (int i = 0; i < tmpForDomainSize.length; i++) {
            for (int j = 0; j < i; j++) {
                tmpForDomainSize[i] += " ";
            }
            tmpLineForDomainSize += "-";
        }
        sb.append("The Problem:\n");
        for (int i = 0; i < p.getNumberOfVariables(); i++) {
            for (int j = 0; j < p.getNumberOfVariables(); j++) {
                if (!p.isConstrained(i, j)) {
                    continue;
                }
                if (i < j && !p.type().isAsymmetric()) {
                    continue;
                }
                sb.append("\n").append("Agent ").append(i).append(" --> Agent ").append(j).append("\n").append("\n");
                sb.append(tmpForDomainSize[tmpForDomainSize.length - 1]).append("|");
                line = "";
                for (Integer l : p.getDomain()) {
                    int size = 0;
                    if (l.intValue() != 0) {
                        size = (int) Math.log10(l.intValue());
                    }
                    sb.append(l).append(tmpForMaxCost[tmpForMaxCost.length - 1]);
                    line += tmpLineForMaxCost;
                }
                line += tmpLineForDomainSize;
                line += "-";
                sb.append("\n").append(line).append("\n");

                for (Integer dj : p.getDomainOf(i)) {
                    boolean first = true;
                    for (Integer di : p.getDomainOf(j)) {
                        final int constraintCost = (int) p.getConstraintCost(i, di, j, dj);
                        int sizeDomain = 0;
                        if (dj != 0) {
                            sizeDomain = (int) Math.log10(dj);
                        }
                        int sizeCons = 0;
                        if (constraintCost != 0) {
                            sizeCons = (int) Math.log10(constraintCost);
                        }
                        if (first) {
                            sb.append(dj).append(tmpForDomainSize[tmpForDomainSize.length - sizeDomain - 2]).append("|");
                            first = false;
                        }
                        sb.append(constraintCost).append(tmpForMaxCost[tmpForMaxCost.length - sizeCons - 1]);
                    }
                    sb.append("\n");
                }
            }
            sb.append("\n");
        }
    }

    private static void constraintsToString(Problem p, StringBuilder sb) {
        int maxVariables = p.getNumberOfVariables();
        maxVariables = (int) Math.log10(maxVariables);
        String tmp[] = new String[maxVariables + 2];
        String tmpLine = "";
        for (int i = 0; i < tmp.length; i++) {
            tmp[i] = "";
        }
        String line = "";
        for (int i = 0; i < tmp.length; i++) {
            for (int j = 0; j < i; j++) {
                tmp[i] += " ";
            }
            tmpLine += "-";
        }
        sb.append("Table Of Constraints:\n");
        sb.append(tmp[tmp.length - 1]).append("|");
        line += tmpLine;
        line += "-";

        for (int l = 0; l < p.getNumberOfVariables(); l++) {
            int size = 0;
            if (l != 0) {
                size = (int) Math.log10(l);
            }
            sb.append(l).append(tmp[maxVariables - size + 1]);
            line += tmpLine;
        }

        sb.append("\n").append(line).append("\n");

        for (int l = 0; l < p.getNumberOfVariables(); l++) {
            int size = 0;
            if (l != 0) {
                size = (int) Math.log10(l);
            }
            sb.append(l).append(tmp[maxVariables - size]).append("|");
            for (int k = 0; k < p.getNumberOfVariables(); k++) {
                if (p.isConstrained(k, l)) {
                    sb.append("1").append(tmp[tmp.length - 1]);
                } else {
                    sb.append("0").append(tmp[tmp.length - 1]);
                }
            }
            sb.append("\n");
        }

        sb.append("\n");
    }
}
