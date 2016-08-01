package com.vb.services.scd;

public class AWSS3ApiExplorerDriver {
	
	public static void main(String args[]) {
		AWSS3ApiExplorer awsS3ApiExplorer = new AWSS3ApiExplorer();
		awsS3ApiExplorer.enableS3Logging("vastests3");
	}
}
