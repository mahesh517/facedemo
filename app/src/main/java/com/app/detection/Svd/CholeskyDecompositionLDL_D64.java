package com.app.detection.Svd;

public class CholeskyDecompositionLDL_D64
        implements CholeskyLDLDecomposition<DenseMatrix64F> {

    // it can decompose a matrix up to this width
    private int maxWidth;
    // width and height of the matrix
    private int n;

    // the decomposed matrix
    private DenseMatrix64F L;
    private double[] el;

    // the D vector
    private double[] d;

    // tempoary variable used by various functions
    double vv[];

    public void setExpectedMaxSize( int numRows , int numCols ) {
        if( numRows != numCols ) {
            throw new IllegalArgumentException("Can only decompose square matrices");
        }

        this.maxWidth = numRows;

        this.L = new DenseMatrix64F(maxWidth,maxWidth);
        this.el = L.data;

        this.vv = new double[maxWidth];
        this.d = new double[maxWidth];
    }

    /**
     * <p>
     * Performs Choleksy decomposition on the provided matrix.
     * </p>
     *
     * <p>
     * If the matrix is not positive definite then this function will return
     * false since it can't complete its computations.  Not all errors will be
     * found.
     * </p>
     * @param mat A symetric n by n positive definite matrix.
     * @return True if it was able to finish the decomposition.
     */
    public boolean decompose( DenseMatrix64F mat ) {
        if( mat.numRows > maxWidth ) {
            setExpectedMaxSize(mat.numRows,mat.numCols);
        } else if( mat.numRows != mat.numCols ) {
            throw new RuntimeException("Can only decompose square matrices");
        }
        n = mat.numRows;

        L.set(mat);

        double d_inv=0;
        for( int i = 0; i < n; i++ ) {
            for( int j = i; j < n; j++ ) {
                double sum = el[i*n+j];

                for( int k = 0; k < i; k++ ) {
                    sum -= el[i*n+k]*el[j*n+k]*d[k];
                }

                if( i == j ) {
                    // is it positive-definate?
                    if( sum <= 0.0 )
                        return false;

                    d[i] = sum;
                    d_inv = 1.0/sum;
                    el[i*n+i] = 1;
                } else {
                    el[j*n+i] = sum*d_inv;
                }
            }
        }
        // zero the top right corner.
        for( int i = 0; i < n; i++ ) {
            for( int j = i+1; j < n; j++ ) {
                el[i*n+j] = 0.0;
            }
        }

        return true;
    }

    @Override
    public boolean inputModified() {
        return false;
    }

    /**
     * Diagonal elements of the diagonal D matrix.
     *
     * @return diagonal elements of D
     */
    @Override
    public double[] getDiagonal() {
        return d;
    }

    /**
     * Returns L matrix from the decomposition.<br>
     * L*D*L<sup>T</sup>=A
     *
     * @return A lower triangular matrix.
     */
    public DenseMatrix64F getL() {
        return L;
    }

    public double[] _getVV() {
        return vv;
    }

    @Override
    public DenseMatrix64F getL(DenseMatrix64F L) {
        if( L == null ) {
            L = this.L.copy();
        } else {
            L.set(this.L);
        }

        return L;
    }

    @Override
    public DenseMatrix64F getD(DenseMatrix64F D) {
        return CommonOps.diag(D,L.numCols,d);
    }
}