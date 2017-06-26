package TUD.Seminar.MongoDB;

import java.util.HashMap;
import java.util.LinkedList;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

/**
 * Wrapper for the MongoDB database connection.
 * 
 * @author Yannick Pferr
 */
public class MongoDBConnector {
	private String dbname;
	private MongoClient mongo;
	private MongoDatabase database;

	/**
	 * Creates a new writer for the given database and collection.
	 * 
	 * @param dbname
	 *            - database name in which the data will be stored
	 */
	public MongoDBConnector(String dbname) {
		this.dbname = dbname;
		mongo = new MongoClient("localhost", 27017);
		database = mongo.getDatabase(dbname);
	}

	/**
	 * Writes the given document to the defined database and collection
	 * 
	 * @param obj
	 *            - document to save
	 * @param collection
	 *            - collection in which the data will be stored
	 */
	public void writeToDb(Document obj, String collection) {
		
		MongoCollection<Document> table = database.getCollection(collection);
		table.insertOne(obj);
	}
	
	/**
	 * Gets the collection for the given name.
	 * 
	 * @param name
	 *            - name of the collection to retrieve
	 * @return
	 */
	private MongoCollection<Document> getCollection(String name) {
		
		return database.getCollection(name);
	}
		
	/**
	 * Executes a query to find specific data and returns it as a LinkedList<Document>
	 * @param collection - the collection to be searched
	 * @return - LinkedList with all found documents
	 */
	public LinkedList<Document> find(String collection){
		
		LinkedList<Document> data = new LinkedList<>();
		for(Document doc : getCollection(collection).find())
			data.add(doc);
		
		return data;
	}
	
	/**
	 * Executes a query to find specific data
	 * @param collection - the collection to be searched
	 * @param hm - A HashMap which contains all values to be searched for
	 * @return - LinkedList with all found documents
	 */
	public LinkedList<Document> find(String collection, HashMap<String, String> hm){
		BasicDBObject query = new BasicDBObject();
		query.putAll(hm);
		
		LinkedList<Document> data = new LinkedList<>();
		for(Document doc : getCollection(collection).find(query))
			data.add(doc);
		
		return data;
	}
	
	public Document findLast(String collection){
		return (Document)getCollection(collection).find().sort(new BasicDBObject("date", -1)).first();
		
	}
	
	/**
	 * Executes a query to find specific data
	 * @param collection - the collection to be searched
	 * @param hm - A HashMap which contains all values to be searched for
	 * @return - LinkedList with all found documents
	 */
	public long count(String collection){
		
		return getCollection(collection).count();
	}
	
	/**
	 * Drops the connected database.
	 */
	public void dropDatabase() {
		mongo.dropDatabase(dbname);
	}
	
	/**
	 * Closes the database connection
	 */
	public void close(){
		mongo.close();
	}
}