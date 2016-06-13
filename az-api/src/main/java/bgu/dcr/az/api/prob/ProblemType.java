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

import bgu.dcr.az.api.prob.cpack.ConstraintsPackage;
import bgu.dcr.az.api.prob.cpack.KAryTreeConstraintPackage;
import bgu.dcr.az.api.prob.cpack.BinaryMapConstraintPackage;
import bgu.dcr.az.api.exen.mdef.CorrectnessTester;
import bgu.dcr.az.api.prob.cpack.AsymmetricBinaryMapConstraintPackage;

/**
 *
 * @author bennyl
 */
public enum ProblemType {

    /**
     * Constraint Satisfaction Problem, K-Ary Constraints allowed
     */
    K_ARY_DCSP (false, false){
        @Override
        public ConstraintsPackage newConstraintPackage(int numvars, int maxDomainSize) {
            return new KAryTreeConstraintPackage(numvars);
        }
    },
    /**
     * Constraint Optimization Problem, K-Ary Constraints allowed
     */
    K_ARY_DCOP (false, false) {
        @Override
        public ConstraintsPackage newConstraintPackage(int numvars, int maxDomainSize) {
            return new KAryTreeConstraintPackage(numvars);
        }
    },
    /**
     * asymmetric Constraint Optimization Problem, K-Ary Constraints allowed
     */
    K_ARY_ADCOP (true, false) {
        @Override
        public ConstraintsPackage newConstraintPackage(int numvars, int maxDomainSize) {
            return new KAryTreeConstraintPackage(numvars);
        }
    },
    /**
     * Constraint Satisfaction Problem, Only Binary and unary constraints
     * allowed
     */
    DCSP {
        @Override
        public ConstraintsPackage newConstraintPackage(int numvars, int maxDomainSize) {
            return new BinaryMapConstraintPackage(numvars, maxDomainSize);
        }
    },
    /**
     * Constraint Optimization Problem, Only Binary and unary constraints
     * allowed
     */
    DCOP {
        @Override
        public ConstraintsPackage newConstraintPackage(int numvars, int maxDomainSize) {
            return new BinaryMapConstraintPackage(numvars, maxDomainSize);
        }
    },
    /**
     * asymmetric Constraint Optimization Problem, Only Binary and unary
     * constraints allowed
     */
    ADCOP (true, true) {
        @Override
        public ConstraintsPackage newConstraintPackage(int numvars, int maxDomainSize) {
            return new AsymmetricBinaryMapConstraintPackage(numvars, maxDomainSize);
        }
    };

    public abstract ConstraintsPackage newConstraintPackage(int numvars, int maxDomainSize);
    boolean asymmetric = false;
    boolean binary = true;

    private ProblemType() {
    }

    private ProblemType(boolean asymmetric, boolean binary) {
        this.asymmetric = asymmetric;
        this.binary = binary;
    }

    public boolean isAsymmetric() {
        return asymmetric;
    }

    public boolean isBinary() {
        return binary;
    }
}
