package com.vb.services.networking.vpc;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeVpcsResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.Vpc;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.ec2.model.Tag;

public class AWSNetworkingVPCUtils {
	
	private AmazonEC2Client amazonEC2Client;
	
	
	public AWSNetworkingVPCUtils(ProfileCredentialsProvider profileCredentialsProvider) {
		this.amazonEC2Client = new AmazonEC2Client(profileCredentialsProvider);
	}
	
	// This method return list of Vpc objects.
	public List<Vpc> getAllVPCs() {
		DescribeVpcsResult describeVpcsResult = this.amazonEC2Client.describeVpcs();
		return describeVpcsResult.getVpcs();
	}
	
	// Get VPC by Name;
	public Vpc getVPCByName(String vpcName) throws NoSuchElementException {
		try {
			List<Vpc> vpcs = this.amazonEC2Client.describeVpcs().getVpcs();
			for(Vpc vpc: vpcs) {
				List<Tag> tags = vpc.getTags();
				for (Tag tag: tags) {
					if ( tag.getValue().equalsIgnoreCase(vpcName)) {
						return vpc;
					}
				}
				
			}
		}catch (AmazonS3Exception ase) {
			System.out.println("Caught an AmazonServiceException");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
		}catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException");
            System.out.println("Error Message: " + ace.getMessage());
        } 
		throw new NoSuchElementException("VPC with vpc name : " + vpcName + " doesn't exist.");
	}
	
	// Get VPC by Id;
	public Vpc getVPCById(String vpcId) throws NoSuchElementException {
		try {
			
			List<Vpc> vpcs = this.amazonEC2Client.describeVpcs().getVpcs()
					.stream()
					.filter(e -> e.getVpcId().equalsIgnoreCase(vpcId))
					.collect(Collectors.toList());
			if ( vpcs.size() == 0 ) {
				throw new NoSuchElementException("VPC with vpc id : " + vpcId + " doesn't exist.");
			}
			return vpcs.get(0);
		} catch (AmazonS3Exception ase) {
			System.out.println("Caught an AmazonServiceException");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
		}catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException");
            System.out.println("Error Message: " + ace.getMessage());
        } 
		return null;
	}
	
	// Get subnet group by name.
	public Subnet getSubnetByName(String subnetName) throws NoSuchElementException {
		
		try {
			List<Subnet> subnets = this.amazonEC2Client.describeSubnets().getSubnets();
			for(Subnet subnet: subnets) {
				List<Tag> tags = subnet.getTags();
				for (Tag tag: tags) {
					if ( tag.getValue().equalsIgnoreCase(subnetName)) {
						return subnet;
					}
				}
			}
		} catch (AmazonS3Exception ase) {
			System.out.println("Caught an AmazonServiceException");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
		}catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException");
            System.out.println("Error Message: " + ace.getMessage());
        }		
		throw new NoSuchElementException("Subnet with name : " + subnetName + " doesn't exist.");
	}
	
	// Get subnet group by subnet-id.
	public Subnet getSubnetById(String subnetId) throws NoSuchElementException {
		try {
			
			List<Subnet> subnets = this.amazonEC2Client.describeSubnets().getSubnets()
					.stream()
					.filter(e -> e.getSubnetId().equalsIgnoreCase(subnetId))
					.collect(Collectors.toList());
			if ( subnets.size() == 0 ) {
				throw new NoSuchElementException("Subnet with subnet id : " + subnetId + " doesn't exist.");
			}
			return subnets.get(0);
		} catch (AmazonS3Exception ase) {
			System.out.println("Caught an AmazonServiceException");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
		}catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException");
            System.out.println("Error Message: " + ace.getMessage());
        } 
		return null;
	}
	
	// Get security group by name.
	public SecurityGroup getSecurityGroupByName(String securityGroupName) throws NoSuchElementException {
		try { 
			List<SecurityGroup> securityGroups = this.amazonEC2Client.describeSecurityGroups().getSecurityGroups();
			
			for(SecurityGroup securityGroup: securityGroups) {
				List<Tag> tags = securityGroup.getTags();
				for (Tag tag: tags) {
					if ( tag.getValue().equalsIgnoreCase(securityGroupName)) {
						return securityGroup;
					}
				}
			}
		} catch (AmazonS3Exception ase) {
			System.out.println("Caught an AmazonServiceException");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException");
			System.out.println("Error Message: " + ace.getMessage());
		}		
	throw new NoSuchElementException("Security Group with name : " + securityGroupName + " doesn't exist.");
		
	}
	
	// Get security group by id.
	public SecurityGroup getSecurityGroupById(String securityGroupId) {
		try {
			List<SecurityGroup> securityGroups = this.amazonEC2Client.describeSecurityGroups().getSecurityGroups()
					.stream()
					.filter(e -> e.getGroupId().equalsIgnoreCase(securityGroupId))
					.collect(Collectors.toList());
			
			if ( securityGroups.size() == 0 ) {
				throw new NoSuchElementException("Security group with security group id : " + securityGroupId + " doesn't exist.");
			}
			return securityGroups.get(0);
		} catch (AmazonS3Exception ase) {
			System.out.println("Caught an AmazonServiceException");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
		}catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException");
            System.out.println("Error Message: " + ace.getMessage());
        } 
		return null;
		
	}
	

}
