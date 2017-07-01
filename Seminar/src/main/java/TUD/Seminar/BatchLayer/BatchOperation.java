package TUD.Seminar.BatchLayer;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.bson.Document;

import com.mongodb.BasicDBObject;

import TUD.Seminar.Constants.Constants;
import TUD.Seminar.GUI.MainFrame;
import TUD.Seminar.MongoDB.MongoDBConnector;
import TUD.Seminar.SimulatedStream.Categories;

public class BatchOperation extends TimerTask {

	MongoDBConnector mongo;
	private double[] beta;
	private int batchSize;

	public BatchOperation() {
		mongo = new MongoDBConnector(Constants.DATABASE);
	}

	@Override
	public void run() {
		if(beta == null){
			calculate();
		}
		else{
			// Create MongoDB Object an write it to the db
			Document mongoDBdoc = new Document("date", new Date()).append("batchSize", batchSize);
			
			List<BasicDBObject> mongoDBArray = new LinkedList<>();
			Categories[] categories = Categories.values();
			for(int i = 0; i < Categories.getCategoryCount(); i++){
				double d = beta[i];
				BasicDBObject o = new BasicDBObject();
				o.put(categories[i].name(), d);
				mongoDBArray.add(o);
			}	
			mongoDBdoc.append("betas", mongoDBArray);
			
			mongo.writeToDb(mongoDBdoc, Constants.REGRESSION);
			
			calculate();
		}
	}

	public void calculate() {
		// Do a regression of all collected orders and store the
		// resulting equation
		System.out.println("started batch calculation");

		batchSize = (int) mongo.count(Constants.RAWDATA);
		double[] totalCartValue = new double[batchSize];
		double[][] variables = new double[batchSize][Categories.getCategoryCount()];

		int i = 0;
		for (Document doc : mongo.find(Constants.RAWDATA)) {

			if(i > batchSize - 1)
				break;
			
			@SuppressWarnings("unchecked")
			ArrayList<Document> categories = (ArrayList<Document>) doc.get("categories");
			double[] arr = new double[Categories.getCategoryCount()];
			int j = 0;
			for (Document category : categories)
				arr[j++] = category.getInteger("quantity");

			totalCartValue[i] = doc.getDouble("totalPrice");
			variables[i] = arr;

			i++;
		}
		if (totalCartValue.length == variables.length && totalCartValue.length > 0 ) {
			//regression
			OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
			regression.setNoIntercept(true);
			regression.newSampleData(totalCartValue, variables);
			beta = regression.estimateRegressionParameters();
			
			//set GUI text
			MainFrame.getInstance().setBatchLayerText(beta);
			
			//reload consumer
			MainFrame.getInstance().reloadConsumer(beta, batchSize);
		}
	}
}
