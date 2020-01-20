package com.app.detection.Svd;

public class BaseDecomposition_B64_to_D64 implements DecompositionInterface<DenseMatrix64F> {

    protected DecompositionInterface<BlockMatrix64F> alg;

    protected double[]tmp;
    protected BlockMatrix64F Ablock = new BlockMatrix64F();
    protected int blockLength;

    public BaseDecomposition_B64_to_D64(DecompositionInterface<BlockMatrix64F> alg,
                                        int blockLength) {
        this.alg = alg;
        this.blockLength = blockLength;
    }

    @Override
    public boolean decompose(DenseMatrix64F A) {
        Ablock.numRows = A.numRows;
        Ablock.numCols = A.numCols;
        Ablock.blockLength = blockLength;
        Ablock.data = A.data;

        int tmpLength = Math.min( Ablock.blockLength , A.numRows ) * A.numCols;

        if( tmp == null || tmp.length < tmpLength )
            tmp = new double[ tmpLength ];

        // doing an in-place convert is much more memory efficient at the cost of a little
        // but of CPU
        BlockMatrixOps.convertRowToBlock(A.numRows,A.numCols,Ablock.blockLength,A.data,tmp);

        boolean ret = alg.decompose(Ablock);

        // convert it back to the normal format if it wouldn't have been modified
        if( !alg.inputModified() ) {
            BlockMatrixOps.convertBlockToRow(A.numRows,A.numCols,Ablock.blockLength,A.data,tmp);
        }

        return ret;
    }

    public void convertBlockToRow(int numRows , int numCols , int blockLength ,
                                  double[] data) {
        int tmpLength = Math.min( blockLength , numRows ) * numCols;

        if( tmp == null || tmp.length < tmpLength )
            tmp = new double[ tmpLength ];

        BlockMatrixOps.convertBlockToRow(numRows,numCols,Ablock.blockLength,data,tmp);
    }

    @Override
    public boolean inputModified() {
        return alg.inputModified();
    }
}
