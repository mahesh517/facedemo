package com.app.detection.Svd;

public class ComplexMath64F
{

    /**
     * Complex conjugate
     * @param input Input complex number
     * @param conj Complex conjugate of the input number
     */
    public static void conj( Complex64F input , Complex64F conj ) {
        conj.real = input.real;
        conj.imaginary = -input.imaginary;
    }

    /**
     * <p>
     * Addition: result = a + b
     * </p>
     *
     * @param a Complex number. Not modified.
     * @param b Complex number. Not modified.
     * @param result Storage for output
     */
    public static void plus( Complex64F a , Complex64F b , Complex64F result ) {
        result.real = a.real + b.real;
        result.imaginary = a.imaginary + b.imaginary;
    }

    /**
     * <p>
     * Subtraction: result = a - b
     * </p>
     *
     * @param a Complex number. Not modified.
     * @param b Complex number. Not modified.
     * @param result Storage for output
     */
    public static void minus( Complex64F a , Complex64F b , Complex64F result ) {
        result.real = a.real - b.real;
        result.imaginary = a.imaginary - b.imaginary;
    }

    /**
     * <p>
     * Multiplication: result = a * b
     * </p>
     *
     * @param a Complex number. Not modified.
     * @param b Complex number. Not modified.
     * @param result Storage for output
     */
    public static void multiply(Complex64F a, Complex64F b, Complex64F result) {
        result.real = a.real * b.real - a.imaginary*b.imaginary;
        result.imaginary = a.real*b.imaginary + a.imaginary*b.real;
    }

    /**
     * <p>
     * Division: result = a / b
     * </p>
     *
     * @param a Complex number. Not modified.
     * @param b Complex number. Not modified.
     * @param result Storage for output
     */
    public static void divide(Complex64F a, Complex64F b, Complex64F result) {
        double norm = b.getMagnitude2();
        result.real = (a.real * b.real + a.imaginary*b.imaginary)/norm;
        result.imaginary = (a.imaginary*b.real - a.real*b.imaginary)/norm;
    }

    /**
     * <p>
     * Converts a complex number into polar notation.
     * </p>
     *
     * @param input Standard notation
     * @param output Polar notation
     */
    public static void convert( Complex64F input , ComplexPolar64F output ) {
        output.r = input.getMagnitude();
        output.theta = Math.atan2(input.imaginary, input.real);
    }

    /**
     * <p>
     * Converts a complex number in polar notation into standard notation.
     * </p>
     *
     * @param input Standard notation
     * @param output Polar notation
     */
    public static void convert(ComplexPolar64F input , Complex64F output ) {
        output.real = input.r* Math.cos(input.theta);
        output.imaginary = input.r* Math.sin(input.theta);
    }

    /**
     * Division in polar notation.
     *
     * @param a Complex number in polar notation. Not modified.
     * @param b Complex number in polar notation. Not modified.
     * @param result Storage for output.
     */
    public static void multiply(ComplexPolar64F a, ComplexPolar64F b, ComplexPolar64F result)
    {
        result.r = a.r*b.r;
        result.theta = a.theta + b.theta;
    }

    /**
     * Division in polar notation.
     *
     * @param a Complex number in polar notation. Not modified.
     * @param b Complex number in polar notation. Not modified.
     * @param result Storage for output.
     */
    public static void divide(ComplexPolar64F a, ComplexPolar64F b, ComplexPolar64F result)
    {
        result.r = a.r/b.r;
        result.theta = a.theta - b.theta;
    }

    /**
     * Computes the power of a complex number in polar notation
     *
     * @param a Complex number
     * @param N Power it is to be multiplied by
     * @param result Result
     */
    public static void pow(ComplexPolar64F a , int N , ComplexPolar64F result )
    {
        result.r = Math.pow(a.r,N);
        result.theta = N*a.theta;
    }

    /**
     * Computes the N<sup>th</sup> root of a complex number in polar notation.  There are
     * N distinct N<sup>th</sup> roots.
     *
     * @param a Complex number
     * @param N The root's magnitude
     * @param k Specifies which root.  0 &le; k < N
     * @param result Computed root
     */
    public static void root(ComplexPolar64F a , int N , int k , ComplexPolar64F result )
    {
        result.r = Math.pow(a.r,1.0/N);
        result.theta = (a.theta + 2.0*k* Math.PI)/N;
    }

    /**
     * Computes the N<sup>th</sup> root of a complex number.  There are
     * N distinct N<sup>th</sup> roots.
     *
     * @param a Complex number
     * @param N The root's magnitude
     * @param k Specifies which root.  0 &le; k < N
     * @param result Computed root
     */
    public static void root( Complex64F a , int N , int k , Complex64F result )
    {
        double r = a.getMagnitude();
        double theta = Math.atan2(a.imaginary,a.real);

        r = Math.pow(r,1.0/N);
        theta = (theta + 2.0*k* Math.PI)/N;

        result.real = r* Math.cos(theta);
        result.imaginary = r* Math.sin(theta);
    }

    /**
     * Computes the square root of the complex number.
     *
     * @param input Input complex number.
     * @param root Output. The square root of the input
     */
    public static void sqrt(Complex64F input, Complex64F root)
    {
        double r = input.getMagnitude();
        double a = input.real;

        root.real = Math.sqrt((r+a)/2.0);
        root.imaginary = Math.sqrt((r-a)/2.0);
        if( input.imaginary < 0 )
            root.imaginary = -root.imaginary;
    }
}
