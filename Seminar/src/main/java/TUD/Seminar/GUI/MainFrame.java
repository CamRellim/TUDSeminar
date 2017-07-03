package TUD.Seminar.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import TUD.Seminar.BatchLayer.BatchOperation;
import TUD.Seminar.Constants.Constants;
import TUD.Seminar.Kafka.Consumer.Consumer;
import TUD.Seminar.Kafka.Producer.SimulatedProducer;
import TUD.Seminar.SimulatedStream.Categories;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private static MainFrame instance = null;
	boolean running = false;

	BatchOperation batchOp = new BatchOperation();
	SimulatedProducer prod;
	Consumer consumer;

	String startStreamText = "Start Streaming";
	String stopStreamText = "Stop Streaming";
	String batchTitle = "Batch Regression Params:" + "\n";
	String speedTitle = "Adjusted Regression Params:" + "\n";
	String deviationTitle = "Deviation of both Regression Params:" + "\n";

	JPanel flowPanelTop = new JPanel();
	JPanel flowPanelCenter = new JPanel();
	JTextPane bottomText = new JTextPane();

	JButton streamOnOff = new JButton();
	JButton config = new JButton();

	JTextPane batchParams = new JTextPane();
	JTextPane speedParams = new JTextPane();
	JTextPane deviationParams = new JTextPane();

	/**
	 * Constructor is private so objects are only created by using the
	 * getInstance() method
	 */
	private MainFrame() {
		this.setTitle("TUD Seminar");
		this.setSize(1500, 1000);
		this.setResizable(false);
		this.setLocation(100, 100);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		this.setLayout(new BorderLayout());

		streamOnOff.setText("Start Streaming");
		streamOnOff.setPreferredSize(new Dimension(150, 50));
		streamOnOff.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				startNStopStream();
			}
		});
		
		config.setText("Settings");
		config.setPreferredSize(new Dimension(150, 50));
		config.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				configure();
			}
		});

		Border border = BorderFactory.createLineBorder(Color.BLACK);
		batchParams.setBorder(border);
		batchParams.setEditable(false);
		batchParams.setPreferredSize(new Dimension(609, 318));

		speedParams.setBorder(border);
		speedParams.setEditable(false);
		speedParams.setPreferredSize(new Dimension(609, 318));

		deviationParams.setBorder(border);
		deviationParams.setEditable(false);
		deviationParams.setPreferredSize(new Dimension(609, 318));

		setupTextPanes();

		// Add all components to FlowLayout
		flowPanelCenter.add(batchParams);
		flowPanelCenter.add(speedParams);
		flowPanelCenter.add(deviationParams);

		// Style for the FlowLayout panel
		flowPanelCenter.setBackground(new Color(35, 47, 62));

		// Style for TitleText
		SimpleAttributeSet center = new SimpleAttributeSet();

		// Costumize bottomText
		bottomText.setBackground(new Color(35, 47, 62));
		bottomText.setEditable(false);
		bottomText.setPreferredSize(new Dimension(300, 175));
		bottomText.setFont(new Font(Font.SANS_SERIF, 3, 48));

		// Style for BottomText
		StyledDocument styleDoc2 = bottomText.getStyledDocument();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		StyleConstants.setForeground(center, new Color(255, 153, 0));
		styleDoc2.setParagraphAttributes(0, styleDoc2.getLength(), center, false);

		// Style for FlowLayout Top
		flowPanelTop.setBackground(new Color(35, 47, 62));
		flowPanelTop.add(streamOnOff);
		flowPanelTop.add(config);

		// Add components to BorderLayout
		this.add(flowPanelTop, BorderLayout.PAGE_START);
		this.add(flowPanelCenter, BorderLayout.CENTER);
		this.add(bottomText, BorderLayout.PAGE_END);

		// Show GUI 
		this.setVisible(true);
		
		if (loadConfig()) {
			// Set a timer
			Timer timer = new Timer();
			Calendar date = Calendar.getInstance();
			date.set(Calendar.AM_PM, Calendar.AM);
			date.set(Calendar.HOUR, 0);
			date.set(Calendar.MINUTE, 0);
			date.set(Calendar.SECOND, 0);
			date.set(Calendar.MILLISECOND, 0);

			// Schedule timer to run every day at 0:00
			timer.schedule(batchOp, date.getTime(),
					// Time to wait before next action in milliseconds
					Constants.BATCH_FREQUENCY);

			consumer = new Consumer("1");
			consumer.subscribeToList(Arrays.asList("orders"));
			consumer.start();
			prod = new SimulatedProducer("");
			prod.start();
		}
		else
			System.exit(0);
	}

	/**
	 * Singleton implementation so theres only one instance of this object.
	 * 
	 * @return - the instance of the JFrame object
	 */
	public static MainFrame getInstance() {
		if (instance == null)
			instance = new MainFrame();
		return instance;
	}

	/**
	 * Handles a click on the start/stop stream button
	 */
	private void startNStopStream() {

		if (streamOnOff.getText().equals(startStreamText)) {
			running = true;
			streamOnOff.setText(stopStreamText);
			startStream();
		} else if (streamOnOff.getText().equals(stopStreamText)) {
			running = false;
			streamOnOff.setText(startStreamText);
			stopStream();
		}
	}

	/**
	 * Starts the simulated stream
	 */
	private void startStream() {
		prod.startProd();
	}

	/**
	 * Stops the simulated stream
	 */
	private void stopStream() {
		prod.stopProd();
	}

	/**
	 * Sets the TextPane titles
	 */
	public void setupTextPanes() {
		SimpleAttributeSet title = new SimpleAttributeSet();
		StyleConstants.setBold(title, true);
		StyleConstants.setFontSize(title, 26);

		StyledDocument batchDoc = batchParams.getStyledDocument();
		StyledDocument speedDoc = speedParams.getStyledDocument();
		StyledDocument deviationDoc = deviationParams.getStyledDocument();

		try {
			batchDoc.insertString(0, batchTitle, title);
			speedDoc.insertString(0, speedTitle, title);
			deviationDoc.insertString(0, deviationTitle, title);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the text of the field, where the speed layer calculation should be
	 * displayed, using a StyledDocument
	 * 
	 * @param params
	 *            - an array of the adjusted regression
	 */
	public void setSpeedLayerText(double[] params) {
		StyledDocument doc = speedParams.getStyledDocument();

		SimpleAttributeSet title = new SimpleAttributeSet();
		StyleConstants.setBold(title, true);
		StyleConstants.setFontSize(title, 26);

		SimpleAttributeSet cats = new SimpleAttributeSet();
		StyleConstants.setBold(cats, true);
		StyleConstants.setFontSize(cats, 22);

		SimpleAttributeSet values = new SimpleAttributeSet();
		StyleConstants.setFontSize(values, 22);

		String date = "Calculated " + new Date() + ": " + "\n";
		try {
			// first remove all previous text
			doc.remove(speedTitle.length(), doc.getLength() - speedTitle.length());

			doc.insertString(doc.getLength(), date, title);
			doc.insertString(doc.getLength(), System.lineSeparator(), title);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		Categories[] categories = Categories.values();
		for (int i = 0; i < Categories.getCategoryCount(); i++) {
			try {
				doc.insertString(doc.getLength(), categories[i].toString() + ": ", cats);
				doc.insertString(doc.getLength(), String.valueOf(params[i]), values);
				if (i != Categories.getCategoryCount() - 1)
					doc.insertString(doc.getLength(), System.lineSeparator(), values);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

		speedParams.setStyledDocument(doc);
	}

	/**
	 * Sets the text of the field, where the batch layer calculation should be
	 * displayed, using a StyledDocument.
	 * 
	 * @param params
	 *            - an array of the batch regression
	 */
	public void setBatchLayerText(double[] params) {
		StyledDocument doc = batchParams.getStyledDocument();

		SimpleAttributeSet title = new SimpleAttributeSet();
		StyleConstants.setBold(title, true);
		StyleConstants.setFontSize(title, 26);

		SimpleAttributeSet cats = new SimpleAttributeSet();
		StyleConstants.setBold(cats, true);
		StyleConstants.setFontSize(cats, 22);

		SimpleAttributeSet values = new SimpleAttributeSet();
		StyleConstants.setFontSize(values, 22);

		Date batchDate = new Date();
		String date = "Calculated " + batchDate + ": " + "\n";
		
		String nextBatchCalcDate = "Next Batch Caculation expected " + new Date(batchDate.getTime() + (Constants.BATCH_FREQUENCY));
		bottomText.setText(nextBatchCalcDate);
		try {
			// first remove all previous text
			doc.remove(batchTitle.length(), doc.getLength() - batchTitle.length());

			doc.insertString(doc.getLength(), date, title);
			doc.insertString(doc.getLength(), System.lineSeparator(), title);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		Categories[] categories = Categories.values();
		for (int i = 0; i < Categories.getCategoryCount(); i++) {
			try {
				doc.insertString(doc.getLength(), categories[i].toString() + ": ", cats);
				doc.insertString(doc.getLength(), String.valueOf(params[i]), values);
				if (i != Categories.getCategoryCount() - 1)
					doc.insertString(doc.getLength(), System.lineSeparator(), values);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

		batchParams.setStyledDocument(doc);
	}

	/**
	 * Sets the text of the field, where the deviation of the batch and the
	 * adjusted regression params should be displayed, using a StyledDocument
	 * 
	 * @param batchCalc
	 *            - an array of the batch regression
	 * @param speedCalc
	 *            - an array of the adjusted regression
	 */
	public void setDeviationText(double[] batchCalc, double[] speedCalc) {
		StyledDocument doc = deviationParams.getStyledDocument();

		SimpleAttributeSet title = new SimpleAttributeSet();
		StyleConstants.setBold(title, true);
		StyleConstants.setFontSize(title, 26);

		SimpleAttributeSet cats = new SimpleAttributeSet();
		StyleConstants.setBold(cats, true);
		StyleConstants.setFontSize(cats, 22);

		SimpleAttributeSet values = new SimpleAttributeSet();
		StyleConstants.setFontSize(values, 22);

		String date = "Calculated " + new Date() + ": " + "\n";
		try {
			// first remove all previous text
			doc.remove(deviationTitle.length(), doc.getLength() - deviationTitle.length());

			doc.insertString(doc.getLength(), date, title);
			doc.insertString(doc.getLength(), System.lineSeparator(), title);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		Categories[] categories = Categories.values();
		for (int i = 0; i < Categories.getCategoryCount(); i++) {
			try {
				doc.insertString(doc.getLength(), categories[i].toString() + ": ", cats);
				doc.insertString(doc.getLength(), String.valueOf(batchCalc[i] - speedCalc[i]), values);
				if (i != Categories.getCategoryCount() - 1)
					doc.insertString(doc.getLength(), System.lineSeparator(), values);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

		deviationParams.setStyledDocument(doc);
	}

	private void configure() {
		JTextField orderSize = new JTextField(5);
		JTextField batchFrequency = new JTextField(5);

		JPanel settingsPanel = new JPanel();
		settingsPanel.add(new JLabel("ORDER_SIZE:"));
		settingsPanel.add(orderSize);
		settingsPanel.add(Box.createHorizontalStrut(15)); // a spacer
		settingsPanel.add(new JLabel("BATCH_FREQUENCY (hours):"));
		settingsPanel.add(batchFrequency);

		int result = JOptionPane.showConfirmDialog(this, settingsPanel, "Settings",
				JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			try {
				int os = Integer.parseInt(orderSize.getText().trim());
				long bf = Long.parseLong(batchFrequency.getText().trim());
				//TODO: CHANGE TO HOURS bf converted from hours to milliseconds
				long bfToMs = bf * 1000l * 60l * 60l;
				
				writeFile(os, bfToMs);
				JOptionPane.showMessageDialog(this, "Changes will be applied after restarting the Application", "Success", JOptionPane.INFORMATION_MESSAGE);
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, "Not a valid input", "Error", JOptionPane.ERROR_MESSAGE);
				configure();				
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "File could not be written", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void writeFile(int orderSize, long batchFrequency) throws IOException {
        String fileName = "properties/config";

        FileWriter fileWriter = new FileWriter(fileName);

        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write("ORDER_SIZE");
        bufferedWriter.write(" = ");
        bufferedWriter.write(String.valueOf(orderSize));
        bufferedWriter.newLine();
        bufferedWriter.write("BATCH_FREQUENCY");
        bufferedWriter.write(" = ");
        bufferedWriter.write(String.valueOf(batchFrequency));

        bufferedWriter.close();
	}

	private boolean loadConfig() {
		Properties properties = new Properties();
		FileInputStream stream;
		try {
			stream = new FileInputStream("properties/config");
			properties.load(stream);
			stream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		try {
			Constants.ORDER_SIZE = Integer.parseInt(properties.getProperty("ORDER_SIZE"));
			Constants.BATCH_FREQUENCY = Long.parseLong(properties.getProperty("BATCH_FREQUENCY"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public void reloadConsumer(double[] betas, int batchSize) {
		consumer.reload(betas, batchSize, running);
	}
	
	public static void main(String[] args) {
		getInstance();
	}
}
