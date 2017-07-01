package TUD.Seminar.Kafka.Consumer;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;

import TUD.Seminar.Constants.Constants;
import TUD.Seminar.GUI.MainFrame;
import TUD.Seminar.MongoDB.MongoDBConnector;
import TUD.Seminar.SimulatedStream.Categories;
import TUD.Seminar.SimulatedStream.Category;
import TUD.Seminar.SimulatedStream.Order;

public class Consumer extends AbstractConsumer {

	MongoDBConnector mongo;
	LinkedList<Order> orders;
	double[] batchBeta;
	double[] adjustedBeta;
	int batchSize;

	public Consumer(String groupId) {
		super(groupId);
		mongo = new MongoDBConnector(Constants.DATABASE);
		orders = new LinkedList<>();
		batchSize = 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	void initializeNeededData() {
		System.out.println("initializing");
		Document doc = mongo.findLast(Constants.REGRESSION);
		if(doc != null){
			ArrayList<Document> betas = (ArrayList<Document>) doc.get("betas");
			
			batchBeta = new double[Categories.getCategoryCount()];
			Categories[] categories = Categories.values();
			for(int i = 0; i < Categories.getCategoryCount(); i++)
				batchBeta[i] = betas.get(i).getDouble(categories[i].name());
			
			adjustedBeta = Arrays.copyOf(batchBeta, batchBeta.length);
			batchSize = doc.getInteger("batchSize");
		}
	}

	public void reload(double[] betas, int batchSize, boolean running) {
		if(running && adjustedBeta != null)
			MainFrame.getInstance().setDeviationText(betas, adjustedBeta);
		
		batchBeta = Arrays.copyOf(betas, betas.length);
		adjustedBeta = Arrays.copyOf(batchBeta, batchBeta.length);
		this.batchSize = batchSize;
	}
	
	@Override
	void consumeObject(JSONObject json) {
		// decocde the json and transform it into a product object
		LinkedList<Category> categories = new LinkedList<>();
		List<BasicDBObject> mongoDBCategories = new LinkedList<>();
		JSONArray arr = json.getJSONArray("categories");
		for (int i = 0; i < arr.length(); i++) {
			// fill products list for speed layer
			Category category = new Category(arr.getJSONObject(i).getString("category"),
					new BigDecimal(arr.getJSONObject(i).getDouble("price")), arr.getJSONObject(i).getInt("quantity"));
			categories.add(category);

			// fill mongodbProducts to write the products list into the db
			BasicDBObject o = new BasicDBObject();
			o.append("category", arr.getJSONObject(i).getString("category"));
			o.append("price", arr.getJSONObject(i).getDouble("price"));
			o.append("quantity", arr.getJSONObject(i).getInt("quantity"));
			mongoDBCategories.add(o);
		}

		SimpleDateFormat df = new SimpleDateFormat();
		Date date = new Date();
		try {
			date = df.parse(json.getString("date"));
		} catch (ParseException e) {
			// If date is not correctly formatted, current time is used
		}

		// Create MongoDB Object an write it to the db
		Document mongoDBdoc = new Document("id", json.getString("id")).append("date", date).append("totalPrice",
				json.getDouble("totalPrice")).append("categories", mongoDBCategories);
		mongo.writeToDb(mongoDBdoc, Constants.RAWDATA);

		// fill the orders list and if ORDER_SIZE is reached, start regression
		// and clear the list
		orders.add(new Order(json.getString("id"), date, categories));
		if (orders.size() == Constants.ORDER_SIZE)
			speedLayerCalculation();
	}

	private void speedLayerCalculation() {
		// get the needed data form the earlier constructed orders list
		double[] totalCartValue = new double[Constants.ORDER_SIZE];
		double[][] variables = new double[Constants.ORDER_SIZE][Categories.getCategoryCount()];
		int i = 0;
		for (Order order : orders) {
			LinkedList<Category> l = order.getCategories();
			double[] a = new double[Categories.getCategoryCount()];
			int j = 0;
			for (Category category : l)
				a[j++] = category.getQuantity();

			totalCartValue[i] = order.getTotalPrice().doubleValue();
			variables[i] = a;

			i++;
		}

		// start the regression to get the estimated parameters
		OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
		regression.setNoIntercept(true);
		regression.newSampleData(totalCartValue, variables);
		double[] beta = regression.estimateRegressionParameters();
		System.out.println("Speed Layer parameters: " + Arrays.toString(beta));

		if(batchBeta != null){
			for (i = 0; i < Categories.getCategoryCount(); i++)
				adjustedBeta[i] = (adjustedBeta[i] * batchSize + beta[i] * Constants.ORDER_SIZE)
						/ (batchSize + Constants.ORDER_SIZE);
			MainFrame.getInstance().setSpeedLayerText(adjustedBeta);
			batchSize += Constants.ORDER_SIZE;
		}
		orders.clear();
	}
}
