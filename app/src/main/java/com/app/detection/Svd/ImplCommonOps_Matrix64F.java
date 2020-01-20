package com.app.detection.Svd;

public class ImplCommonOps_Matrix64F {
    public static void extract(RealMatrix64F src,
                               int srcY0, int srcX0,
                               RealMatrix64F dst,
                               int dstY0, int dstX0,
                               int numRows, int numCols )
    {
        for( int y = 0; y < numRows; y++ ) {
            for( int x = 0; x < numCols; x++ ) {
                double v = src.get(y+srcY0,x+srcX0);
                dst.set(dstY0+y , dstX0 +x, v);
            }
        }
    }
}
