package com.vb.services.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.applicationdiscovery.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.CreateGlobalSecondaryIndexAction;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndexUpdate;
import com.amazonaws.services.dynamodbv2.model.InternalServerErrorException;
import com.amazonaws.services.dynamodbv2.model.ItemCollectionSizeLimitExceededException;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.LimitExceededException;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;
import com.amazonaws.services.dynamodbv2.model.UpdateTableRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateTableResult;

/*
 * This class explores AWS DynamoDB API, to understand about the service in better way.
 */

public class DynamoDBAPIExplorer {
	
	// DynamoDB client.
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
	
	// This method creates the table with default configuration.
	public void createTable(String tableName) {
		
		Collection<KeySchemaElement> keySchema = createKeySchema();
		Collection<AttributeDefinition> attributeDefinitions = createAttributeDefinitions();
		ProvisionedThroughput provisionedThroughput = createProvisionedThroughput();
		
		createTable(tableName,keySchema, attributeDefinitions, provisionedThroughput);
	}
	
	// This method creates the table with default configuration and provided read/write througput.
	public void createTable(String tableName, long readThroughput, long writeThroughput) {
		
		Collection<KeySchemaElement> keySchema = createKeySchema();
		Collection<AttributeDefinition> attributeDefinitions = createAttributeDefinitions();
		ProvisionedThroughput provisionedThroughput = createProvisionedThroughput(readThroughput,writeThroughput);
		
		createTable(tableName,keySchema, attributeDefinitions, provisionedThroughput);
	}
	
	
	// This method creates the table with configuration provided by user.
	public void createTable(String tableName,Collection<KeySchemaElement> keySchema,Collection<AttributeDefinition> attributeDefinitions,ProvisionedThroughput provisionedThroughput ) {
		
		try {
			
			CreateTableRequest createTableRequest = new CreateTableRequest()
														.withTableName(tableName)
														.withKeySchema(createKeySchema())
														.withAttributeDefinitions(createAttributeDefinitions())
														.withProvisionedThroughput(createProvisionedThroughput());
			
			CreateTableResult createTableResult = this.amazonDynamoDBClient.createTable(createTableRequest);
			TableDescription tableDescription = createTableResult.getTableDescription();
			displayTableDetails(tableDescription);
			
	
														
		} catch(ResourceInUseException riue) {
			riue.printStackTrace();		
		} catch(LimitExceededException lee) {
			lee.printStackTrace();
		} catch(InternalServerErrorException isee) {
			isee.printStackTrace();
		}
		
	}
	
	// This method creates  'KeySchema'.
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
	
	// This method creates 'AttributeDefinition'
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
	
	// This method creates 'ProvisionedThroughput" object with default values.
	private ProvisionedThroughput createProvisionedThroughput() {
		ProvisionedThroughput provisionedThroughput = createProvisionedThroughput(1l,1l);
		return provisionedThroughput;
															
	}
	
	// This method creates "ProvisionedThroughput" object with provided values.
	private ProvisionedThroughput createProvisionedThroughput(long readThroughput, long writeThroughput) {
		ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput()
															.withReadCapacityUnits(readThroughput)
															.withWriteCapacityUnits(writeThroughput);
		return provisionedThroughput;
															
	}
	
	
	
	// This method displays info about table.
	public void displayTable(String tableName) {
		
		DescribeTableRequest describeTableRequest = new DescribeTableRequest(tableName);
		DescribeTableResult describeTableResult = null;
		try {
			describeTableResult = this.amazonDynamoDBClient.describeTable(describeTableRequest);
		}catch(ResourceInUseException riue) {
			riue.printStackTrace();		
		} catch(InternalServerErrorException isee) {
			isee.printStackTrace();
		}
		if ( describeTableResult != null ) {
			TableDescription tableDescription = describeTableResult.getTable();
			displayTableDetails(tableDescription);
		} else {
			throw new NullPointerException("describeTableResult is null.");
		}
		
	}
	
	// This method prints table details.
	private void displayTableDetails(TableDescription tableDescription) {
		System.out.println("===Table Details===\n" + 
				"\tTable Name : " + tableDescription.getTableName() + "\n" + 
				"\tTable Creation Time : " + tableDescription.getCreationDateTime() + "\n" + 
				"\tTable ARN : " + tableDescription.getTableArn() + "\n" + 
				"\tTable Status : " + tableDescription.getTableStatus() + "\n" +
				"\tTable KeySchema : " + tableDescription.getKeySchema() + "\n" +
				"\tTable Provisioned Throughput : " + tableDescription.getProvisionedThroughput() + "\n" + 
				"\tTable Size Bytes : " + tableDescription.getTableSizeBytes());
	}
	
	// This method puts item into the table.
	public void putItems() {
		
		
		
		try {
			this.amazonDynamoDBClient.putItem(putItem1());
			this.amazonDynamoDBClient.putItem(putItem2());
			this.amazonDynamoDBClient.putItem(putItem3());
			this.amazonDynamoDBClient.putItem(putItem4());
			this.amazonDynamoDBClient.putItem(putItem4());
			System.out.println("Items had been added to the table.");
		} catch(ConditionalCheckFailedException ccfe) {
			ccfe.printStackTrace();
		} catch(ProvisionedThroughputExceededException  ptee) {
			ptee.printStackTrace();
		} catch(ResourceNotFoundException rnfe) {
			rnfe.printStackTrace();
		} catch(ItemCollectionSizeLimitExceededException icslee) {
			icslee.printStackTrace();
		} catch(InternalServerErrorException isee) {
			isee.printStackTrace();
		} catch(AmazonServiceException ase) {
			ase.printStackTrace();
		}
		
		
		
		
		
	}
	
	private PutItemRequest putItem1() {
		PutItemRequest putItemRequest = new PutItemRequest();
		Map<String, AttributeValue> itemAttributes = new HashMap<String, AttributeValue>();
		String tableName = "Music";
		
		AttributeValue artistValue = new AttributeValue();
		artistValue.setS("No One You Know");
		
		AttributeValue songTitleValue = new AttributeValue();
		songTitleValue.setS("My Dog Spot");
		
		AttributeValue albumTitleValue = new AttributeValue();
		albumTitleValue.setS("Hey Now");
		
		AttributeValue priceValue = new AttributeValue();
		priceValue.setN("1.98");
		
		AttributeValue genreValue = new AttributeValue();
		genreValue.setS("Country");
		
		AttributeValue criticRatingValue = new AttributeValue();
		criticRatingValue.setN("8.4");
		
		itemAttributes.put("Artist", artistValue);
		itemAttributes.put("SongTitle", songTitleValue);
		itemAttributes.put("AlbumTitle", albumTitleValue);
		itemAttributes.put("Price", priceValue);
		itemAttributes.put("Genre", genreValue);
		itemAttributes.put("CriticRating", criticRatingValue);
		
		
		putItemRequest.setItem(itemAttributes);
		putItemRequest.setTableName(tableName);
		
		return putItemRequest;
	}
	
	private PutItemRequest putItem2() {
		PutItemRequest putItemRequest = new PutItemRequest();
		Map<String, AttributeValue> itemAttributes = new HashMap<String, AttributeValue>();
		String tableName = "Music";
		
		AttributeValue artistValue = new AttributeValue();
		artistValue.setS("No One You Know");
		
		AttributeValue songTitleValue = new AttributeValue();
		songTitleValue.setS("Somewhere Down The Road");
		
		AttributeValue albumTitleValue = new AttributeValue();
		albumTitleValue.setS("Somewhat Famous");
		
		AttributeValue yearValue = new AttributeValue();
		yearValue.setN("1984");
		
		AttributeValue genreValue = new AttributeValue();
		genreValue.setS("Country");
		
		AttributeValue criticRatingValue = new AttributeValue();
		criticRatingValue.setN("8.4");
		
		itemAttributes.put("Artist", artistValue);
		itemAttributes.put("SongTitle", songTitleValue);
		itemAttributes.put("AlbumTitle", albumTitleValue);
		itemAttributes.put("Year", yearValue);
		itemAttributes.put("Genre", genreValue);
		itemAttributes.put("CriticRating", criticRatingValue);
		
		
		putItemRequest.setItem(itemAttributes);
		putItemRequest.setTableName(tableName);
		
		return putItemRequest;
	}
	
	private PutItemRequest putItem3() {
		PutItemRequest putItemRequest = new PutItemRequest();
		Map<String, AttributeValue> itemAttributes = new HashMap<String, AttributeValue>();
		String tableName = "Music";
		
		AttributeValue artistValue = new AttributeValue();
		artistValue.setS("VAS1 : No One You Know");
		
		AttributeValue songTitleValue = new AttributeValue();
		songTitleValue.setS("VAS1 :Somewhere Down The Road");
		
		AttributeValue albumTitleValue = new AttributeValue();
		albumTitleValue.setS("VAS1 :Somewhat Famous");
		
		AttributeValue yearValue = new AttributeValue();
		yearValue.setN("1984");
		
		AttributeValue genreValue = new AttributeValue();
		genreValue.setS("Country");
		
		AttributeValue criticRatingValue = new AttributeValue();
		criticRatingValue.setN("8.4");
		
		itemAttributes.put("Artist", artistValue);
		itemAttributes.put("SongTitle", songTitleValue);
		itemAttributes.put("AlbumTitle", albumTitleValue);
		itemAttributes.put("Year", yearValue);
		itemAttributes.put("Genre", genreValue);
		itemAttributes.put("CriticRating", criticRatingValue);
		
		
		putItemRequest.setItem(itemAttributes);
		putItemRequest.setTableName(tableName);
		
		return putItemRequest;
	}
	
	private PutItemRequest putItem4() {
		PutItemRequest putItemRequest = new PutItemRequest();
		Map<String, AttributeValue> itemAttributes = new HashMap<String, AttributeValue>();
		String tableName = "Music";
		
		AttributeValue artistValue = new AttributeValue();
		artistValue.setS("VAS2 :No One You Know");
		
		AttributeValue songTitleValue = new AttributeValue();
		songTitleValue.setS("VAS2 : Somewhere Down The Road");
		
		AttributeValue albumTitleValue = new AttributeValue();
		albumTitleValue.setS("VAS2 : Somewhat Famous");
		
		AttributeValue yearValue = new AttributeValue();
		yearValue.setN("1984");
		
		AttributeValue genreValue = new AttributeValue();
		genreValue.setS("Country");
		
		AttributeValue criticRatingValue = new AttributeValue();
		criticRatingValue.setN("8.4");
		
		itemAttributes.put("Artist", artistValue);
		itemAttributes.put("SongTitle", songTitleValue);
		itemAttributes.put("AlbumTitle", albumTitleValue);
		itemAttributes.put("Year", yearValue);
		itemAttributes.put("Genre", genreValue);
		itemAttributes.put("CriticRating", criticRatingValue);
		
		
		putItemRequest.setItem(itemAttributes);
		putItemRequest.setTableName(tableName);
		
		return putItemRequest;
	}
	
	// This method puts item into the table.
	public void putItem(PutItemRequest putItemRequest) {
		
		
	}
	
	// This method puts item into the table.
	public void putItem(String tableName, Map<String,AttributeValue> item) {
		
	}
	
	// This method puts item into the table.
	public void putItem(String tableName, Map<String,AttributeValue> item, String returnValues) {
			
	}
	
	// This method gets the item.
	public void getItem() {
		
		String tableName = "Music";
		Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		
		AttributeValue artistValue = new AttributeValue();
		artistValue.setS("No One You Know");
		
		AttributeValue songTitleValue = new AttributeValue();
		songTitleValue.setS("Somewhere Down The Road");
		
		key.put("Artist", artistValue);
		key.put("SongTitle", songTitleValue);
		
		GetItemRequest getItemRequest = new GetItemRequest(tableName, key);
		getItemRequest.setProjectionExpression("AlbumTitle,Price");
		
		try {
			GetItemResult getItemResult = this.amazonDynamoDBClient.getItem(getItemRequest);
			 System.out.println("GET Item : " + getItemResult.getItem());
		} catch(AmazonServiceException ase) {
			ase.printStackTrace();
			throw new AmazonServiceException("Error executing getItem method.");
		}
		
		
	}
	
	// This method querys the items and match get which match.
	public void queryItem() {
		String tableName = "Music";
		QueryRequest queryRequest = new QueryRequest(tableName);
		
		
		Map<String, String> expressionAttributeNames = new HashMap<String,String>();
		expressionAttributeNames.put("#a", "Artist");
		expressionAttributeNames.put("#t", "SongTitle");
		
		
		
		Map<String,AttributeValue> expressionAttributeValues = new HashMap<String,AttributeValue>();
		AttributeValue artistValue = new AttributeValue();
		artistValue.setS("No One You Know");
		
		AttributeValue songTitleValue = new AttributeValue();
		songTitleValue.setS("My Dog Spot");
		
		expressionAttributeValues.put(":a", artistValue);
		expressionAttributeValues.put(":t", songTitleValue);
		
		queryRequest.setKeyConditionExpression("#a = :a and #t = :t");
		queryRequest.setExpressionAttributeNames(expressionAttributeNames);
		queryRequest.setExpressionAttributeValues(expressionAttributeValues);
		
		// Alternate Approach
		//QuerySpec querySpec = new QuerySpec()
		//.withKeyConditionExpression("#yr = :yyyy")
        //.withNameMap(nameMap)
        //.withValueMap(valueMap);
		//items = table.query(querySpec);
		
		try {
			QueryResult queryResult = this.amazonDynamoDBClient.query(queryRequest);
			System.out.println("Query Items : " + queryResult.getItems());
		} catch(AmazonServiceException ase) {
			ase.printStackTrace();
			throw new AmazonServiceException("Error executing queryItem method.");
		}
		
		
		
	}
	
	// This method scans the items and match get which match.
	public void scanItem() {
		String tableName = "Music";
		ScanRequest scanRequest = new ScanRequest(tableName);
		
		try {
			ScanResult scanResult = this.amazonDynamoDBClient.scan(scanRequest);
			System.out.println("Scan Items : " + scanResult.getItems());
		} catch(AmazonServiceException ase) {
			ase.printStackTrace();
			throw new AmazonServiceException("Error executing scanItem method.");
		}
		
			
	}
	
	// This method creates Global Secondary Index.
	public void createGlobalIndex() {
		
		String tableName = "Music";
		String indexName = "GenreAndPriceIndex";
		UpdateTableRequest updateTableRequest = new UpdateTableRequest();
		updateTableRequest.setTableName(tableName);
		
		GlobalSecondaryIndexUpdate globalSecondaryIndexUpdate = new GlobalSecondaryIndexUpdate();
		CreateGlobalSecondaryIndexAction createGlobalSecondaryIndexAction = new CreateGlobalSecondaryIndexAction();
		
		Collection<KeySchemaElement> keySchemaElements = new ArrayList<KeySchemaElement>();
		
		KeySchemaElement genre = new KeySchemaElement();
		genre.setAttributeName("Genre");
		genre.setKeyType(KeyType.HASH);
		keySchemaElements.add(genre);
		
		KeySchemaElement price = new KeySchemaElement();
		price.setAttributeName("Price");
		price.setKeyType(KeyType.RANGE);
		keySchemaElements.add(price);
		
		createGlobalSecondaryIndexAction.setIndexName(indexName);
		createGlobalSecondaryIndexAction.setKeySchema(keySchemaElements);
		ProvisionedThroughput provisionedThroughput = new ProvisionedThroughput(1l,1l);
		Projection projection = new Projection();
		projection.setProjectionType(ProjectionType.ALL);
		
		createGlobalSecondaryIndexAction.setProvisionedThroughput(provisionedThroughput);
		createGlobalSecondaryIndexAction.setProjection(projection);
		globalSecondaryIndexUpdate.setCreate(createGlobalSecondaryIndexAction);
		
		Collection<GlobalSecondaryIndexUpdate> globalSecondaryIndexUpdates = new ArrayList<GlobalSecondaryIndexUpdate>();
		globalSecondaryIndexUpdates.add(globalSecondaryIndexUpdate);
		
		Collection<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
		AttributeDefinition genreAttributeDefinition = new AttributeDefinition("Genre", ScalarAttributeType.S);
		AttributeDefinition priceAttributeDefinition = new AttributeDefinition("Price", ScalarAttributeType.N);
		
		attributeDefinitions.add(genreAttributeDefinition);
		attributeDefinitions.add(priceAttributeDefinition);
		
		
		updateTableRequest.setGlobalSecondaryIndexUpdates(globalSecondaryIndexUpdates);
		updateTableRequest.setAttributeDefinitions(attributeDefinitions);
		
		try {
			UpdateTableResult updateTableResult = this.amazonDynamoDBClient.updateTable(updateTableRequest);
			System.out.println("Index created : " + updateTableResult.getTableDescription());
		} catch ( ResourceInUseException riue) {
			riue.printStackTrace();
		} catch ( ResourceNotFoundException rnfe) {
			rnfe.printStackTrace();
		} catch ( LimitExceededException lee) {
			lee.printStackTrace();
		} catch ( InternalServerErrorException isee ) {
			isee.printStackTrace();
		}
		
	}
	
	// Update data in the table.
	public void updateDataInTable() {
		
		this.updateTable(false);
	}
	
	// Update data in the table with ConditionalExpression.
	public void updateDataInTableWithConditionalExpression() {
		this.updateTable(true);
	}
	
	private void updateTable(boolean enableConditionalEpxression) {
		
		String tableName = "Music";
		
		// We can update table using below code too. Currently commented it.
		// String hashKeyName = "Artist";
		// String hashKeyValue = "No One You Know";
		// String rangeKeyName = "SongTitle";
		//String rangeKeyValue = "Call Me Today";
		
		//String updateExpression = "SET RecordLabel = :label";
		
		
		//UpdateItemSpec updateItemSpec = new UpdateItemSpec()
		//								.withPrimaryKey(hashKeyName, hashKeyValue, rangeKeyName, rangeKeyValue)
		//								.withUpdateExpression(updateExpression)
		//								.withValueMap(new ValueMap().withString(":label", "Global Records"))
		//								.withReturnValues(ReturnValue.UPDATED_NEW);
		//Table table = getTable(tableName);
		//table.updateItem(updateItemSpec);
		
		UpdateItemRequest updateItemRequest = new UpdateItemRequest();
		
		AttributeValue hashKeyValue = new AttributeValue();
		hashKeyValue.setS("No One You Know");
		
		AttributeValue rangeKeyValue = new AttributeValue();
		rangeKeyValue.setS("My Dog Spot");
		
		String hashKeyName = "Artist";
		String rangeKeyName = "SongTitle";
		
		Map<String,AttributeValue> primaryKey = new HashMap<String,AttributeValue>();
		primaryKey.put(hashKeyName, hashKeyValue);
		primaryKey.put(rangeKeyName, rangeKeyValue);
		
		String updateExpression = "SET RecordLabel = :label";
		
		Map<String,AttributeValue> expressionAttributeValues = new HashMap<String,AttributeValue>();
		AttributeValue expressionAttributeValueLabel = new AttributeValue();
		expressionAttributeValueLabel.setS("Global Records");
		expressionAttributeValues.put(":label", expressionAttributeValueLabel);
		
		updateItemRequest.setTableName(tableName);
		updateItemRequest.setKey(primaryKey);
		updateItemRequest.setUpdateExpression(updateExpression);
		updateItemRequest.setExpressionAttributeValues(expressionAttributeValues);
		updateItemRequest.setReturnValues(ReturnValue.ALL_NEW);
		
		if ( enableConditionalEpxression ) {
			
			AttributeValue expressionAttributeValuePrice = new AttributeValue();
			expressionAttributeValuePrice.setN("2.00");
			expressionAttributeValues.put(":p", expressionAttributeValuePrice);
			
			String conditionEpxression = "Price >= :p";
			updateItemRequest.setConditionExpression(conditionEpxression);
			
		}
		
		try {
			UpdateItemResult updateItemResult = this.amazonDynamoDBClient.updateItem(updateItemRequest);
			System.out.println("Item with primary key : " + primaryKey + " had been updated.");
			
		} catch(ConditionalCheckFailedException ccfe) {
			System.out.println("Item had not been updated as conditional check failed.");
			ccfe.printStackTrace();
		} catch(ProvisionedThroughputExceededException ptee) {
			ptee.printStackTrace();
		} catch(ResourceNotFoundException  rnfe) {
			rnfe.printStackTrace();
		} catch(ItemCollectionSizeLimitExceededException icsee) {
			icsee.printStackTrace();
		} catch(InternalServerErrorException isee) {
			isee.printStackTrace();
		}
		
	}
	
	// This method deletes an Item.
	public void deleteItem(boolean enableConditionalExpression) {
		
		String tableName = "Music";
		DeleteItemRequest deleteItemRequest = new DeleteItemRequest();
		
		// Set Table Name.
		deleteItemRequest.setTableName(tableName);
		
		AttributeValue hashKeyValue = new AttributeValue();
		hashKeyValue.setS("VAS1 : No One You Know");
		
		AttributeValue rangeKeyValue = new AttributeValue();
		rangeKeyValue.setS("VAS1 :Somewhere Down The Road");
		
		String hashKeyName = "Artist";
		String rangeKeyName = "SongTitle";
		
		Map<String,AttributeValue> primaryKey = new HashMap<String,AttributeValue>();
		primaryKey.put(hashKeyName, hashKeyValue);
		primaryKey.put(rangeKeyName, rangeKeyValue);
		
		// Set Primary Key.
		deleteItemRequest.setKey(primaryKey);
		
		if ( enableConditionalExpression ) {
			String conditionExpression = "attribute_exists(RecordLabel)";
			deleteItemRequest.setConditionExpression(conditionExpression);
		}
		
		try {
			
			// First check if the item exists. Only if exists then delete, else throw Exception. Not yet implemented.
			this.amazonDynamoDBClient.deleteItem(deleteItemRequest);
			System.out.println("Item with primary key : " + primaryKey + " has been deleted.");
		}  catch(ConditionalCheckFailedException ccfe) {
			System.out.println("Item had not been deleted as conditional check failed.");
			ccfe.printStackTrace();
		} catch(ProvisionedThroughputExceededException ptee) {
			ptee.printStackTrace();
		} catch(ResourceNotFoundException  rnfe) {
			rnfe.printStackTrace();
		} catch(ItemCollectionSizeLimitExceededException icsee) {
			icsee.printStackTrace();
		} catch(InternalServerErrorException isee) {
			isee.printStackTrace();
		}
		
		
		
	}
	
	// This method deletes an item and by default assumes conditionalExpression to "false".
	public void deleteItem() {
		boolean conditionalExpression = false;
		deleteItem(conditionalExpression);
	}
	
	
	
	// Get the Table object.
	public Table getTable(String tableName) {
		
		// Yet to implement this method.
		Table table = null;
		return table;
	}
	
}
