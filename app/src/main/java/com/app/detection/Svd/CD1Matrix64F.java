package com.app.detection.Svd;

public abstract class CD1Matrix64F implements ComplexMatrix64F, ReshapeMatrix{
    /**
     * Where the raw data for the matrix is stored.  The format is type dependent.
     */
    public double[] data;

    /**
     * Number of rows in the matrix.
     */
    public int numRows;
    /**
     * Number of columns in the matrix.
     */
    public int numCols;

    /**
     * Used to get a reference to the internal data.
     *
     * @return Reference to the matrix's data.
     */
    public double[] getData() {
        return data;
    }

    /**
     * Changes the internal array reference.
     */
    public void setData( double[] data ) {
        this.data = data;
    }

    /**
     * Returns the internal array index for the specified row and column.
     *
     * @param row Row index.
     * @param col Column index.
     * @return Internal array index.
     */
    public abstract int getIndex( int row, int col );

    /**
     * Sets the value of this matrix to be the same as the value of the provided matrix.  Both
     * matrices must have the same shape:<br>
     * <br>
     * a<sub>ij</sub> = b<sub>ij</sub><br>
     * <br>
     *
     * @param b The matrix that this matrix is to be set equal to.
     */
    public void set( CD1Matrix64F b )
    {
        if( numRows != b.numRows || numCols != b.numCols ) {
            throw new MatrixDimensionException("The two matrices do not have compatible shapes.");
        }

        int dataLength = b.getDataLength();

        System.arraycopy(b.data, 0, this.data, 0, dataLength);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumRows() {
        return numRows;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumCols() {
        return numCols;
    }

    /**
     * Sets the number of rows.
     *
     * @param numRows Number of rows
     */
    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    /**
     * Sets the number of columns.
     *
     * @param numCols Number of columns
     */
    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }
}