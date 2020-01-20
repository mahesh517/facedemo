package com.app.detection.Svd;

public class LinearSolverFactory {

    /**
     * Creates a linear solver using LU decomposition
     */
    public static LinearSolver<DenseMatrix64F> lu( int numRows ) {
        return linear(numRows);
    }

    /**
     * Creates a linear solver using Cholesky decomposition
     */
    public static LinearSolver<DenseMatrix64F> chol( int numRows ) {
        return symmPosDef(numRows);
    }

    /**
     * Creates a linear solver using QR decomposition
     */
    public static LinearSolver<DenseMatrix64F> qr( int numRows , int numCols ) {
        return leastSquares(numRows,numCols);
    }

    /**
     * Creates a linear solver using QRP decomposition
     */
    public static LinearSolver<DenseMatrix64F> qrp( boolean computeNorm2, boolean computeQ ) {
        return leastSquaresQrPivot(computeNorm2,computeQ);
    }

    /**
     * Creates a general purpose solver.  Use this if you are not sure what you need.
     *
     * @param numRows The number of rows that the decomposition is optimized for.
     * @param numCols The number of columns that the decomposition is optimized for.
     */
    public static LinearSolver<DenseMatrix64F> general( int numRows , int numCols ) {
        if( numRows == numCols )
            return linear(numRows);
        else
            return leastSquares(numRows,numCols);
    }

    /**
     * Creates a solver for linear systems.  The A matrix will have dimensions (m,m).
     *
     * @return A new linear solver.
     */
    public static LinearSolver<DenseMatrix64F> linear( int matrixSize ) {
        return new LinearSolverLu_D64(new LUDecompositionAlt_D64());
    }

    /**
     * Creates a good general purpose solver for over determined systems and returns the optimal least-squares
     * solution.  The A matrix will have dimensions (m,n) where m &ge; n.
     *
     * @param numRows The number of rows that the decomposition is optimized for.
     * @param numCols The number of columns that the decomposition is optimized for.
     * @return A new least-squares solver for over determined systems.
     */
    public static LinearSolver<DenseMatrix64F> leastSquares( int numRows , int numCols ) {
        if(numCols < EjmlParameters.SWITCH_BLOCK64_QR )  {
            return new LinearSolverQrHouseCol_D64();
        } else {
            if( EjmlParameters.MEMORY == EjmlParameters.MemoryUsage.FASTER )
                return new LinearSolverQrBlock64_D64();
            else
                return new LinearSolverQrHouseCol_D64();
        }
    }

    /**
     * Creates a solver for symmetric positive definite matrices.
     *
     * @return A new solver for symmetric positive definite matrices.
     */
    public static LinearSolver<DenseMatrix64F> symmPosDef( int matrixWidth ) {
        if(matrixWidth < EjmlParameters.SWITCH_BLOCK64_CHOLESKY )  {
            CholeskyDecompositionCommon_D64 decomp = new CholeskyDecompositionInner_D64(true);
            return new LinearSolverChol_D64(decomp);
        } else {
            if( EjmlParameters.MEMORY == EjmlParameters.MemoryUsage.FASTER )
                return new LinearSolverChol_B64();
            else {
                CholeskyDecompositionCommon_D64 decomp = new CholeskyDecompositionInner_D64(true);
                return new LinearSolverChol_D64(decomp);
            }
        }
    }

    /**
     * <p>
     * Linear solver which uses QR pivot decomposition.  These solvers can handle singular systems
     * and should never fail.  For singular systems, the solution might not be as accurate as a
     * pseudo inverse that uses SVD.
     * </p>
     *
     * <p>
     * For singular systems there are multiple correct solutions.  The optimal 2-norm solution is the
     * solution vector with the minimal 2-norm and is unique.  If the optimal solution is not computed
     * for details.  There is only a runtime difference for small matrices, 2-norm solution is slower.
     * </p>
     *
     * <p>
     * Two different solvers are available.  Compute Q will compute the Q matrix once then use it multiple times.
     * If the solution for a single vector is being found then this should be set to false.  If the pseudo inverse
     * is being found or the solution matrix has more than one columns AND solve is being called numerous multiples
     * times then this should be set to true.
     * </p>
     *
     * @param computeNorm2 true to compute the minimum 2-norm solution for singular systems. Try true.
     * @param computeQ Should it precompute Q or use house holder.  Try false;
     * @return Pseudo inverse type solver using QR with column pivots.
     */
    public static LinearSolver<DenseMatrix64F> leastSquaresQrPivot( boolean computeNorm2 , boolean computeQ ) {
        QRColPivDecompositionHouseholderColumn_D64 decomposition =
                new QRColPivDecompositionHouseholderColumn_D64();

        if( computeQ )
            return new SolvePseudoInverseQrp_D64(decomposition,computeNorm2);
        else
            return new LinearSolverQrpHouseCol_D64(decomposition,computeNorm2);
    }

    /**
     * <p>
     * Returns a solver which uses the pseudo inverse.  Useful when a matrix
     * needs to be inverted which is singular.  Two variants of pseudo inverse are provided.  SVD
     * will tend to be the most robust but the slowest and QR decomposition with column pivots will
     * be faster, but less robust.
     * </p>
     *
     * <p>
     * See {@link #leastSquaresQrPivot} for additional options specific to QR decomposition based
     * pseudo inverse.  These options allow for better runtime performance in different situations.
     * </p>
     *
     * @param useSVD If true SVD will be used, otherwise QR with column pivot will be used.
     * @return Solver for singular matrices.
     */
    public static LinearSolver<DenseMatrix64F> pseudoInverse( boolean useSVD ) {
        if( useSVD )
            return new SolvePseudoInverseSvd();
        else
            return leastSquaresQrPivot(true,false);
    }

    /**
     * Create a solver which can efficiently add and remove elements instead of recomputing
     * everything from scratch.
     */
    public static AdjustableLinearSolver adjustable() {
        return new AdjLinearSolverQr_D64();
    }
}
