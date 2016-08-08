package com.vb.services.database;

public class DynamoDBAPIExplorerDriver {
	
	public static void main(String args[]) {
		
		DynamoDBAPIExplorer dynamoDBAPIExplorer = new DynamoDBAPIExplorer();
		String tableName = "Music";
		//dynamoDBAPIExplorer.createTable(tableName);
		//dynamoDBAPIExplorer.displayTable(tableName);
		dynamoDBAPIExplorer.putItems();
		dynamoDBAPIExplorer.getItem();
		dynamoDBAPIExplorer.queryItem();
		dynamoDBAPIExplorer.scanItem();
		dynamoDBAPIExplorer.createGlobalIndex();
		dynamoDBAPIExplorer.updateDataInTable();
		dynamoDBAPIExplorer.updateDataInTableWithConditionalExpression();
		dynamoDBAPIExplorer.deleteItem(true);
		//dynamoDBAPIExplorer.deleteTable();
		
	}

}
