package com.app.detection.Svd;

public interface ComplexMatrix64F extends Matrix {

    /**
     * Returns the complex value of the matrix's element
     * @param row Matrix element's row index..
     * @param col Matrix element's column index.
     * @param output Storage for the complex number
     */
    public void get(int row, int col, Complex64F output);

    /**
     * Set's the complex value of the matrix's element
     *
     * @param row Matrix element's row index..
     * @param col Matrix element's column index.
     * @param real The real component
     * @param imaginary The imaginary component
     */
    public void set(int row, int col, double real, double imaginary);

    /**
     * Returns the real component of the matrix's element.
     *
     * @param row Matrix element's row index..
     * @param col Matrix element's column index.
     * @return The specified element's value.
     */
    public double getReal(int row, int col);


    /**
     * Sets the real component of the matrix's element.
     *
     * @param row Matrix element's row index..
     * @param col Matrix element's column index.
     * @param val  The element's new value.
     */
    public void setReal(int row, int col, double val);

    /**
     * Returns the imaginary component of the matrix's element.
     *
     * @param row Matrix element's row index..
     * @param col Matrix element's column index.
     * @return The specified element's value.
     */
    public double getImaginary(int row, int col);


    /**
     * Sets the imaginary component of the matrix's element.
     *
     * @param row Matrix element's row index..
     * @param col Matrix element's column index.
     * @param val  The element's new value.
     */
    public void setImaginary(int row, int col, double val);

    /**
     * Returns the number of elements in the internal data array
     *
     * @return Number of elements in the data array.
     */
    public int getDataLength();

}
