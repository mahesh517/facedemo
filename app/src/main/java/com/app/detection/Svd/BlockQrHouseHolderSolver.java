package com.app.detection.Svd;

public class BlockQrHouseHolderSolver implements LinearSolver<BlockMatrix64F> {

    // QR decomposition algorithm
    protected QRDecompositionHouseholder_B64 decomposer = new QRDecompositionHouseholder_B64();

    // the input matrix which has been decomposed
    protected BlockMatrix64F QR;


    public BlockQrHouseHolderSolver() {
        decomposer.setSaveW(false);
    }

    /**
     * Computes the QR decomposition of A and store the results in A.
     *
     * @param A The A matrix in the linear equation. Modified. Reference saved.
     * @return true if the decomposition was successful.
     */
    @Override
    public boolean setA(BlockMatrix64F A) {
        if( A.numRows < A.numCols )
            throw new IllegalArgumentException("Number of rows must be more than or equal to the number of columns.  " +
                    "Can't solve an underdetermined system.");

        if( !decomposer.decompose(A))
            return false;

        this.QR = decomposer.getQR();

        return true;
    }

    /**
     * Computes the quality using diagonal elements the triangular R matrix in the QR decomposition.
     *
     * @return Solutions quality.
     */
    @Override
    public double quality() {
        return SpecializedOps.qualityTriangular(decomposer.getQR());
    }

    @Override
    public void solve(BlockMatrix64F B, BlockMatrix64F X) {

        if( B.numCols != X.numCols )
            throw new IllegalArgumentException("Columns of B and X do not match");
        if( QR.numCols != X.numRows )
            throw new IllegalArgumentException("Rows in X do not match the columns in A");
        if( QR.numRows != B.numRows )
            throw new IllegalArgumentException("Rows in B do not match the rows in A.");
        if( B.blockLength != QR.blockLength || X.blockLength != QR.blockLength )
            throw new IllegalArgumentException("All matrices must have the same block length.");

        // The system being solved for can be described as:
        // Q*R*X = B

        // First apply householder reflectors to B
        // Y = Q^T*B
        decomposer.applyQTran(B);

        // Second solve for Y using the upper triangle matrix R and the just computed Y
        // X = R^-1 * Y
        BlockMatrixOps.extractAligned(B,X);

        // extract a block aligned matrix
        int M = Math.min(QR.numRows,QR.numCols);

        BlockTriangularSolver.solve(QR.blockLength,true,
                new D1Submatrix64F(QR,0,M,0,M),new D1Submatrix64F(X),false);

    }

    /**
     * Invert by solving for against an identity matrix.
     *
     * @param A_inv Where the inverted matrix saved. Modified.
     */
    @Override
    public void invert(BlockMatrix64F A_inv) {
        int M = Math.min(QR.numRows,QR.numCols);
        if( A_inv.numRows != M || A_inv.numCols != M )
            throw new IllegalArgumentException("A_inv must be square an have dimension "+M);


        // Solve for A^-1
        // Q*R*A^-1 = I

        // Apply householder reflectors to the identity matrix
        // y = Q^T*I = Q^T
        BlockMatrixOps.setIdentity(A_inv);
        decomposer.applyQTran(A_inv);

        // Solve using upper triangular R matrix
        // R*A^-1 = y
        // A^-1 = R^-1*y
        BlockTriangularSolver.solve(QR.blockLength,true,
                new D1Submatrix64F(QR,0,M,0,M),new D1Submatrix64F(A_inv),false);
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
    public QRDecomposition<BlockMatrix64F> getDecomposition() {
        return decomposer;
    }
}
