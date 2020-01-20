package com.app.detection.Svd;

public class LinearSolverQrBlock64_D64 extends LinearSolver_B64_to_D64 {

    public LinearSolverQrBlock64_D64() {
        super(new BlockQrHouseHolderSolver());
    }
}
