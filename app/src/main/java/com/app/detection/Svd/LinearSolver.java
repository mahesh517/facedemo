package com.app.detection.Svd;

public interface LinearSolver< T extends Matrix> {

    /**
     * <p>
     * Specifies the A matrix in the linear equation.  A reference might be saved
     * and it might also be modified depending on the implementation.  If it is modified
     * then {@link #modifiesA()} will return true.
     * </p>
     *
     * <p>
     * If this value returns true that does not guarantee a valid solution was generated.  This
     * is because some decompositions don't detect singular matrices.
     * </p>
     *
     * @param A The 'A' matrix in the linear equation. Might be modified or save the reference.
     * @return true if it can be processed.
     */
    public boolean setA(T A);

    /**
     * <p>
     * Returns a very quick to compute measure of how singular the system is.  This measure will
     * be invariant to the scale of the matrix and always be positive, with larger values
     * indicating it is less singular.  If not supported by the solver then the runtime
     * exception IllegalArgumentException is thrown.  This is NOT the matrix's condition.
     * </p>
     *
     * <p>
     * How this function is implemented is not specified.  One possible implementation is the following:
     * In many decompositions a triangular matrix
     * is extracted.  The determinant of a triangular matrix is easily computed and once normalized
     * to be scale invariant and its absolute value taken it will provide functionality described above.
     * </p>
     *
     * @return The quality of the linear system.
     */
    public double quality();

    /**
     * <p>
     * Solves for X in the linear system, A*X=B.
     * </p>
     * <p>
     * In some implementations 'B' and 'X' can be the same instance of a variable.  Call
     * {@link #modifiesB()} to determine if 'B' is modified.
     * </p>
     *
     * @param B A matrix &real; <sup>m &times; p</sup>.  Might be modified.
     * @param X A matrix &real; <sup>n &times; p</sup>, where the solution is written to.  Modified.
     */
    public void solve(T B, T X);


    /**
     * and writes the results to the provided matrix.  If 'A_inv' needs to be different from 'A'
     * is implementation dependent.
     *
     * @param A_inv Where the inverted matrix saved. Modified.
     */
    public void invert(T A_inv);

    /**
     * is modified.
     *
     * @return true if A is modified in setA().
     */
    public boolean modifiesA();

    /**
     * is modified.
     *
     * @return true if B is modified in solve(B,X).
     */
    public boolean modifiesB();


    /**
     * If a decomposition class was used internally then this will return that class.
     * Most linear solvers decompose the input matrix into a more simplistic form.
     * However some solutions do not require decomposition, e.g. inverse by minor.
     * @param <D> Decomposition type
     * @return Internal decomposition class.  If there is none then null.
     */
    public <D extends DecompositionInterface>D getDecomposition();
}
