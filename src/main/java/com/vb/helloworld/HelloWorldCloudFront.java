package com.vb.helloworld;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClient;
import com.amazonaws.services.cloudfront.model.CustomErrorResponse;
import com.amazonaws.services.cloudfront.model.CustomErrorResponses;
import com.amazonaws.services.cloudfront.model.DistributionList;
import com.amazonaws.services.cloudfront.model.DistributionSummary;
import com.amazonaws.services.cloudfront.model.ListDistributionsRequest;
import com.amazonaws.services.cloudfront.model.ListDistributionsResult;

public class HelloWorldCloudFront {
	
	public AmazonCloudFrontClient amazonCloudFrontClient;
	
	public HelloWorldCloudFront() {
		
		this.amazonCloudFrontClient = createCloudFrontClient();
		
	}
	private AmazonCloudFrontClient createCloudFrontClient() {
		AWSCredentialsProvider awsCredentialsProvider = getAWSCredentialsProvider();
		amazonCloudFrontClient = new AmazonCloudFrontClient(awsCredentialsProvider);
		return amazonCloudFrontClient;
	}
	
	private AWSCredentialsProvider getAWSCredentialsProvider() {
		ProfileCredentialsProvider profileCredentialsProvider = new ProfileCredentialsProvider();
		return profileCredentialsProvider;
	}
	
	public void listDistributions() {
		ListDistributionsRequest listDistributionRequest = new ListDistributionsRequest();
		ListDistributionsResult listDistributionResult = this.amazonCloudFrontClient.listDistributions(listDistributionRequest);
		DistributionList distributionList = listDistributionResult.getDistributionList();
		List<DistributionSummary> distributionSummaryList = distributionList.getItems();
		
		for(DistributionSummary distributionSummary : distributionSummaryList) {
			System.out.println("===========");
			String domainName = distributionSummary.getDomainName();
			String id = distributionSummary.getId();
			System.out.println("Domain Name : " + domainName);
			System.out.println("Id : " + id);
			CustomErrorResponses customErrorResponses = distributionSummary.getCustomErrorResponses(); 
			List<CustomErrorResponse> listCustomErrorResponse = customErrorResponses.getItems();
			
			
			if ( listCustomErrorResponse.size() != 0 ) {
				for(CustomErrorResponse customerErrorResponse : listCustomErrorResponse) {
					
					if ( customerErrorResponse.getErrorCode() == 405 || customerErrorResponse.getErrorCode() == 414 ) {
						System.out.println("Error code 405 or 414 exists, so updating TTL to 0 for these error codes");
						customerErrorResponse.setErrorCachingMinTTL(0L);
					}
					
				}
			} else {
				
				System.out.println("Error code 405 and 414 doesn't exist, so creating them.");
				//List<CustomErrorResponse> listCustomerErrorResponse = null;
				List<Integer> errorCodes = new ArrayList<Integer>();
				
				errorCodes.add(405);
				errorCodes.add(414);
				
				for(Integer errorCode : errorCodes) {
					CustomErrorResponse customErrorResponse = new CustomErrorResponse();
					customErrorResponse.setErrorCode(errorCode);
					customErrorResponse.setErrorCachingMinTTL(0L);
					listCustomErrorResponse.add(customErrorResponse);
				}
				
				
			}
			
			listCustomErrorResponse = customErrorResponses.getItems();
			for(CustomErrorResponse customerErrorResponse : listCustomErrorResponse) {
				System.out.println("Error Code : " + customerErrorResponse.getResponseCode());
				System.out.println("Error Caching Min TTL : " + customerErrorResponse.getErrorCachingMinTTL());
			}
			
			
		}
		
	}

	
}
