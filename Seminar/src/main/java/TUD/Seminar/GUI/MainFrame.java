package TUD.Seminar.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.JPanel;
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
	
	JPanel flowPanel = new JPanel();
	JTextPane titleText = new JTextPane();
	JTextPane bottomText = new JTextPane();
	
	JButton streamOnOff = new JButton();
	JTextPane batchParams = new JTextPane();
	JTextPane speedParams = new JTextPane();

	/**
	 * Constructor is private so objects are only created by using the getInstance() method
	 */
	private MainFrame(){
		this.setTitle("TUD Seminar");
    	this.setSize(1500, 750);
    	this.setResizable(false);
    	this.setLocation(100, 100);
    	this.setVisible(true);
    	
    	this.setLayout(new BorderLayout());
    	
    	streamOnOff.setText("Start Streaming");
    	streamOnOff.setPreferredSize(new Dimension(150, 50));
    	streamOnOff.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonClicked();
			}
		});
    	
    	Border border = BorderFactory.createLineBorder(Color.BLACK);
    	batchParams.setBorder(border);
    	batchParams.setEditable(false);
    	batchParams.setPreferredSize(new Dimension(609, 318));
    	
    	speedParams.setBorder(border);
    	speedParams.setEditable(false);
    	speedParams.setPreferredSize(new Dimension(609, 318));
    	
    	//Add all components to FlowLayout
    	flowPanel.add(streamOnOff);
    	flowPanel.add(batchParams);
    	flowPanel.add(speedParams);
    	
    	//Style for the FlowLayout panel
    	flowPanel.setBackground(new Color(35, 47, 62));
    	
    	//Costumize Title
    	titleText.setText("Lorem ipsum dolor sit amet, consetetur sadipscing elitr");
    	titleText.setBackground(new Color(35, 47, 62));
    	titleText.setEditable(false);
    	titleText.setPreferredSize(new Dimension(300, 175));
    	titleText.setFont(new Font(Font.SANS_SERIF, 3, 48));
    	
    	//Style for TitleText
    	StyledDocument styleDoc = titleText.getStyledDocument();
    	SimpleAttributeSet center = new SimpleAttributeSet();
    	StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
    	StyleConstants.setForeground(center, new Color(255, 153, 0));
    	styleDoc.setParagraphAttributes(0, styleDoc.getLength(), center, false);
    	
    	//Costumize bottomText
    	bottomText.setText("At vero eos et accusam et justo duo dolores et ea rebum.");
    	bottomText.setBackground(new Color(35, 47, 62));
    	bottomText.setEditable(false);
    	bottomText.setPreferredSize(new Dimension(300, 175));
    	bottomText.setFont(new Font(Font.SANS_SERIF, 3, 48));
    	
    	//Style for BottomText
    	StyledDocument styleDoc2 = bottomText.getStyledDocument();
    	StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
    	StyleConstants.setForeground(center, new Color(255, 153, 0));
    	styleDoc2.setParagraphAttributes(0, styleDoc2.getLength(), center, false);
    	
    	//Add components to BorderLayout
    	this.add(titleText, BorderLayout.PAGE_START);
    	this.add(flowPanel, BorderLayout.CENTER);
    	this.add(bottomText, BorderLayout.PAGE_END);
   
    	
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
		StyleConstants.setFontSize(title, 26);
		
		SimpleAttributeSet cats = new SimpleAttributeSet();
		StyleConstants.setBold(cats, true);
		StyleConstants.setFontSize(cats, 22);
		
		SimpleAttributeSet values = new SimpleAttributeSet();
		StyleConstants.setFontSize(values, 22);

		
		String date = new Date() + ": " + "\n";
		String text = "Adjusted Calculation with values:" + "\n";
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
		StyleConstants.setFontSize(title, 26);
		
		SimpleAttributeSet cats = new SimpleAttributeSet();
		StyleConstants.setBold(cats, true);
		StyleConstants.setFontSize(cats, 22);
		
		SimpleAttributeSet values = new SimpleAttributeSet();
		StyleConstants.setFontSize(values, 22);

		
		String date = new Date() + ": " + "\n";
		String text = "Batch Layer Calculation with values:" + "\n";
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
