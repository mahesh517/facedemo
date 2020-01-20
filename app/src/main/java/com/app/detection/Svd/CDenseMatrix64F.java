package com.app.detection.Svd;

public class CDenseMatrix64F extends CD1Matrix64F {

    /**
     * <p>
     * Creates a matrix with the values and shape defined by the 2D array 'data'.
     * It is assumed that 'data' has a row-major formatting:<br>
     *  <br>
     * data[ row ][ column ]
     * </p>
     * @param data 2D array representation of the matrix. Not modified.
     */
    public CDenseMatrix64F( double data[][] ) {

        this.numRows = data.length;
        this.numCols = data[0].length/2;

        this.data = new double[ numRows * numCols * 2 ];

        for (int i = 0; i < numRows; i++) {
            double[] row = data[i];
            if( row.length != numCols*2 )
                throw new IllegalArgumentException("Unexpected row size in input data at row "+i);

            System.arraycopy(row,0,this.data,i*numCols*2,row.length);
        }
    }

    public CDenseMatrix64F(int numRows, int numCols, boolean rowMajor, double... data) {
        if( data.length != numRows*numCols*2 )
            throw new RuntimeException("Unexpected length for data");

        this.data = new double[ numRows * numCols * 2 ];

        this.numRows = numRows;
        this.numCols = numCols;

        set(numRows,numCols, rowMajor, data);
    }

    /**
     * @param original Matrix which is to be copied
     */
    public CDenseMatrix64F(CDenseMatrix64F original) {
        this(original.numRows, original.numCols);
        set(original);
    }

    /**
     * Creates a new matrix with the specified number of rows and columns
     *
     * @param numRows number of rows
     * @param numCols number of columns
     */
    public CDenseMatrix64F(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.data = new double[numRows*numCols*2];
    }

    @Override
    public int getIndex(int row, int col) {
        return row*numCols*2 + col*2;
    }

    @Override
    public void reshape( int numRows , int numCols ) {
        int newLength = numRows*numCols*2;

        if( newLength > data.length ) {
            data = new double[newLength];
        }

        this.numRows = numRows;
        this.numCols = numCols;
    }

    @Override
    public void get(int row, int col, Complex64F output) {
        int index = row*numCols*2 + col*2;
        output.real = data[index];
        output.imaginary = data[index+1];
    }

    @Override
    public void set(int row, int col, double real, double imaginary) {
        int index = row*numCols*2 + col*2;
        data[index] = real;
        data[index+1] = imaginary;
    }

    @Override
    public double getReal(int row, int col) {
        return data[row*numCols*2 + col*2];
    }

    @Override
    public void setReal(int row, int col, double val) {
        data[row*numCols*2 + col*2] = val;
    }

    @Override
    public double getImaginary(int row, int col) {
        return data[row*numCols*2 + col*2 + 1];
    }

    @Override
    public void setImaginary(int row, int col, double val) {
        data[row*numCols*2 + col*2 + 1] = val;
    }

    @Override
    public int getDataLength() {
        return numRows*numCols*2;
    }

    public void set( CDenseMatrix64F original ) {
        reshape(original.numRows,original.numCols);
        int columnSize = numCols*2;
        for (int y = 0; y < numRows; y++) {
            int index = y*numCols*2;
            System.arraycopy(original.data,index,data,index,columnSize);
        }
    }

    @Override
    public CDenseMatrix64F copy() {
        return new CDenseMatrix64F(this);
    }

    @Override
    public void set(Matrix original) {
        reshape(original.getNumRows(),original.getNumCols());

        ComplexMatrix64F n = (ComplexMatrix64F)original;

        Complex64F c = new Complex64F();
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                n.get(i,j,c);
                set(i,j,c.real,c.imaginary);
            }
        }
    }

    @Override
    public void print() {
        MatrixIO.print(System.out, this);
    }

    /**
     * Number of array elements in the matrix's row.
     */
    public int getRowStride() {
        return numCols*2;
    }

    /**
     * Sets this matrix equal to the matrix encoded in the array.
     *
     * @param numRows The number of rows.
     * @param numCols The number of columns.
     * @param rowMajor If the array is encoded in a row-major or a column-major format.
     * @param data The formatted 1D array. Not modified.
     */
    public void set(int numRows, int numCols, boolean rowMajor, double ...data)
    {
        reshape(numRows,numCols);
        int length = numRows*numCols*2;

        if( length > data.length )
            throw new RuntimeException("Passed in array not long enough");

        if( rowMajor ) {
            System.arraycopy(data,0,this.data,0,length);
        } else {
            int index = 0;
            int stride = numRows*2;
            for( int i = 0; i < numRows; i++ ) {
                for( int j = 0; j < numCols; j++ ) {
                    this.data[index++] = data[j*stride+i*2];
                    this.data[index++] = data[j*stride+i*2+1];
                }
            }
        }
    }
}
