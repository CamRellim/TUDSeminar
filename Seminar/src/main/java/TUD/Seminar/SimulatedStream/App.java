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
    	//start the GUI
    	MainFrame frame = MainFrame.getInstance();
//    	Consumer c = new Consumer("1");
//    	c.subscribeToList(Arrays.asList("orders"));
//    	c.start();
    }
}
