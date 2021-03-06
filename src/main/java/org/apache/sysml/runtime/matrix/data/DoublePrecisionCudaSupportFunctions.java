/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sysml.runtime.matrix.data;

import static jcuda.runtime.JCuda.cudaMemcpy;
import static jcuda.runtime.cudaMemcpyKind.cudaMemcpyDeviceToHost;
import static jcuda.runtime.cudaMemcpyKind.cudaMemcpyHostToDevice;

import org.apache.sysml.api.DMLScript;
import org.apache.sysml.runtime.DMLRuntimeException;
import org.apache.sysml.runtime.instructions.gpu.GPUInstruction;
import org.apache.sysml.runtime.instructions.gpu.context.GPUContext;
import org.apache.sysml.utils.GPUStatistics;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas2;
import jcuda.jcublas.cublasHandle;
import jcuda.jcusolver.JCusolverDn;
import jcuda.jcusolver.cusolverDnHandle;
import jcuda.jcusparse.JCusparse;
import jcuda.jcusparse.cusparseHandle;
import jcuda.jcusparse.cusparseMatDescr;

public class DoublePrecisionCudaSupportFunctions implements CudaSupportFunctions {

	@Override
	public int cusparsecsrgemm(cusparseHandle handle, int transA, int transB, int m, int n, int k,
			cusparseMatDescr descrA, int nnzA, Pointer csrValA, Pointer csrRowPtrA, Pointer csrColIndA,
			cusparseMatDescr descrB, int nnzB, Pointer csrValB, Pointer csrRowPtrB, Pointer csrColIndB,
			cusparseMatDescr descrC, Pointer csrValC, Pointer csrRowPtrC, Pointer csrColIndC) {
		return JCusparse.cusparseDcsrgemm(handle, transA,  transB,  m,  n,  k,
				 descrA,  nnzA,  csrValA,  csrRowPtrA,  csrColIndA,
				 descrB,  nnzB,  csrValB,  csrRowPtrB,  csrColIndB,
				 descrC,  csrValC,  csrRowPtrC,  csrColIndC);
	}
	
	@Override
	public int cublasgeam(cublasHandle handle, int transa, int transb, int m, int n, Pointer alpha, Pointer A, int lda,
			Pointer beta, Pointer B, int ldb, Pointer C, int ldc) {
		return JCublas2.cublasDgeam(handle, transa, transb, m, n, alpha, A, lda, beta, B, ldb, C, ldc);
	}
	
	@Override
	public int cusparsecsrmv(cusparseHandle handle, int transA, int m, int n, int nnz, Pointer alpha,
			cusparseMatDescr descrA, Pointer csrValA, Pointer csrRowPtrA, Pointer csrColIndA, Pointer x, Pointer beta,
			Pointer y) {
		return JCusparse.cusparseDcsrmv(handle, transA, m, n, nnz, alpha, 
				descrA, csrValA, csrRowPtrA, csrColIndA, x, beta, y);
	}
	
	@Override
	public int	cusparsecsrmm2(cusparseHandle handle, int transa, int transb, int m, int n, int k, int nnz, jcuda.Pointer alpha, cusparseMatDescr descrA, 
			jcuda.Pointer csrValA, jcuda.Pointer csrRowPtrA, jcuda.Pointer csrColIndA, 
			jcuda.Pointer B, int ldb, jcuda.Pointer beta, jcuda.Pointer C, int ldc) {
		return JCusparse.cusparseDcsrmm2(handle, transa, transb, m, n, k, nnz, alpha, descrA, csrValA, 
				csrRowPtrA, csrColIndA, B, ldb, beta, C, ldc);
	}
	
	@Override
	public int cublasdot(cublasHandle handle, int n, Pointer x, int incx, Pointer y, int incy, Pointer result) {
		return JCublas2.cublasDdot(handle, n, x, incx, y, incy, result);
	}
	
	@Override
	public int cublasgemv(cublasHandle handle, int trans, int m, int n, Pointer alpha, Pointer A, int lda, Pointer x,
			int incx, Pointer beta, Pointer y, int incy) {
		return JCublas2.cublasDgemv(handle, trans, m, n, alpha, A, lda, x, incx, beta, y, incy);
	}
	
	@Override
	public int cublasgemm(cublasHandle handle, int transa, int transb, int m, int n, int k, Pointer alpha, Pointer A,
			int lda, Pointer B, int ldb, Pointer beta, Pointer C, int ldc) {
		return JCublas2.cublasDgemm(handle, transa, transb, m, n, k, alpha, A, lda, B, ldb, beta, C, ldc);
	}
	
	@Override
	public int cusparsecsr2csc(cusparseHandle handle, int m, int n, int nnz, Pointer csrVal, Pointer csrRowPtr,
			Pointer csrColInd, Pointer cscVal, Pointer cscRowInd, Pointer cscColPtr, int copyValues, int idxBase) {
		return JCusparse.cusparseDcsr2csc(handle, m, n, nnz, csrVal, csrRowPtr, csrColInd, cscVal, cscRowInd, cscColPtr, copyValues, idxBase);
	}
	
	@Override
	public int cublassyrk(cublasHandle handle, int uplo, int trans, int n, int k, Pointer alpha, Pointer A, int lda,
			Pointer beta, Pointer C, int ldc) {
		return JCublas2.cublasDsyrk(handle, uplo, trans, n, k, alpha, A, lda, beta, C, ldc);
	}
	
	@Override
	public int cublasaxpy(cublasHandle handle, int n, Pointer alpha, Pointer x, int incx, Pointer y, int incy) {
		return JCublas2.cublasDaxpy(handle, n, alpha, x, incx, y, incy);
	}
	
	@Override
	public int cublastrsm(cublasHandle handle, int side, int uplo, int trans, int diag, int m, int n, Pointer alpha,
			Pointer A, int lda, Pointer B, int ldb) {
		return JCublas2.cublasDtrsm(handle, side, uplo, trans, diag, m, n, alpha, A, lda, B, ldb);
	}

	@Override
	public int cusolverDngeqrf_bufferSize(cusolverDnHandle handle, int m, int n, Pointer A, int lda, int[] Lwork) {
		return JCusolverDn.cusolverDnDgeqrf_bufferSize(handle, m, n, A, lda, Lwork);
	}
	
	@Override
	public int cusolverDngeqrf(cusolverDnHandle handle, int m, int n, Pointer A, int lda, Pointer TAU,
			Pointer Workspace, int Lwork, Pointer devInfo) {
		return JCusolverDn.cusolverDnDgeqrf(handle, m, n, A, lda, TAU, Workspace, Lwork, devInfo);
	}

	@Override
	public int cusolverDnormqr(cusolverDnHandle handle, int side, int trans, int m, int n, int k, Pointer A, int lda,
			Pointer tau, Pointer C, int ldc, Pointer work, int lwork, Pointer devInfo) {
		return JCusolverDn.cusolverDnDormqr(handle, side, trans, m, n, k, A, lda, tau, C, ldc, work, lwork, devInfo);
	}
	
	@Override
	public int cusparsecsrgeam(cusparseHandle handle, int m, int n, Pointer alpha, cusparseMatDescr descrA, int nnzA,
			Pointer csrValA, Pointer csrRowPtrA, Pointer csrColIndA, Pointer beta, cusparseMatDescr descrB, int nnzB,
			Pointer csrValB, Pointer csrRowPtrB, Pointer csrColIndB, cusparseMatDescr descrC, Pointer csrValC,
			Pointer csrRowPtrC, Pointer csrColIndC) {
		return JCusparse.cusparseDcsrgeam(handle, m, n, alpha, descrA, nnzA, 
				csrValA, csrRowPtrA, csrColIndA, beta, descrB, nnzB, 
				csrValB, csrRowPtrB, csrColIndB, descrC, csrValC, csrRowPtrC, csrColIndC);
	}
	
	@Override
	public int cusparsecsr2dense(cusparseHandle handle, int m, int n, cusparseMatDescr descrA, Pointer csrValA,
			Pointer csrRowPtrA, Pointer csrColIndA, Pointer A, int lda) {
		return JCusparse.cusparseDcsr2dense(handle, m, n, descrA, csrValA, csrRowPtrA, csrColIndA, A, lda);
	}

	@Override
	public int cusparsedense2csr(cusparseHandle handle, int m, int n, cusparseMatDescr descrA, Pointer A, int lda,
			Pointer nnzPerRow, Pointer csrValA, Pointer csrRowPtrA, Pointer csrColIndA) {
		return JCusparse.cusparseDdense2csr(handle, m, n, descrA, A, lda, nnzPerRow, csrValA, csrRowPtrA, csrColIndA);
	}

	@Override
	public int cusparsennz(cusparseHandle handle, int dirA, int m, int n, cusparseMatDescr descrA, Pointer A, int lda,
			Pointer nnzPerRowCol, Pointer nnzTotalDevHostPtr) {
		return JCusparse.cusparseDnnz(handle, dirA, m, n, descrA, A, lda, nnzPerRowCol, nnzTotalDevHostPtr);
	}

	@Override
	public void deviceToHost(GPUContext gCtx, Pointer src, double[] dest, String instName, boolean isEviction) throws DMLRuntimeException {
		long t1 = DMLScript.FINEGRAINED_STATISTICS  && instName != null? System.nanoTime() : 0;
		cudaMemcpy(Pointer.to(dest), src, ((long)dest.length)*Sizeof.DOUBLE, cudaMemcpyDeviceToHost);
		if(DMLScript.FINEGRAINED_STATISTICS && instName != null) 
			GPUStatistics.maintainCPMiscTimes(instName, GPUInstruction.MISC_TIMER_DEVICE_TO_HOST, System.nanoTime() - t1);
	}

	@Override
	public void hostToDevice(GPUContext gCtx, double[] src, Pointer dest, String instName) throws DMLRuntimeException {
		long t1 = DMLScript.FINEGRAINED_STATISTICS  && instName != null? System.nanoTime() : 0;
		cudaMemcpy(dest, Pointer.to(src), ((long)src.length)*Sizeof.DOUBLE, cudaMemcpyHostToDevice);
		if(DMLScript.FINEGRAINED_STATISTICS && instName != null) 
			GPUStatistics.maintainCPMiscTimes(instName, GPUInstruction.MISC_TIMER_HOST_TO_DEVICE, System.nanoTime() - t1);
	}
}
