package gui;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.sampled.Mixer;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import realtimesound.AudioAnalyser;

import math.DFT;
import math.TwoWayMismatch;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class Settings extends JFrame implements ActionListener {

	private static final long serialVersionUID = -4289544378146824904L;
	private ResourceManager rm;
	private JComboBox comboBoxInputDevice;
	private JComboBox ComboBoxNHops;
	private JComboBox comboBoxWindowSize;
	private JLabel labelRefreshRate;
	private JPanel panelDisplay;
	private JTextField textRefreshRate;
	private JPanel panelDFTParameters;
	private JPanel panelAudioDevices;
	private JComboBox comboBoxWindowType;
	private JButton buttonApply;
	private JLabel labelNHops;
	private JLabel labelWindowSize;
	private JLabel labelWindowType;
	private JLabel labelOutputDevice;
	private JLabel labelInputDevice;
	private JComboBox comboBoxOutputDevices;
	
	private int displayRefreshRate;
	private JLabel labelF0Min;
	private JPanel panelF0Range;
	private JTextField textMPr;
	private JLabel labelMPr;
	private JTextField textMPq;
	private JLabel labelMPq;
	private JTextField textMPp;
	private JLabel labelMPp;
	private JPanel panelMeasuredToPredicted;
	private JTextField textBlockSize;
	private JLabel labelBlockSize;
	private JTextField textBufferSize;
	private JLabel labelInputOutputBufferSize;
	private JPanel panelSelectDevice;
	private JPanel panelBuffers;
	private JComboBox comboBoxAlgorithm;
	private JPanel panelAlgorithm;
	private JTextField textTotalRho;
	private JLabel labelTotalRho;
	private JPanel panelTotalError;
	private JPanel panelThresholdLevel;
	private JLabel labelF0Max;
	private JTextField textPMr;
	private JLabel labelPMr;
	private JTextField textPMq;
	private JLabel labelPMq;
	private JTextField textPMp;
	private JLabel labelPMp;
	private JPanel panelPredictedToMeasured;
	private JTextField textF0Max;
	private JTextField textF0Min;
	private JCheckBox checkBoxAntiAlias;
	private JTextField textThreshold;
	private JPanel panelTwoWayMismatch;
	private JTabbedPane tabbedPane1;
	private int windowSize;
	private int numberOfHops;
	
	private String[] inputInfos;
	private String[] outputInfos;

	public Settings(ResourceManager rm) {
		this.rm = rm;
		displayRefreshRate = rm.getDisplay().getRefreshRate();
//		windowType = rm.getAudioEngine().getAudioAnalyser().getWindowType();
		windowSize = rm.getAudioEngine().getAudioAnalyser().getWindowSize();
		numberOfHops = rm.getAudioEngine().getAudioAnalyser().getNumberOfHops();
		
		Mixer.Info[] mixerInfos = rm.getAudioEngine().getInputMixerInfos();
		inputInfos = new String[mixerInfos.length];
		for (int i = 0; i < mixerInfos.length; i++) {
			 inputInfos[i] = mixerInfos[i].getName();
		}
		
		mixerInfos = rm.getAudioEngine().getOutputMixerInfos();
		outputInfos = new String[mixerInfos.length];
		for (int i = 0; i < mixerInfos.length; i++) {
			 outputInfos[i] = mixerInfos[i].getName();
		}
		
		initGUI();
	}
	
	private void initGUI() {
		try {
			{
				getContentPane().setLayout(null);
				this.setTitle("Settings");
				this.setSize(512, 265);
				this.setLocation(500,100);
			}
			{
				tabbedPane1 = new JTabbedPane();
				getContentPane().add(tabbedPane1);
				
				tabbedPane1.setBounds(0, 0, 504, 231);
				{
					panelAudioDevices = new JPanel();
					tabbedPane1.addTab("AudioDevices", null, panelAudioDevices, null);
					panelAudioDevices.setLayout(null);
					panelAudioDevices.setBounds(378, 133, 273, 175);
					panelAudioDevices.setPreferredSize(new java.awt.Dimension(462, 175));
					{
						panelBuffers = new JPanel();
						FlowLayout panelBuffersLayout = new FlowLayout();
						panelBuffersLayout.setAlignment(FlowLayout.LEFT);
						panelAudioDevices.add(panelBuffers);
						panelBuffers.setBounds(238, 0, 182, 140);
						panelBuffers.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder(""), "Buffers", TitledBorder.LEADING, TitledBorder.TOP));
						panelBuffers.setLayout(panelBuffersLayout);
						{
							labelInputOutputBufferSize = new JLabel();
							panelBuffers.add(labelInputOutputBufferSize);
							labelInputOutputBufferSize.setText("Buffer Size:");
							labelInputOutputBufferSize.setPreferredSize(new java.awt.Dimension(81, 14));
						}
						{
							textBufferSize = new JTextField();
							panelBuffers.add(textBufferSize);
							int bufferSize = rm.getAudioEngine().getBufferSize();
							textBufferSize.setText(
									Integer.toString(bufferSize)
							);
							textBufferSize.setPreferredSize(new java.awt.Dimension(64, 20));
							textBufferSize
								.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									int bufferSize = Integer.parseInt( textBufferSize.getText() );
									rm.getAudioEngine().setBufferSize(bufferSize);
								}
								});
						}
						{
							labelBlockSize = new JLabel();
							panelBuffers.add(labelBlockSize);
							labelBlockSize.setText("Block Size:");
							labelBlockSize.setPreferredSize(new java.awt.Dimension(80, 14));
						}
						{
							textBlockSize = new JTextField();
							panelBuffers.add(textBlockSize);
							int blockSize = rm.getAudioEngine().getBlockSize();
							textBlockSize.setText(
									Integer.toString(blockSize)
							);
							textBlockSize.setPreferredSize(new java.awt.Dimension(64, 20));
							textBlockSize
								.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									int blockSize = Integer.parseInt( textBlockSize.getText() );
									rm.getAudioEngine().setBlockSize(blockSize);
								}
								});
						}
					}
					{
						panelSelectDevice = new JPanel();	
						FlowLayout panelSelectDeviceLayout = new FlowLayout();
						panelSelectDeviceLayout.setAlignment(FlowLayout.LEFT);
						panelSelectDevice.setLayout(panelSelectDeviceLayout);
						panelAudioDevices.add(panelSelectDevice);
						panelSelectDevice.setBounds(0, 0, 238, 140);
						panelSelectDevice.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder(""), "Select Device", TitledBorder.LEADING, TitledBorder.TOP));
						
						{
							labelInputDevice = new JLabel();
							panelSelectDevice.add(labelInputDevice);
							labelInputDevice.setText("Input Device:");
							labelInputDevice.setBounds(14, 14, 119, 14);
						}
						{
							ComboBoxModel comboBoxInputDeviceModel = new DefaultComboBoxModel(
							//new String[] { "AudioDevice1", "Input2", "Soundcard3"}
								inputInfos);
							comboBoxInputDevice = new JComboBox();
							panelSelectDevice.add(comboBoxInputDevice);
							comboBoxInputDevice
								.setModel(comboBoxInputDeviceModel);
							comboBoxInputDevice.setBounds(14, 28, 210, 21);
							comboBoxInputDevice.setPreferredSize(new java.awt.Dimension(212, 21));
							comboBoxInputDevice.addActionListener(this);
						}
						{
							labelOutputDevice = new JLabel();
							panelSelectDevice.add(labelOutputDevice);
							labelOutputDevice.setText("Output Device:");
							labelOutputDevice.setBounds(14, 56, 119, 14);
						}
						{
							ComboBoxModel comboBoxOutputDevicesModel = new DefaultComboBoxModel(
							//						new String[] { "Outputcard1", "Item Two" }
								outputInfos);
							comboBoxOutputDevices = new JComboBox();
							panelSelectDevice.add(comboBoxOutputDevices);
							comboBoxOutputDevices
								.setModel(comboBoxOutputDevicesModel);
							comboBoxOutputDevices.setBounds(14, 73, 210, 21);
							comboBoxOutputDevices.setPreferredSize(new java.awt.Dimension(213, 21));
							comboBoxOutputDevices.addActionListener(this);
						}
					}
				}
				{
					panelDFTParameters = new JPanel();
					tabbedPane1.addTab("DFT Parameters", null, panelDFTParameters, null);
					panelDFTParameters.setLayout(null);
					panelDFTParameters.setBounds(266, 7, 231, 105);
					panelDFTParameters.setBorder(BorderFactory
						.createTitledBorder("DFT Parameters"));
					panelDFTParameters.setPreferredSize(new java.awt.Dimension(258, 205));
					{
						labelWindowType = new JLabel();
						panelDFTParameters.add(labelWindowType);
						labelWindowType.setText("Window Type:");
						labelWindowType
							.setPreferredSize(new java.awt.Dimension(102, 14));
						labelWindowType.setBounds(10, 26, 102, 14);
					}
					{
						ComboBoxModel comboBoxWindowTypeModel = new DefaultComboBoxModel(
							new String[] { "Rectangular", "Hann", "Hamming", "Blackmann" });
						comboBoxWindowType = new JComboBox();
						panelDFTParameters.add(comboBoxWindowType);
						comboBoxWindowType.setModel(comboBoxWindowTypeModel);
						comboBoxWindowType.setBounds(112, 21, 98, 21);
						comboBoxWindowType.setSelectedIndex( rm.getAudioEngine().getAudioAnalyser().getWindowType() );
						comboBoxWindowType
							.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								switch (comboBoxWindowType.getSelectedIndex()) {
								case 0:	
									rm.getAudioEngine().getAudioAnalyser().setWindowType( DFT.RECTANGULAR );
									break;
								case 1:
									rm.getAudioEngine().getAudioAnalyser().setWindowType( DFT.HANN );
								case 2:									 
									rm.getAudioEngine().getAudioAnalyser().setWindowType( DFT.HAMMING );
									break;								
								case 3:
									rm.getAudioEngine().getAudioAnalyser().setWindowType( DFT.BLACKMANN );
									break;									
								default:
									break;
								}
								System.out
									.println(comboBoxWindowType.getSelectedIndex()
											+ " " + 
											comboBoxWindowType.getSelectedItem());
							}
							});
					}
					{
						labelWindowSize = new JLabel();
						panelDFTParameters.add(labelWindowSize);
						labelWindowSize.setText("Window Size");
						labelWindowSize.setBounds(10, 56, 105, 14);
					}
					{
						ComboBoxModel comboBoxWindowSizeModel = new DefaultComboBoxModel(
							new String[] { "128", "256", "512", "1024", "2048",
									"4096", "8192" });
						comboBoxWindowSize = new JComboBox();
						panelDFTParameters.add(comboBoxWindowSize);
						comboBoxWindowSize.setModel(comboBoxWindowSizeModel);
						String size = Integer.toString(windowSize);
						comboBoxWindowSize.setSelectedItem(size);
						comboBoxWindowSize.setBounds(112, 56, 98, 21);
						comboBoxWindowSize
							.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									String str = ((String) ((JComboBox) evt
										.getSource()).getSelectedItem());
									windowSize = Integer.parseInt(str);
								}
							});
					}
					{
						labelNHops = new JLabel();
						panelDFTParameters.add(labelNHops);
						labelNHops.setText("# of Hops");
						labelNHops.setBounds(10, 91, 105, 14);
					}
					{
						ComboBoxModel ComboBoxNHopsModel = new DefaultComboBoxModel(
							new String[] { "1", "2", "3", "4", "5", "6", "7",
									"8" });
						ComboBoxNHops = new JComboBox();
						panelDFTParameters.add(ComboBoxNHops);
						ComboBoxNHops.setModel(ComboBoxNHopsModel);
						ComboBoxNHops.setSelectedIndex(numberOfHops - 1);
						ComboBoxNHops.setBounds(112, 91, 98, 21);
						ComboBoxNHops.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								int n = ((JComboBox) evt.getSource())
									.getSelectedIndex();
								numberOfHops = n + 1;
							}
						});
					}
					{
						buttonApply = new JButton();
						panelDFTParameters.add(buttonApply);
						buttonApply.setText("Apply");
						buttonApply.setBounds(7, 119, 77, 28);
						buttonApply.addActionListener(this);
					}
				}
				{
					panelDisplay = new JPanel();
					tabbedPane1.addTab("Display", null, panelDisplay, null);
					panelDisplay.setBounds(392, 112, 133, 84);
					panelDisplay.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createTitledBorder(""),
						"Display",
						TitledBorder.LEADING,
						TitledBorder.TOP));
					panelDisplay.setLayout(null);
					{
						textRefreshRate = new JTextField();
						panelDisplay.add(textRefreshRate);
						textRefreshRate.setText(Integer
							.toString(displayRefreshRate));
						textRefreshRate.setBounds(14, 28, 42, 21);
						textRefreshRate.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								String input = ((JTextField) evt.getSource())
									.getText();
								displayRefreshRate = Integer.parseInt(input);
								rm.getDisplay().setRefreshRate(displayRefreshRate);
							}
						});
					}
					{
						labelRefreshRate = new JLabel();
						panelDisplay.add(labelRefreshRate);
						labelRefreshRate.setText("Refresh Rate (ms)");
						labelRefreshRate.setBounds(63, 28, 140, 21);
					}
					{
						checkBoxAntiAlias = new JCheckBox("Anti-aliased lines", true);
						panelDisplay.add(checkBoxAntiAlias);
						checkBoxAntiAlias.setBounds(42, 56, 168, 21);
						checkBoxAntiAlias
							.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								rm.getDisplay().setAntialiased( checkBoxAntiAlias.isSelected() );
							}
							});
					}
				}
				{
					panelTwoWayMismatch = new JPanel();
					tabbedPane1.addTab(
						"Two-Way Mismatch",
						null,
						panelTwoWayMismatch,
						null);
					panelTwoWayMismatch.setLayout(null);
					{
						panelPredictedToMeasured = new JPanel();
						FlowLayout panelPredictedToMeasuredLayout = new FlowLayout();
						panelPredictedToMeasuredLayout.setAlignment(FlowLayout.LEFT);						
						panelTwoWayMismatch.add(panelPredictedToMeasured);
						panelPredictedToMeasured.setBounds(224, 7, 224, 56);
						panelPredictedToMeasured.setBorder(BorderFactory.createTitledBorder("Predicted-to-measured Mismatch"));
						panelPredictedToMeasured.setLayout(panelPredictedToMeasuredLayout);
						{
							labelPMp = new JLabel();
							panelPredictedToMeasured.add(labelPMp);
							labelPMp.setText("p:");
							labelPMp.setPreferredSize(new java.awt.Dimension(14, 14));
						}
						{
							textPMp = new JTextField();
							panelPredictedToMeasured.add(textPMp);
							textPMp.setText(Double.toString(TwoWayMismatch.getPmP()));
							textPMp.setPreferredSize(new java.awt.Dimension(39, 20));
							textPMp.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									TwoWayMismatch.setPmP(Double.parseDouble(textPMp.getText()));
								}
							});
						}
						{
							labelPMq = new JLabel();
							panelPredictedToMeasured.add(labelPMq);
							labelPMq.setText("q:");
							labelPMq.setPreferredSize(new java.awt.Dimension(14, 14));
						}
						{
							textPMq = new JTextField();
							panelPredictedToMeasured.add(textPMq);
							textPMq.setText(Double.toString(TwoWayMismatch.getPmQ()));
							textPMq.setPreferredSize(new java.awt.Dimension(39, 20));
							textPMq.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									TwoWayMismatch.setPmQ(Double.parseDouble(textPMq.getText()));
								}
							});
						}
						{
							labelPMr = new JLabel();
							panelPredictedToMeasured.add(labelPMr);
							labelPMr.setText("r:");
							labelPMr.setPreferredSize(new java.awt.Dimension(14, 14));
							labelPMr.setDoubleBuffered(true);
						}
						{
							textPMr = new JTextField();
							panelPredictedToMeasured.add(textPMr);
							textPMr.setText(Double.toString(TwoWayMismatch.getPmR()));
							textPMr.setPreferredSize(new java.awt.Dimension(44, 20));
							textPMr.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									TwoWayMismatch.setPmR(Double.parseDouble(textPMr.getText()));
								}
							});
						}
					}
					{
						panelMeasuredToPredicted = new JPanel();
						panelTwoWayMismatch.add(panelMeasuredToPredicted);
						FlowLayout jPanel1Layout = new FlowLayout();
						jPanel1Layout.setAlignment(FlowLayout.LEFT);
						panelMeasuredToPredicted.setBorder(BorderFactory.createTitledBorder("Measured-to-predicted Mismatch"));
						panelMeasuredToPredicted.setLayout(jPanel1Layout);
						panelMeasuredToPredicted.setBounds(224, 63, 224, 56);
						{
							labelMPp = new JLabel();
							panelMeasuredToPredicted.add(labelMPp);
							labelMPp.setText("p:");
							labelMPp.setPreferredSize(new java.awt.Dimension(
								14,
								14));
						}
						{
							textMPp = new JTextField();
							panelMeasuredToPredicted.add(textMPp);
							textMPp.setText(Double.toString(TwoWayMismatch.getMpP()));
							textMPp.setPreferredSize(new java.awt.Dimension(39, 20));
							textMPp.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									TwoWayMismatch.setMpP(Double.parseDouble(textMPp.getText()));
								}
							});
						}
						{
							labelMPq = new JLabel();
							panelMeasuredToPredicted.add(labelMPq);
							labelMPq.setText("q:");
							labelMPq.setPreferredSize(new java.awt.Dimension(
								14,
								14));
						}
						{
							textMPq = new JTextField();
							panelMeasuredToPredicted.add(textMPq);
							textMPq.setText(Double.toString(TwoWayMismatch.getMpQ()));
							textMPq.setPreferredSize(new java.awt.Dimension(39, 20));
							textMPq.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									TwoWayMismatch.setMpQ(Double.parseDouble(textMPq.getText()));
								}
							});
						}
						{
							labelMPr = new JLabel();
							panelMeasuredToPredicted.add(labelMPr);
							labelMPr.setText("r:");
							labelMPr.setPreferredSize(new java.awt.Dimension(
								14,
								14));
							labelMPr.setDoubleBuffered(true);
						}
						{
							textMPr = new JTextField();
							panelMeasuredToPredicted.add(textMPr);
							textMPr.setText(Double.toString(TwoWayMismatch.getMpR()));
							textMPr.setPreferredSize(new java.awt.Dimension(41, 20));
							textMPr.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									TwoWayMismatch.setMpR(Double.parseDouble(textMPr.getText()));
								}
							});
						}
					}
					{
						panelF0Range = new JPanel();
						FlowLayout panelF0RangeLayout = new FlowLayout();
						panelF0RangeLayout.setAlignment(FlowLayout.LEFT);
						panelF0Range.setLayout(panelF0RangeLayout);
						panelTwoWayMismatch.add(panelF0Range);
						panelF0Range.setBounds(7, 63, 217, 56);
						panelF0Range.setBorder(BorderFactory.createTitledBorder("Fundamental Frequency Range (Hz)"));
						{
							labelF0Min = new JLabel();
							panelF0Range.add(labelF0Min);
							labelF0Min.setText("min:");
						}
						{
							textF0Min = new JTextField();
							panelF0Range.add(textF0Min);
							textF0Min.setText(									
									Double.toString(rm.getAudioEngine().getAudioAnalyser().getF0Min())
											);
							textF0Min.setBounds(7, 63, 49, 21);
							textF0Min.setPreferredSize(new java.awt.Dimension(59, 21));
							textF0Min.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									double f0Min = Double.parseDouble(textF0Min.getText());
									rm.getAudioEngine().getAudioAnalyser().setF0Min(f0Min);
								}
							});
						}
						{
							labelF0Max = new JLabel();
							panelF0Range.add(labelF0Max);
							labelF0Max.setText("max:");
						}
						{
							textF0Max = new JTextField();
							panelF0Range.add(textF0Max);
							textF0Max.setText(
									Double.toString(rm.getAudioEngine().getAudioAnalyser().getF0Max())		
							);
							textF0Max.setBounds(63, 63, 49, 21);
							textF0Max.setPreferredSize(new java.awt.Dimension(58, 21));
							textF0Max.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									double f0Max = Double.parseDouble( textF0Max.getText() );
									rm.getAudioEngine().getAudioAnalyser().setF0Max(f0Max);
								}
							});
						}
					}
					{
						panelThresholdLevel = new JPanel();
						FlowLayout panelThresholdLevelLayout = new FlowLayout();
						panelThresholdLevelLayout.setAlignment(FlowLayout.LEFT);
						panelThresholdLevel.setLayout(panelThresholdLevelLayout);
						panelTwoWayMismatch.add(panelThresholdLevel);
						panelThresholdLevel.setBounds(7, 7, 217, 56);
						panelThresholdLevel.setBorder(BorderFactory.createTitledBorder("Threshold Level"));
						{
							textThreshold = new JTextField();
							panelThresholdLevel.add(textThreshold);
							textThreshold.setText("10");
							textThreshold.setPreferredSize(new java.awt.Dimension(56, 21));
							textThreshold
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent evt) {
										//								System.out.println(Double.parseDouble( textThreshold.getText() ));
										rm.getAudioEngine().getAudioAnalyser()
											.setPeakThreshold(
												Double
													.parseDouble(textThreshold
														.getText()));
									}
								});
						}
					}
					{
						panelTotalError = new JPanel();
						FlowLayout panelTotalErrorLayout = new FlowLayout();
						panelTotalErrorLayout.setAlignment(FlowLayout.LEFT);
						panelTotalError.setLayout(panelTotalErrorLayout);
						panelTwoWayMismatch.add(panelTotalError);
						panelTotalError.setBounds(224, 119, 224, 56);
						panelTotalError.setBorder(BorderFactory.createTitledBorder("Total TWM Error"));
						{
							labelTotalRho = new JLabel();
							panelTotalError.add(labelTotalRho);
							labelTotalRho.setText("rho:");
						}
						{
							textTotalRho = new JTextField();
							panelTotalError.add(textTotalRho);
							textTotalRho.setText(Double.toString(TwoWayMismatch.getRho()));
							textTotalRho.setPreferredSize(new java.awt.Dimension(43, 20));
							textTotalRho
								.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									TwoWayMismatch.setRho(Double.parseDouble(textTotalRho.getText()));
								}
								});
						}
					}
					{
						panelAlgorithm = new JPanel();
						FlowLayout panelAlgorithmLayout = new FlowLayout();
						panelAlgorithmLayout.setAlignment(FlowLayout.LEFT);
						panelAlgorithm.setLayout(panelAlgorithmLayout);
						panelTwoWayMismatch.add(panelAlgorithm);
						panelAlgorithm.setBounds(7, 119, 217, 56);
						panelAlgorithm.setBorder(BorderFactory.createTitledBorder("Detection Algorithm"));
						{
							ComboBoxModel comboBoxAlgorithmModel = new DefaultComboBoxModel(
								new String[] { "Two-Way Mismatch", "Left Most Peak", "Peak with highest amp" });
							comboBoxAlgorithm = new JComboBox();
							panelAlgorithm.add(comboBoxAlgorithm);
							comboBoxAlgorithm.setModel(comboBoxAlgorithmModel);
							comboBoxAlgorithm.setPreferredSize(new java.awt.Dimension(187, 20));
							comboBoxAlgorithm
								.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent evt) {
									switch(comboBoxAlgorithm.getSelectedIndex()) {
									case 0:
										rm.getAudioEngine().getAudioAnalyser().setMethod(AudioAnalyser.TWM);
										break;
									case 1:
										rm.getAudioEngine().getAudioAnalyser().setMethod(AudioAnalyser.LEFT_MOST_PEAK);
										break;
									case 2:
										rm.getAudioEngine().getAudioAnalyser().setMethod(AudioAnalyser.HIGHEST_PEAK);
										break;
									}
								}
								});
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void apply() {		
		rm.getAudioEngine().getAudioAnalyser().setWindowSizeAndNumberOfHops(windowSize, numberOfHops);
	}

	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource()==buttonApply) {
			apply();
		}		
		else if( ae.getSource()==comboBoxInputDevice ) {
			//System.out.println(comboBoxInputDevice.getSelectedItem().toString());
			rm.getAudioEngine().changeInputAndOutputLine(comboBoxInputDevice.getSelectedIndex(), comboBoxOutputDevices.getSelectedIndex());
		}
		else if( ae.getSource() == comboBoxOutputDevices ) {
			rm.getAudioEngine().changeInputAndOutputLine(comboBoxInputDevice.getSelectedIndex(), comboBoxOutputDevices.getSelectedIndex());
		}
		
	}

}
