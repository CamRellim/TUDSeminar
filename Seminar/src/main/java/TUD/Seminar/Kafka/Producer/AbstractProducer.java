package TUD.Seminar.Kafka.Producer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import TUD.Seminar.MongoDB.MongoDBConnector;
import TUD.Seminar.SimulatedStream.Order;


/**
 * Abstract producer that includes all needed elements. It automates the
 * initialization and provides functions to implement a normal producers easily.
 * 
 * @author Yannick Pferr
 */
public abstract class AbstractProducer extends Thread {
	/** The kafka producer. */
	private Producer<String, String> producer;
	
	/** flag to indicate re-initializing before the next run */
	private boolean running = false;

	/**
	 * Constructor that handles loading from configuration files. Creates the
	 * producer and needed connectors.
	 */
	AbstractProducer(String env) {
		// set configs for kafka
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		// Initialize the producer
		producer = new KafkaProducer<String, String>(props);
	}

	/**
	 * <p>
	 * Here you specify what your producer should do, so for example check every
	 * rss message in a list (initialized in initializeNeededData()).
	 * </p>
	 * 
	 * <p>
	 * DO NOT USE AN INFINITE LOOP BECAUSE THIS IS ALREADY HANDLED IN THE RUN
	 * METHOD OF THIS CLASS.
	 * </p
	 */
	abstract void runRoutine();

	
	public void startProd() {
		running = true;
		synchronized(this){
			notify();
		}
	}
	
	
	public void stopProd() {
		running = false;
	}

	
	public void constructJSON(String topic, Order order){
		producer.send(new ProducerRecord<String, String>(topic, order.getJSONString()));
	}

	@Override
	public void run() {
		while (true){ 
			if(running)
				runRoutine();
			else{
				synchronized(this){
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}	
				}
			}
		}
	}
}