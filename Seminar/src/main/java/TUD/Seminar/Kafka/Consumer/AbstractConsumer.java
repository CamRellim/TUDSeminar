package TUD.Seminar.Kafka.Consumer;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Abstract producer that includes all needed elements. It automates the
 * initialization and the topic listening.
 */
public abstract class AbstractConsumer extends Thread {
	/** The kafka consumer. */
	private KafkaConsumer<String, String> consumer;
	/** flag to indicate re-initializing before the next run */
	private boolean reload = false;

	/**
	 * Constructor that handles loading from configuration files. Creates the
	 * consumer and needed connectors.
	 */
	AbstractConsumer(String groupId) {
		// set configs for kafka
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("group.id", groupId);
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("auto.offset.reset", "latest");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		// Initialize the consumer
		consumer = new KafkaConsumer<String, String>(props);
	}
	
	/**
	 * Here you should setup everything that only needs to be initialized after
	 * startup or on reload e.g loading the keyword list.
	 */
	abstract void initializeNeededData();

	/**
	 * Works on a single data object.
	 * @param json The object containing the articles.
	 */
	abstract void consumeObject(JSONObject json);

	/**
	 * Method to subscribe to some topics
	 * @param topics - the topics to subscribe to
	 */
	public void subscribeToList(Collection<String> topics){
		consumer.subscribe(topics);
	}
	
	/**
	 * Sets the reload flag.
	 */
	public void reload() {
		reload = true;
	}

	@Override
	public void run() {
		initializeNeededData();
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(10);
			for (ConsumerRecord<String, String> record : records) {
				// Decode JSON String
				JSONObject json = null;
				try {
					json = new JSONObject(record.value());
				} catch (JSONException e) {
					continue;
				}
				consumeObject(json);
			}
			// If the reload flag is set re-init the data and continue running
			if (reload) {
				initializeNeededData();
				reload = false;
			}
		}
	}
}