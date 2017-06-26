package TUD.Seminar.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import TUD.Seminar.BatchLayer.BatchOperation;
import TUD.Seminar.Kafka.Consumer.Consumer;
import TUD.Seminar.Kafka.Producer.SimulatedProducer;
import TUD.Seminar.SimulatedStream.Categories;

public class MainFrame extends JFrame {
	
	private static MainFrame instance = null;
	 
	BatchOperation batchOp = new BatchOperation();
	SimulatedProducer prod;
	Consumer consumer;
	
	String startStreamText = "Start Streaming";
	String stopStreamText = "Stop Streaming";
	
	JButton streamOnOff = new JButton();
	JTextPane batchParams = new JTextPane();
	JTextPane speedParams = new JTextPane();

	/**
	 * Constructor is private so objects are only created by using the getInstance() method
	 */
	private MainFrame(){
		this.setTitle("TUD Seminar");
		//TODO: hier kannst du auch die Größen verändern, ist auch einfach random
    	this.setSize(1500, 250);
    	this.setResizable(false);
    	this.setLocation(50, 50);
    	this.setVisible(true);
    	
    	//TODO: hier musst du evtl das Layout ändern, hab einfach erstmal irgendeins genommen
    	this.setLayout(new FlowLayout());
    	
    	streamOnOff.setText("Start Streaming");
    	streamOnOff.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonClicked();
			}
		});
    	
    	Border border = BorderFactory.createLineBorder(Color.BLACK);
    	batchParams.setBorder(border);
    	batchParams.setEditable(false);
    	//TODO: 609 und 159 kommt daher weil des Feld diese Größe annimmt wenn man keine fixe Größe einstellt
    	batchParams.setPreferredSize(new Dimension(609, 159));
    	
    	speedParams.setBorder(border);
    	speedParams.setEditable(false);
    	speedParams.setPreferredSize(new Dimension(609, 159));
    	
    	//Add all components
    	this.add(streamOnOff);
    	this.add(batchParams);
    	this.add(speedParams);
   
    	
    	//Set a timer
    	Timer timer = new Timer();
		Calendar date = Calendar.getInstance();
		date.set(Calendar.AM_PM, Calendar.AM);
		date.set(Calendar.HOUR, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);

		//Schedule timer to run every day at 0:00
		timer.schedule(batchOp, date.getTime(),
				// Time to wait before next action in milliseconds
				1000l * 60l);// * 60l * 24l);
    	
    	consumer = new Consumer("1");
    	consumer.subscribeToList(Arrays.asList("orders"));
    	consumer.start();
    	prod = new SimulatedProducer("");
    	prod.start();
	}
	
	/**
	 * Singleton implementation so theres only one instance of this object.
	 * @return - the instance of the JFrame object
	 */
	public static MainFrame getInstance(){
		if(instance == null)
			instance = new MainFrame();
		return instance;
	}
	
	/**
	 * Handles a click on the start/stop stream button
	 */
	private void buttonClicked(){
		
		if(streamOnOff.getText().equals(startStreamText)){
			streamOnOff.setText(stopStreamText);
			startStream();
		}
		else if(streamOnOff.getText().equals(stopStreamText)){
			streamOnOff.setText(startStreamText);
			stopStream();
		}
	}
	
	/**
	 * Starts the simulated stream
	 */
	private void startStream(){
		prod.startProd();
	}
	
	/**
	 * Stops the simulated stream
	 */
	private void stopStream(){
		prod.stopProd();
	}
	
	/**
	 * Sets the text of the field, where the speed layer calculation should be displayed, using a StyledDocument
	 * @param params - an array of the calculated params
	 */
	public void setSpeedLayerText(double[] params){	
		StyledDocument doc = speedParams.getStyledDocument();
		
		SimpleAttributeSet title = new SimpleAttributeSet();
		StyleConstants.setBold(title, true);
		StyleConstants.setFontSize(title, 18);
		
		SimpleAttributeSet cats = new SimpleAttributeSet();
		StyleConstants.setBold(cats, true);
		StyleConstants.setFontSize(cats, 14);
		
		SimpleAttributeSet values = new SimpleAttributeSet();
		StyleConstants.setFontSize(values, 14);

		
		String date = new Date() + ": ";
		String text = "Adjusted Calculation with values:";
		try {
			//first remove all previous text
			doc.remove(0, doc.getLength());
			
			doc.insertString(0, date, title);
			doc.insertString(doc.getLength(), text, title);
			doc.insertString(doc.getLength(), System.lineSeparator(), title);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		Categories[] categories = Categories.values();
		for(int i = 0; i < Categories.getCategoryCount(); i++){
			try {
				doc.insertString(doc.getLength(), categories[i].toString() + ": ", cats);
				doc.insertString(doc.getLength(), String.valueOf(params[i]), values);
				if(i != Categories.getCategoryCount() - 1)
					doc.insertString(doc.getLength(), System.lineSeparator(), values);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		
		speedParams.setStyledDocument(doc);;
	}
	
	/**
	 * Sets the text of the field, where the batch layer calculation should be displayed, using a StyledDocument.
	 * @param params - an array of the calculated params
	 */
	public void setBatchLayerText(double[] params){	
		StyledDocument doc = batchParams.getStyledDocument();
		
		SimpleAttributeSet title = new SimpleAttributeSet();
		StyleConstants.setBold(title, true);
		StyleConstants.setFontSize(title, 18);
		
		SimpleAttributeSet cats = new SimpleAttributeSet();
		StyleConstants.setBold(cats, true);
		StyleConstants.setFontSize(cats, 14);
		
		SimpleAttributeSet values = new SimpleAttributeSet();
		StyleConstants.setFontSize(values, 14);

		
		String date = new Date() + ": ";
		String text = "Batch Layer Calculation with values:";
		try {
			//first remove all previous text
			doc.remove(0, doc.getLength());
			
			doc.insertString(0, date, title);
			doc.insertString(doc.getLength(), text, title);
			doc.insertString(doc.getLength(), System.lineSeparator(), title);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		
		Categories[] categories = Categories.values();
		for(int i = 0; i < Categories.getCategoryCount(); i++){
			try {
				doc.insertString(doc.getLength(), categories[i].toString() + ": ", cats);
				doc.insertString(doc.getLength(), String.valueOf(params[i]), values);
				if(i != Categories.getCategoryCount() - 1)
					doc.insertString(doc.getLength(), System.lineSeparator(), values);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		
		batchParams.setStyledDocument(doc);;
	}
	
	public void reloadConsumer(double[] betas, int batchSize){
		consumer.reload(betas, batchSize);
	}
}
