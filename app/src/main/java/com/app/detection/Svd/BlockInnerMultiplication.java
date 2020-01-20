package com.app.detection.Svd;

public class BlockInnerMultiplication {

    /**
     * <p>
     * Performs the follow operation on individual inner blocks:<br>
     * <br>
     * C = C + A * B
     * </p>
     */
    public static void blockMultPlus( final double[] dataA, final double []dataB, final double []dataC,
                                      int indexA, int indexB, int indexC,
                                      final int heightA, final int widthA, final int widthC) {
//        for( int i = 0; i < heightA; i++ ) {
//            for( int k = 0; k < widthA; k++ ) {
//                for( int j = 0; j < widthC; j++ ) {
//                    dataC[ i*widthC + j + indexC ] += dataA[i*widthA + k + indexA] * dataB[k*widthC + j + indexB];
//                }
//            }
//        }

        int a = indexA;
        int rowC = indexC;
        for( int i = 0; i < heightA; i++ , rowC += widthC ) {
            int b = indexB;

            final int endC = rowC + widthC;
            final int endA = a + widthA;
            while( a != endA ) {//for( int k = 0; k < widthA; k++ ) {
                double valA = dataA[a++];

                int c = rowC;

                while( c != endC  ) {//for( int j = 0; j < widthC; j++ ) {
                    dataC[ c++ ] += valA * dataB[ b++ ];
                }
            }
        }
    }

    /**
     * <p>
     * Performs the follow operation on individual inner blocks:<br>
     * <br>
     * C = C + A<sup>T</sup> * B
     * </p>
     */
    public static void blockMultPlusTransA( final double[] dataA, final double []dataB, final double []dataC,
                                            int indexA, int indexB, int indexC,
                                            final int heightA, final int widthA, final int widthC) {
//        for( int i = 0; i < widthA; i++ ) {
//            for( int k = 0; k < heightA; k++ ) {
//                double valA = dataA[k*widthA + i + indexA];
//                for( int j = 0; j < widthC; j++ ) {
//                    dataC[ i*widthC + j + indexC ] += valA * dataB[k*widthC + j + indexB];
//                }
//            }
//        }

        int rowC = indexC;
        for( int i = 0; i < widthA; i++ , rowC += widthC) {
            int colA = i + indexA;
            int endA = colA + widthA*heightA;
            int b = indexB;

            // for( int k = 0; k < heightA; k++ ) {
            while(colA != endA ) {
                double valA = dataA[colA];

                int c = rowC;
                final int endB = b + widthC;

                //for( int j = 0; j < widthC; j++ ) {
                while( b != endB ) {
                    dataC[ c++ ] += valA * dataB[b++];
                }
                colA += widthA;
            }
        }
    }

    /**
     * <p>
     * Performs the follow operation on individual inner blocks:<br>
     * <br>
     * C = C + A * B<sup>T</sup>
     * </p>
     */
    public static void blockMultPlusTransB( final double[] dataA, final double []dataB, final double []dataC,
                                            int indexA, int indexB, int indexC,
                                            final int heightA, final int widthA, final int widthC) {
        for( int i = 0; i < heightA; i++ ) {
            for( int j = 0; j < widthC; j++ ) {
                double val = 0;

                for( int k = 0; k < widthA; k++ ) {
                    val += dataA[i*widthA + k + indexA] * dataB[j*widthA + k + indexB];
                }

                dataC[ i*widthC + j + indexC ] += val;
            }
        }
    }

    /**
     * <p>
     * Performs the follow operation on individual inner blocks:<br>
     * <br>
     * C = C - A * B
     * </p>
     */
    public static void blockMultMinus( final double[] dataA, final double []dataB, final double []dataC,
                                       int indexA, int indexB, int indexC,
                                       final int heightA, final int widthA, final int widthC) {
//        for( int i = 0; i < heightA; i++ ) {
//            for( int k = 0; k < widthA; k++ ) {
//                for( int j = 0; j < widthC; j++ ) {
//                    dataC[ i*widthC + j + indexC ] += dataA[i*widthA + k + indexA] * dataB[k*widthC + j + indexB];
//                }
//            }
//        }

        int a = indexA;
        int rowC = indexC;
        for( int i = 0; i < heightA; i++ , rowC += widthC ) {
            int b = indexB;

            final int endC = rowC + widthC;
            final int endA = a + widthA;
            while( a != endA ) {//for( int k = 0; k < widthA; k++ ) {
                double valA = dataA[a++];

                int c = rowC;

                while( c != endC  ) {//for( int j = 0; j < widthC; j++ ) {
                    dataC[ c++ ] -= valA * dataB[ b++ ];
                }
            }
        }
    }

    /**
     * <p>
     * Performs the follow operation on individual inner blocks:<br>
     * <br>
     * C = C - A<sup>T</sup> * B
     * </p>
     */
    public static void blockMultMinusTransA( final double[] dataA, final double []dataB, final double []dataC,
                                             int indexA, int indexB, int indexC,
                                             final int heightA, final int widthA, final int widthC) {
//        for( int i = 0; i < widthA; i++ ) {
//            for( int k = 0; k < heightA; k++ ) {
//                double valA = dataA[k*widthA + i + indexA];
//                for( int j = 0; j < widthC; j++ ) {
//                    dataC[ i*widthC + j + indexC ] += valA * dataB[k*widthC + j + indexB];
//                }
//            }
//        }

        int rowC = indexC;
        for( int i = 0; i < widthA; i++ , rowC += widthC) {
            int colA = i + indexA;
            int endA = colA + widthA*heightA;
            int b = indexB;

            // for( int k = 0; k < heightA; k++ ) {
            while(colA != endA ) {
                double valA = dataA[colA];

                int c = rowC;
                final int endB = b + widthC;

                //for( int j = 0; j < widthC; j++ ) {
                while( b != endB ) {
                    dataC[ c++ ] -= valA * dataB[b++];
                }
                colA += widthA;
            }
        }
    }

    /**
     * <p>
     * Performs the follow operation on individual inner blocks:<br>
     * <br>
     * C = C - A * B<sup>T</sup>
     * </p>
     */
    public static void blockMultMinusTransB( final double[] dataA, final double []dataB, final double []dataC,
                                             int indexA, int indexB, int indexC,
                                             final int heightA, final int widthA, final int widthC) {
        for( int i = 0; i < heightA; i++ ) {
            for( int j = 0; j < widthC; j++ ) {
                double val = 0;

                for( int k = 0; k < widthA; k++ ) {
                    val += dataA[i*widthA + k + indexA] * dataB[j*widthA + k + indexB];
                }

                dataC[ i*widthC + j + indexC ] -= val;
            }
        }
    }

    /**
     * <p>
     * Performs the follow operation on individual inner blocks:<br>
     * <br>
     * C = A * B
     * </p>
     */
    public static void blockMultSet( final double[] dataA, final double []dataB, final double []dataC,
                                     int indexA, int indexB, int indexC,
                                     final int heightA, final int widthA, final int widthC) {
//        for( int i = 0; i < heightA; i++ ) {
//            for( int k = 0; k < widthA; k++ ) {
//                for( int j = 0; j < widthC; j++ ) {
//                    dataC[ i*widthC + j + indexC ] += dataA[i*widthA + k + indexA] * dataB[k*widthC + j + indexB];
//                }
//            }
//        }

        int a = indexA;
        int rowC = indexC;
        for( int i = 0; i < heightA; i++ , rowC += widthC ) {
            int b = indexB;

            final int endC = rowC + widthC;
            final int endA = a + widthA;
            while( a != endA ) {//for( int k = 0; k < widthA; k++ ) {
                double valA = dataA[a++];

                int c = rowC;

                if( b == indexB ) {
                    while( c != endC  ) {//for( int j = 0; j < widthC; j++ ) {
                        dataC[ c++ ] = valA * dataB[ b++ ];
                    }
                } else {
                    while( c != endC  ) {//for( int j = 0; j < widthC; j++ ) {
                        dataC[ c++ ] += valA * dataB[ b++ ];
                    }
                }
            }
        }
    }

    /**
     * <p>
     * Performs the follow operation on individual inner blocks:<br>
     * <br>
     * C = A<sup>T</sup> * B
     * </p>
     */
    public static void blockMultSetTransA( final double[] dataA, final double []dataB, final double []dataC,
                                           int indexA, int indexB, int indexC,
                                           final int heightA, final int widthA, final int widthC) {
//        for( int i = 0; i < widthA; i++ ) {
//            for( int k = 0; k < heightA; k++ ) {
//                double valA = dataA[k*widthA + i + indexA];
//                for( int j = 0; j < widthC; j++ ) {
//                    dataC[ i*widthC + j + indexC ] += valA * dataB[k*widthC + j + indexB];
//                }
//            }
//        }

        int rowC = indexC;
        for( int i = 0; i < widthA; i++ , rowC += widthC) {
            int colA = i + indexA;
            int endA = colA + widthA*heightA;
            int b = indexB;

            // for( int k = 0; k < heightA; k++ ) {
            while(colA != endA ) {
                double valA = dataA[colA];

                int c = rowC;
                final int endB = b + widthC;

                //for( int j = 0; j < widthC; j++ ) {
                if( b == indexB ) {
                    while( b != endB ) {
                        dataC[ c++ ] = valA * dataB[b++];
                    }
                } else {
                    while( b != endB ) {
                        dataC[ c++ ] += valA * dataB[b++];
                    }
                }
                colA += widthA;
            }
        }
    }

    /**
     * <p>
     * Performs the follow operation on individual inner blocks:<br>
     * <br>
     * C = A * B<sup>T</sup>
     * </p>
     */
    public static void blockMultSetTransB( final double[] dataA, final double []dataB, final double []dataC,
                                           int indexA, int indexB, int indexC,
                                           final int heightA, final int widthA, final int widthC) {
        for( int i = 0; i < heightA; i++ ) {
            for( int j = 0; j < widthC; j++ ) {
                double val = 0;

                for( int k = 0; k < widthA; k++ ) {
                    val += dataA[i*widthA + k + indexA] * dataB[j*widthA + k + indexB];
                }

                dataC[ i*widthC + j + indexC ] = val;
            }
        }
    }

    /**
     * <p>
     * Performs the follow operation on individual inner blocks:<br>
     * <br>
     * C = C +  &alpha; A * B
     * </p>
     */
    public static void blockMultPlus( double alpha , final double[] dataA, final double []dataB, final double []dataC,
                                      int indexA, int indexB, int indexC,
                                      final int heightA, final int widthA, final int widthC) {
//        for( int i = 0; i < heightA; i++ ) {
//            for( int k = 0; k < widthA; k++ ) {
//                for( int j = 0; j < widthC; j++ ) {
//                    dataC[ i*widthC + j + indexC ] += dataA[i*widthA + k + indexA] * dataB[k*widthC + j + indexB];
//                }
//            }
//        }

        int a = indexA;
        int rowC = indexC;
        for( int i = 0; i < heightA; i++ , rowC += widthC ) {
            int b = indexB;

            final int endC = rowC + widthC;
            final int endA = a + widthA;
            while( a != endA ) {//for( int k = 0; k < widthA; k++ ) {
                double valA = alpha*dataA[a++];

                int c = rowC;

                while( c != endC  ) {//for( int j = 0; j < widthC; j++ ) {
                    dataC[ c++ ] += valA * dataB[ b++ ];
                }
            }
        }
    }

    /**
     * <p>
     * Performs the follow operation on individual inner blocks:<br>
     * <br>
     * C = C +  &alpha; A<sup>T</sup> * B
     * </p>
     */
    public static void blockMultPlusTransA( double alpha , final double[] dataA, final double []dataB, final double []dataC,
                                            int indexA, int indexB, int indexC,
                                            final int heightA, final int widthA, final int widthC) {
//        for( int i = 0; i < widthA; i++ ) {
//            for( int k = 0; k < heightA; k++ ) {
//                double valA = dataA[k*widthA + i + indexA];
//                for( int j = 0; j < widthC; j++ ) {
//                    dataC[ i*widthC + j + indexC ] += valA * dataB[k*widthC + j + indexB];
//                }
//            }
//        }

        int rowC = indexC;
        for( int i = 0; i < widthA; i++ , rowC += widthC) {
            int colA = i + indexA;
            int endA = colA + widthA*heightA;
            int b = indexB;

            // for( int k = 0; k < heightA; k++ ) {
            while(colA != endA ) {
                double valA = alpha*dataA[colA];

                int c = rowC;
                final int endB = b + widthC;

                //for( int j = 0; j < widthC; j++ ) {
                while( b != endB ) {
                    dataC[ c++ ] += valA * dataB[b++];
                }
                colA += widthA;
            }
        }
    }

    /**
     * <p>
     * Performs the follow operation on individual inner blocks:<br>
     * <br>
     * C = C +  &alpha; A * B<sup>T</sup>
     * </p>
     */
    public static void blockMultPlusTransB( double alpha , final double[] dataA, final double []dataB, final double []dataC,
                                            int indexA, int indexB, int indexC,
                                            final int heightA, final int widthA, final int widthC) {
        for( int i = 0; i < heightA; i++ ) {
            for( int j = 0; j < widthC; j++ ) {
                double val = 0;

                for( int k = 0; k < widthA; k++ ) {
                    val += dataA[i*widthA + k + indexA] * dataB[j*widthA + k + indexB];
                }

                dataC[ i*widthC + j + indexC ] += alpha *  val;
            }
        }
    }

    /**
     * <p>
     * Performs the follow operation on individual inner blocks:<br>
     * <br>
     * C =  &alpha; A * B
     * </p>
     */
    public static void blockMultSet( double alpha , final double[] dataA, final double []dataB, final double []dataC,
                                     int indexA, int indexB, int indexC,
                                     final int heightA, final int widthA, final int widthC) {
//        for( int i = 0; i < heightA; i++ ) {
//            for( int k = 0; k < widthA; k++ ) {
//                for( int j = 0; j < widthC; j++ ) {
//                    dataC[ i*widthC + j + indexC ] += dataA[i*widthA + k + indexA] * dataB[k*widthC + j + indexB];
//                }
//            }
//        }

        int a = indexA;
        int rowC = indexC;
        for( int i = 0; i < heightA; i++ , rowC += widthC ) {
            int b = indexB;

            final int endC = rowC + widthC;
            final int endA = a + widthA;
            while( a != endA ) {//for( int k = 0; k < widthA; k++ ) {
                double valA = alpha*dataA[a++];

                int c = rowC;

                if( b == indexB ) {
                    while( c != endC  ) {//for( int j = 0; j < widthC; j++ ) {
                        dataC[ c++ ] = valA * dataB[ b++ ];
                    }
                } else {
                    while( c != endC  ) {//for( int j = 0; j < widthC; j++ ) {
                        dataC[ c++ ] += valA * dataB[ b++ ];
                    }
                }
            }
        }
    }

    /**
     * <p>
     * Performs the follow operation on individual inner blocks:<br>
     * <br>
     * C =  &alpha; A<sup>T</sup> * B
     * </p>
     */
    public static void blockMultSetTransA( double alpha , final double[] dataA, final double []dataB, final double []dataC,
                                           int indexA, int indexB, int indexC,
                                           final int heightA, final int widthA, final int widthC) {
//        for( int i = 0; i < widthA; i++ ) {
//            for( int k = 0; k < heightA; k++ ) {
//                double valA = dataA[k*widthA + i + indexA];
//                for( int j = 0; j < widthC; j++ ) {
//                    dataC[ i*widthC + j + indexC ] += valA * dataB[k*widthC + j + indexB];
//                }
//            }
//        }

        int rowC = indexC;
        for( int i = 0; i < widthA; i++ , rowC += widthC) {
            int colA = i + indexA;
            int endA = colA + widthA*heightA;
            int b = indexB;

            // for( int k = 0; k < heightA; k++ ) {
            while(colA != endA ) {
                double valA = alpha*dataA[colA];

                int c = rowC;
                final int endB = b + widthC;

                //for( int j = 0; j < widthC; j++ ) {
                if( b == indexB ) {
                    while( b != endB ) {
                        dataC[ c++ ] = valA * dataB[b++];
                    }
                } else {
                    while( b != endB ) {
                        dataC[ c++ ] += valA * dataB[b++];
                    }
                }
                colA += widthA;
            }
        }
    }

    /**
     * <p>
     * Performs the follow operation on individual inner blocks:<br>
     * <br>
     * C =  &alpha; A * B<sup>T</sup>
     * </p>
     */
    public static void blockMultSetTransB( double alpha , final double[] dataA, final double []dataB, final double []dataC,
                                           int indexA, int indexB, int indexC,
                                           final int heightA, final int widthA, final int widthC) {
        for( int i = 0; i < heightA; i++ ) {
            for( int j = 0; j < widthC; j++ ) {
                double val = 0;

                for( int k = 0; k < widthA; k++ ) {
                    val += dataA[i*widthA + k + indexA] * dataB[j*widthA + k + indexB];
                }

                dataC[ i*widthC + j + indexC ] = alpha *  val;
            }
        }
    }
}
