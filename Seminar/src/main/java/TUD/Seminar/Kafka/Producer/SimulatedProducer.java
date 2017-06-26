package TUD.Seminar.Kafka.Producer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import TUD.Seminar.SimulatedStream.Categories;
import TUD.Seminar.SimulatedStream.Order;
import TUD.Seminar.SimulatedStream.Category;

public class SimulatedProducer extends AbstractProducer {

	public SimulatedProducer(String env) {
		super(env);
		// TODO Auto-generated constructor stub
	}

	@Override
	void runRoutine() {
		LinkedList<Category> categories = new LinkedList<>();
		
		for(Categories category : Categories.values()){
			int quantity = (int) Math.round((Math.random() * 10));
			double price = 0d;
			if(quantity == 0)
				price = 0;
			else
				price = Math.random() * 1000;
			
			DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
			dfs.setDecimalSeparator('.');
			DecimalFormat dFormat = new DecimalFormat("0.00", dfs);
					
			Category c = new Category(category.name(), new BigDecimal(dFormat.format(price)), quantity);
			categories.add(c);
		}
		
		
		Order order = new Order(UUID.randomUUID().toString(), new Date(), categories);
		try {
			this.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendMessage("orders", order.getJSONString());	
	}

}
