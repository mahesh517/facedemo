package com.app.detection.Svd;

public class ComplexPolar64F {
    public double r;
    public double theta;

    public ComplexPolar64F(double r, double theta) {
        this.r = r;
        this.theta = theta;
    }

    public ComplexPolar64F( Complex64F n ) {
        ComplexMath64F.convert(n, this);
    }

    public ComplexPolar64F() {
    }

    public Complex64F toStandard() {
        Complex64F ret = new Complex64F();
        ComplexMath64F.convert(this, ret);
        return ret;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public double getTheta() {
        return theta;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    public String toString() {
        return "( r = "+r+" theta = "+theta+" )";
    }
}
