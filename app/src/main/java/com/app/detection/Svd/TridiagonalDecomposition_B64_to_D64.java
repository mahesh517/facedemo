package com.app.detection.Svd;

public class TridiagonalDecomposition_B64_to_D64
        extends BaseDecomposition_B64_to_D64
        implements TridiagonalSimilarDecomposition<DenseMatrix64F> {


    public TridiagonalDecomposition_B64_to_D64() {
        this(EjmlParameters.BLOCK_WIDTH);
    }

    public TridiagonalDecomposition_B64_to_D64(int blockSize) {
        super(new TridiagonalDecompositionHouseholder_B64(),blockSize);
    }

    @Override
    public DenseMatrix64F getT(DenseMatrix64F T) {
        int N = Ablock.numRows;

        if( T == null ) {
            T = new DenseMatrix64F(N,N);
        } else {
            CommonOps.fill(T, 0);
        }

        double[] diag = new double[ N ];
        double[] off = new double[ N ];

        ((TridiagonalDecompositionHouseholder_B64)alg).getDiagonal(diag,off);

        T.unsafe_set(0,0,diag[0]);
        for( int i = 1; i < N; i++ ) {
            T.unsafe_set(i,i,diag[i]);
            T.unsafe_set(i,i-1,off[i-1]);
            T.unsafe_set(i-1,i,off[i-1]);
        }

        return T;
    }

    @Override
    public DenseMatrix64F getQ(DenseMatrix64F Q, boolean transposed) {
        if( Q == null ) {
            Q = new DenseMatrix64F(Ablock.numRows,Ablock.numCols);
        }

        BlockMatrix64F Qblock = new BlockMatrix64F();
        Qblock.numRows =  Q.numRows;
        Qblock.numCols =  Q.numCols;
        Qblock.blockLength = blockLength;
        Qblock.data = Q.data;

        ((TridiagonalDecompositionHouseholder_B64)alg).getQ(Qblock,transposed);

        convertBlockToRow(Q.numRows,Q.numCols,Ablock.blockLength,Q.data);

        return Q;
    }

    @Override
    public void getDiagonal(double[] diag, double[] off) {
        ((TridiagonalDecompositionHouseholder_B64)alg).getDiagonal(diag,off);
    }
}
