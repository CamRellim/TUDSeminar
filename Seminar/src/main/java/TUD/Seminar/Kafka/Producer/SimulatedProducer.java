package TUD.Seminar.Kafka.Producer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;


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
			//Set individual price for each category
			else{
				if(category.equals(Categories.beauty))
					price = Math.random() * 200;	
				if(category.equals(Categories.books))
					price = Math.random() * 150;
				if(category.equals(Categories.clothing)){
					price = Math.random() * 600;
					if(price < 120)
						price += 120;
				}
				if(category.equals(Categories.electronics)){
					price = Math.random() * 1000;
					if(price < 200)
						price += 200;
				}
				if(category.equals(Categories.groceries))
					price = Math.random() * 70;
				if(category.equals(Categories.home)){
					price = Math.random() * 450;
					if(price < 80)
						price += 80;
				}
				if(category.equals(Categories.sports)){
					price = Math.random() * 300;
					if(price < 50)
						price += 50;
				}
			}
				
			
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
