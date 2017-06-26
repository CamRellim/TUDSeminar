package TUD.Seminar.SimulatedStream;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Timer;

import TUD.Seminar.BatchLayer.BatchOperation;
import TUD.Seminar.GUI.MainFrame;
import TUD.Seminar.Kafka.Consumer.Consumer;
import TUD.Seminar.Kafka.Producer.SimulatedProducer;

/**
 * Hello world!
 *
 */
public class App 
{	
    public static void main( String[] args )
    {
    	MainFrame frame = MainFrame.getInstance();
    	
    	//Start the kafka consumer
//    	Consumer consumer = new Consumer("1");
//    	consumer.subscribeToList(Arrays.asList("orders"));
//    	consumer.start();
//    	
    	//Set a timer
//    	Timer timer = new Timer();
//		Calendar date = Calendar.getInstance();
//		date.set(Calendar.AM_PM, Calendar.AM);
//		date.set(Calendar.HOUR, 0);
//		date.set(Calendar.MINUTE, 0);
//		date.set(Calendar.SECOND, 0);
//		date.set(Calendar.MILLISECOND, 0);
//
//		//Schedule timer to run every day at 0:00
//		timer.schedule(new BatchOperation(), date.getTime(),
//				// Time to wait before next action in milliseconds
//				1000l * 60l);// * 60l * 24l);
    	
//    	//Start the data stream
//        new SimulatedProducer("").start();
    }
}
