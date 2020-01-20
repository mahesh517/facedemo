package com.app.detection.Svd;

public class CholeskyDecomposition_B64_to_D64
        extends BaseDecomposition_B64_to_D64 implements CholeskyDecomposition<DenseMatrix64F> {

    public CholeskyDecomposition_B64_to_D64(boolean lower) {
        super(new CholeskyOuterForm_B64(lower), EjmlParameters.BLOCK_WIDTH);
    }

    @Override
    public boolean isLower() {
        return ((CholeskyOuterForm_B64)alg).isLower();
    }

    @Override
    public DenseMatrix64F getT(DenseMatrix64F T) {
        BlockMatrix64F T_block = ((CholeskyOuterForm_B64)alg).getT(null);

        if( T == null ) {
            T = new DenseMatrix64F(T_block.numRows,T_block.numCols);
        }

        BlockMatrixOps.convert(T_block,T);
        // todo set zeros
        return T;
    }

    @Override
    public Complex64F computeDeterminant() {
        return ((CholeskyOuterForm_B64)alg).computeDeterminant();
    }
}
