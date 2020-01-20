package com.app.detection.Svd;

public interface EigenvalueExtractor {

    public boolean process(DenseMatrix64F A);

    public int getNumberOfEigenvalues();

    public Complex64F[] getEigenvalues();
}