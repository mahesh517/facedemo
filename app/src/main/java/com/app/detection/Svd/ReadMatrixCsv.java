package com.app.detection.Svd;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ReadMatrixCsv extends ReadCsv {

    /**
     * Specifies where input comes from.
     *
     * @param in Where the input comes from.
     */
    public ReadMatrixCsv(InputStream in) {
        super(in);
    }

    /**
     * Reads in a DenseMatrix64F from the IO stream.
     * @return DenseMatrix64F
     * @throws IOException If anything goes wrong.
     */
    public <M extends Matrix>M read() throws IOException {
        List<String> words = extractWords();
        if( words.size() != 3 )
            throw new IOException("Unexpected number of words on first line.");

        int numRows = Integer.parseInt(words.get(0));
        int numCols = Integer.parseInt(words.get(1));
        boolean real = words.get(2).compareToIgnoreCase("real") == 0;

        if( numRows < 0 || numCols < 0)
            throw new IOException("Invalid number of rows and/or columns: "+numRows+" "+numCols);

        if( real )
            return (M)readReal(numRows, numCols);
        else
            return (M)readComplex(numRows, numCols);
    }

    /**
     * Reads in a DenseMatrix64F from the IO stream where the user specifies the matrix dimensions.
     *
     * @param numRows Number of rows in the matrix
     * @param numCols Number of columns in the matrix
     * @return DenseMatrix64F
     * @throws IOException
     */
    public DenseMatrix64F readReal(int numRows, int numCols) throws IOException {

        DenseMatrix64F A = new DenseMatrix64F(numRows,numCols);

        for( int i = 0; i < numRows; i++ ) {
            List<String> words = extractWords();
            if( words == null )
                throw new IOException("Too few rows found. expected "+numRows+" actual "+i);

            if( words.size() != numCols )
                throw new IOException("Unexpected number of words in column. Found "+words.size()+" expected "+numCols);
            for( int j = 0; j < numCols; j++ ) {
                A.set(i,j, Double.parseDouble(words.get(j)));
            }
        }

        return A;
    }

    /**
     * Reads in a CDenseMatrix64F from the IO stream where the user specifies the matrix dimensions.
     *
     * @param numRows Number of rows in the matrix
     * @param numCols Number of columns in the matrix
     * @return DenseMatrix64F
     * @throws IOException
     */
    public CDenseMatrix64F readComplex(int numRows, int numCols) throws IOException {

        CDenseMatrix64F A = new CDenseMatrix64F(numRows,numCols);

        int wordsCol = numCols*2;

        for( int i = 0; i < numRows; i++ ) {
            List<String> words = extractWords();
            if( words == null )
                throw new IOException("Too few rows found. expected "+numRows+" actual "+i);

            if( words.size() != wordsCol )
                throw new IOException("Unexpected number of words in column. Found "+words.size()+" expected "+wordsCol);
            for( int j = 0; j < wordsCol; j += 2 ) {

                double real = Double.parseDouble(words.get(j));
                double imaginary = Double.parseDouble(words.get(j+1));

                A.set(i, j, real, imaginary);
            }
        }

        return A;
    }
}
