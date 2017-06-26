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
	JTextArea speedLayerText = new JTextArea(30, 80);

	private MainFrame(){
		this.setTitle("TUD Seminar");
    	this.setSize(1500, 620);
    	this.setResizable(false);
    	this.setLocation(50, 50);
    	this.setVisible(true);
    	
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
    	batchParams.setPreferredSize(new Dimension(609, 159));
    	
    	speedParams.setBorder(border);
    	speedParams.setEditable(false);
    	speedParams.setPreferredSize(new Dimension(609, 550));
    	
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
	
	public static MainFrame getInstance(){
		if(instance == null)
			instance = new MainFrame();
		return instance;
	}
	
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
	
	private void startStream(){
		prod.startProd();
	}
	
	private void stopStream(){
		prod.stopProd();
	}
	
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
		String text = "Speed Layer Calculation with values:";
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
}
