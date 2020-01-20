package com.app.detection.Svd;

public class SubmatrixOps {

    public static void setSubMatrix( RowD1Matrix64F src , RowD1Matrix64F dst ,
                                     int srcRow , int srcCol , int dstRow , int dstCol ,
                                     int numSubRows, int numSubCols )
    {
        for( int i = 0; i < numSubRows; i++ ) {
            for( int j = 0; j < numSubCols; j++ ) {
                double val = src.get(i+srcRow,j+srcCol);
                dst.set(i+dstRow,j+dstCol,val);
            }
        }
    }
}