package TUD.Seminar.SimulatedStream;

import java.math.BigDecimal;

public class Category {

	String category;
	BigDecimal price;
	int quantity;
	
	public Category(String category, BigDecimal price, int quantity){
		this.category = category;
		this.price = price;
		this.quantity = quantity;
	}
	
	public String getCategory(){
		return category;
	}
	
	public BigDecimal getPrice(){
		return price;
	}
	
	public int getQuantity(){
		return quantity;
	}
}
