package com.app.detection.Svd;

public class Eigenpair64F {
    public double value;
    public DenseMatrix64F vector;

    public Eigenpair64F(double value, DenseMatrix64F vector) {
        this.value = value;
        this.vector = vector;
    }
}