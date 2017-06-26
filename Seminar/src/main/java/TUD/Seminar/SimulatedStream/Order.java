package TUD.Seminar.SimulatedStream;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;

import org.json.JSONObject;

public class Order {
	
	String id;
	Date date;
	LinkedList<Category> categories;
	
	public Order(String id, Date date, LinkedList<Category> categories){
		this.id = id;
		this.date = date;
		this.categories = categories;
	}
	
	public String getID(){
		return id;
	}
	
	public Date getDate(){
		return date;
	}
	
	public LinkedList<Category> getCategories(){
		return categories;
	}
	
	public BigDecimal getTotalPrice(){
		BigDecimal price = new BigDecimal("0.00");
		
		for(Category category : categories){
			price = price.add(category.getPrice());
		}
		
		return price;
	}
	
	public String getJSONString(){
		
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("date", date);
		json.put("categories", categories);
		json.put("totalPrice", getTotalPrice());
		
		return json.toString();
	}

}
