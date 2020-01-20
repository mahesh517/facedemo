package com.app.detection.Svd;

public class LUDecompositionAlt_D64 extends LUDecompositionBase_D64 {

    /**
     * This is a modified version of what was found in the JAMA package.  The order that it
     * performs its permutations in is the primary difference from NR
     *
     * @param a The matrix that is to be decomposed.  Not modified.
     * @return true If the matrix can be decomposed and false if it can not.
     */
    public boolean decompose( DenseMatrix64F a )
    {
        decomposeCommonInit(a);

        double LUcolj[] = vv;

        for( int j = 0; j < n; j++ ) {

            // make a copy of the column to avoid cache jumping issues
            for( int i = 0; i < m; i++) {
                LUcolj[i] = dataLU[i*n + j];
            }

            // Apply previous transformations.
            for( int i = 0; i < m; i++ ) {
                int rowIndex = i*n;

                // Most of the time is spent in the following dot product.
                int kmax = i < j ? i : j;
                double s = 0.0;
                for (int k = 0; k < kmax; k++) {
                    s += dataLU[rowIndex+k]*LUcolj[k];
                }

                dataLU[rowIndex+j] = LUcolj[i] -= s;
            }

            // Find pivot and exchange if necessary.
            int p = j;
            double max = Math.abs(LUcolj[p]);
            for (int i = j+1; i < m; i++) {
                double v = Math.abs(LUcolj[i]);
                if ( v > max) {
                    p = i;
                    max = v;
                }
            }

            if (p != j) {
                // swap the rows
//                for (int k = 0; k < n; k++) {
//                    double t = dataLU[p*n + k];
//                    dataLU[p*n + k] = dataLU[j*n + k];
//                    dataLU[j*n + k] = t;
//                }
                int rowP = p*n;
                int rowJ = j*n;
                int endP = rowP+n;
                for (;rowP < endP; rowP++,rowJ++) {
                    double t = dataLU[rowP];
                    dataLU[rowP] = dataLU[rowJ];
                    dataLU[rowJ] = t;
                }
                int k = pivot[p]; pivot[p] = pivot[j]; pivot[j] = k;
                pivsign = -pivsign;
            }
            indx[j] = p;

            // Compute multipliers.
            if (j < m ) {
                double lujj = dataLU[j*n+j];
                if( lujj != 0 ) {
                    for (int i = j+1; i < m; i++) {
                        dataLU[i*n+j] /= lujj;
                    }
                }
            }
        }

        return true;
    }
}
