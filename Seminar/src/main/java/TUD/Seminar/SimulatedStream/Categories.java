package TUD.Seminar.SimulatedStream;

public enum Categories {
	electronics, groceries, books, clothing, sports, home, beauty;
	
	public static int getCategoryCount(){
		return Categories.values().length;
	}
}
