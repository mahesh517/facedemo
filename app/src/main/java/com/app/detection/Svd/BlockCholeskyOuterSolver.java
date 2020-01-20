package com.app.detection.Svd;

public class BlockCholeskyOuterSolver implements LinearSolver<BlockMatrix64F> {

    // cholesky decomposition
    private CholeskyOuterForm_B64 decomposer = new CholeskyOuterForm_B64(true);

    // size of a block take from input matrix
    private int blockLength;

    // temporary data structure used in some calculation.
    private double temp[];

    /**
     * Decomposes and overwrites the input matrix.
     *
     * @param A Semi-Positive Definite (SPD) system matrix. Modified. Reference saved.
     * @return If the matrix can be decomposed.  Will always return false of not SPD.
     */
    @Override
    public boolean setA(BlockMatrix64F A) {
        // Extract a lower triangular solution
        if( !decomposer.decompose(A) )
            return false;

        blockLength = A.blockLength;

        return true;
    }

    @Override
    public double quality() {
        return SpecializedOps.qualityTriangular(decomposer.getT(null));
    }

    /**
     * If X == null then the solution is written into B.  Otherwise the solution is copied
     * from B into X.
     */
    @Override
    public void solve(BlockMatrix64F B, BlockMatrix64F X) {
        if( B.blockLength != blockLength )
            throw new IllegalArgumentException("Unexpected blocklength in B.");

        D1Submatrix64F L = new D1Submatrix64F(decomposer.getT(null));

        if( X != null ) {
            if( X.blockLength != blockLength )
                throw new IllegalArgumentException("Unexpected blocklength in X.");
            if( X.numRows != L.col1 ) throw new IllegalArgumentException("Not enough rows in X");
        }

        if( B.numRows != L.col1 ) throw new IllegalArgumentException("Not enough rows in B");

        //  L * L^T*X = B

        // Solve for Y:  L*Y = B
        BlockTriangularSolver.solve(blockLength,false,L,new D1Submatrix64F(B),false);

        // L^T * X = Y
        BlockTriangularSolver.solve(blockLength,false,L,new D1Submatrix64F(B),true);

        if( X != null ) {
            // copy the solution from B into X
            BlockMatrixOps.extractAligned(B,X);
        }

    }

    @Override
    public void invert(BlockMatrix64F A_inv) {
        BlockMatrix64F T = decomposer.getT(null);
        if( A_inv.numRows != T.numRows || A_inv.numCols != T.numCols )
            throw new IllegalArgumentException("Unexpected number or rows and/or columns");


        if( temp == null || temp.length < blockLength*blockLength )
            temp = new double[ blockLength* blockLength ];

        // zero the upper triangular portion of A_inv
        BlockMatrixOps.zeroTriangle(true,A_inv);

        D1Submatrix64F L = new D1Submatrix64F(T);
        D1Submatrix64F B = new D1Submatrix64F(A_inv);

        // invert L from cholesky decomposition and write the solution into the lower
        // triangular portion of A_inv
        // B = inv(L)
        BlockTriangularSolver.invert(blockLength,false,L,B,temp);

        // B = L^-T * B
        // todo could speed up by taking advantage of B being lower triangular
        // todo take advantage of symmetry
        BlockTriangularSolver.solveL(blockLength,L,B,true);
    }

    @Override
    public boolean modifiesA() {
        return decomposer.inputModified();
    }

    @Override
    public boolean modifiesB() {
        return true;
    }

    @Override
    public CholeskyDecomposition<BlockMatrix64F> getDecomposition() {
        return decomposer;
    }
}

