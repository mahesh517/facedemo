package com.app.detection.Svd;

public class WatchedDoubleStepQRDecomposition_D64
        implements EigenDecomposition<DenseMatrix64F> {

    HessenbergSimilarDecomposition_D64 hessenberg;
    WatchedDoubleStepQREigenvalue algValue;
    WatchedDoubleStepQREigenvector algVector;

    DenseMatrix64F H;

    // should it compute eigenvectors or just eigenvalues
    boolean computeVectors;

    public WatchedDoubleStepQRDecomposition_D64(boolean computeVectors) {
        hessenberg = new HessenbergSimilarDecomposition_D64(10);
        algValue = new WatchedDoubleStepQREigenvalue();
        algVector = new WatchedDoubleStepQREigenvector();

        this.computeVectors = computeVectors;
    }

    @Override
    public boolean decompose(DenseMatrix64F A) {

        if( !hessenberg.decompose(A) )
            return false;

        H = hessenberg.getH(null);

        algValue.getImplicitQR().createR = false;
//        algValue.getImplicitQR().setChecks(true,true,true);

        if( !algValue.process(H) )
            return false;

//        for( int i = 0; i < A.numRows; i++ ) {
//            System.out.println(algValue.getEigenvalues()[i]);
//        }

        algValue.getImplicitQR().createR = true;

        if( computeVectors )
            return algVector.process(algValue.getImplicitQR(), H, hessenberg.getQ(null));
        else
            return true;
    }

    @Override
    public boolean inputModified() {
        return hessenberg.inputModified();
    }

    @Override
    public int getNumberOfEigenvalues() {
        return algValue.getEigenvalues().length;
    }

    @Override
    public Complex64F getEigenvalue(int index) {
        return algValue.getEigenvalues()[index];
    }

    @Override
    public DenseMatrix64F getEigenVector(int index) {
        return algVector.getEigenvectors()[index];
    }
}
