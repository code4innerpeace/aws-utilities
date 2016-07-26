package com.vb.services.database;

import java.util.ArrayList;
import java.util.Collection;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.InternalServerErrorException;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.LimitExceededException;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;

public class DynamoDBAPIExplorer {
	
	private AmazonDynamoDBClient amazonDynamoDBClient;
	
	
	
	// Default constructor. Uses "default" profile under ~/.aws/credentials
	public DynamoDBAPIExplorer() {
		this(null);
	}
	
	// Uses the profile passed. The profileName must exists under "~/.aws/credentials".
	public DynamoDBAPIExplorer(String profileName) {
		
		AWSCredentialsProvider awsCredentialProvider = getProfileCredentialsProvider(profileName);
		this.amazonDynamoDBClient = new AmazonDynamoDBClient(awsCredentialProvider);
		
	}
	
	// This method returns the AWSCredentialsProvider.
	private AWSCredentialsProvider getProfileCredentialsProvider(String profileName) {
		
		ProfileCredentialsProvider profileCredentialsProvider;
		
		//Use "default" profile.
		if ( profileName == null ) {					
			profileCredentialsProvider = new ProfileCredentialsProvider();
		} else {
			profileCredentialsProvider = new ProfileCredentialsProvider(profileName);		
		}
		
		return profileCredentialsProvider;
	}
	
	// This method creates the table.
	public void createTable(String tableName) {
		
		Collection<KeySchemaElement> keySchema = createKeySchema();
		Collection<AttributeDefinition> attributeDefinitions = createAttributeDefinitions();
		ProvisionedThroughput provisionedThroughput = createProvisionedThroughput();
		
		createTable(tableName,keySchema, attributeDefinitions, provisionedThroughput);
	}
	
	public void createTable(String tableName,Collection<KeySchemaElement> keySchema,Collection<AttributeDefinition> attributeDefinitions,ProvisionedThroughput provisionedThroughput ) {
		
		try {
			
			CreateTableRequest createTableRequest = new CreateTableRequest()
														.withTableName(tableName)
														.withKeySchema(createKeySchema())
														.withAttributeDefinitions(createAttributeDefinitions())
														.withProvisionedThroughput(createProvisionedThroughput());
			
			CreateTableResult createTableResult = this.amazonDynamoDBClient.createTable(createTableRequest);
			displayTableInfo(createTableResult);
	
														
		} catch(ResourceInUseException riue) {
			riue.printStackTrace();		
		} catch(LimitExceededException lee) {
			lee.printStackTrace();
		} catch(InternalServerErrorException isee) {
			isee.printStackTrace();
		}
		
	}
	
	private Collection<KeySchemaElement> createKeySchema() {
		
		Collection<KeySchemaElement> keySchemaElements = new ArrayList<KeySchemaElement>();
		KeySchemaElement keySchemaElement;
		
		keySchemaElement = new KeySchemaElement()
								.withAttributeName("Artist")
								.withKeyType(KeyType.HASH);
		
		keySchemaElements.add(keySchemaElement);
		
		keySchemaElement = new KeySchemaElement()
				.withAttributeName("SongTitle")
				.withKeyType(KeyType.RANGE);
		
		keySchemaElements.add(keySchemaElement);
		
		return keySchemaElements; 
		
		
	}
	
	private Collection<AttributeDefinition> createAttributeDefinitions() {
		
		Collection<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
		AttributeDefinition attributeDistribution;
		attributeDistribution	= new AttributeDefinition()
										.withAttributeName("Artist")
										.withAttributeType(ScalarAttributeType.S);
		attributeDefinitions.add(attributeDistribution);
		
		attributeDistribution = new AttributeDefinition()
				.withAttributeName("SongTitle")
				.withAttributeType(ScalarAttributeType.S);
		
		attributeDefinitions.add(attributeDistribution);
		
		return attributeDefinitions;
		
		
	}
	
	private ProvisionedThroughput createProvisionedThroughput() {
		ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput()
															.withReadCapacityUnits(1L)
															.withWriteCapacityUnits(1L);
		return provisionedThroughput;
															
	}
	
	private void displayTableInfo(CreateTableResult createTableResult) {
		
		TableDescription tableDescription = createTableResult.getTableDescription();
		System.out.println("===Table Details===\n" + 
							"\tTable Name : " + tableDescription.getTableName() + "\n" + 
							"\tTable Creation Time : " + tableDescription.getCreationDateTime() + "\n" + 
							"\tTable ARN : " + tableDescription.getTableArn() + "\n" + 
							"\tTable Status : " + tableDescription.getTableStatus() + "\n" +
							"\tTable KeySchema : " + tableDescription.getKeySchema() + "\n" +
							"\tTable Provisioned Throughput : " + tableDescription.getProvisionedThroughput() + "\n" + 
							"\tTable Size Bytes : " + tableDescription.getTableSizeBytes());
		
		
	}
}
