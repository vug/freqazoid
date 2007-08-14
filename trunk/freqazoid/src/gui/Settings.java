package gui;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

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
	private JButton buttonCancel;
	private JButton buttonOK;
	private JButton buttonApply;
	private JLabel labelNHops;
	private JLabel labelWindowSize;
	private JLabel labelWindowType;
	private JLabel labelOutputDevice;
	private JLabel labelInputDevice;
	private JComboBox comboBoxOutputDevices;
	
	private int displayRefreshRate;
	private int windowType;
	private int windowSize;
	private int numberOfHops;

	public Settings(ResourceManager rm) {
		this.rm = rm;
		displayRefreshRate = rm.getDisplay().getRefreshRate();
		windowType = rm.getAudioEngine().getAudioAnalyser().getWindowType();
		windowSize = rm.getAudioEngine().getAudioAnalyser().getWindowSize();
		numberOfHops = rm.getAudioEngine().getAudioAnalyser().getNumberOfHops();
//		System.out.println(numberOfHops);
		
		initGUI();
	}
	
	private void initGUI() {
		try {
			{
				getContentPane().setLayout(null);
				this.setTitle("Settings");
				this.setSize(400, 265);
				this.setLocation(500,100);
			}
			{
				buttonApply = new JButton();
				getContentPane().add(buttonApply);
				buttonApply.setText("Apply");
				buttonApply.setBounds(210, 161, 77, 28);
				buttonApply.addActionListener(this);
			}
			{
				buttonOK = new JButton();
				getContentPane().add(buttonOK);
				buttonOK.setText("OK");
				buttonOK.setBounds(147, 161, 63, 28);
				buttonOK.addActionListener(this);
			}
			{
				buttonCancel = new JButton();
				getContentPane().add(buttonCancel);
				buttonCancel.setText("Cancel");
				buttonCancel.setBounds(287, 161, 77, 28);
			}
			{
				panelAudioDevices = new JPanel();
				FlowLayout panelAudioDevicesLayout = new FlowLayout();
				panelAudioDevicesLayout.setAlignment(FlowLayout.LEFT);
				panelAudioDevicesLayout.setVgap(2);
				panelAudioDevices.setLayout(panelAudioDevicesLayout);
				getContentPane().add(panelAudioDevices);
				panelAudioDevices.setBounds(7, 7, 133, 119);
				panelAudioDevices.setBorder(BorderFactory.createTitledBorder("Audio Devices"));
				{
					labelInputDevice = new JLabel();
					panelAudioDevices.add(labelInputDevice);
					labelInputDevice.setText("Input Device:");
					labelInputDevice.setBounds(7, 7, 119, 14);
					labelInputDevice.setPreferredSize(new java.awt.Dimension(82, 14));
				}
				{
					ComboBoxModel comboBoxInputDeviceModel = new DefaultComboBoxModel(
						new String[] { "AudioDevice1", "Input2", "Soundcard3" });
					comboBoxInputDevice = new JComboBox();
					panelAudioDevices.add(comboBoxInputDevice);
					comboBoxInputDevice.setModel(comboBoxInputDeviceModel);
					comboBoxInputDevice.setBounds(7, 21, 119, 21);
				}
				{
					labelOutputDevice = new JLabel();
					panelAudioDevices.add(labelOutputDevice);
					labelOutputDevice.setText("Output Device:");
					labelOutputDevice.setBounds(7, 42, 119, 14);
					labelOutputDevice.setPreferredSize(new java.awt.Dimension(89, 14));
				}
				{
					ComboBoxModel comboBoxOutputDevicesModel = new DefaultComboBoxModel(
						new String[] { "Outputcard1", "Item Two" });
					comboBoxOutputDevices = new JComboBox();
					panelAudioDevices.add(comboBoxOutputDevices);
					comboBoxOutputDevices.setModel(comboBoxOutputDevicesModel);
					comboBoxOutputDevices.setBounds(7, 56, 119, 21);
				}
			}
			{
				panelDFTParameters = new JPanel();
				FlowLayout panelDFTParametersLayout = new FlowLayout();
				panelDFTParametersLayout.setAlignment(FlowLayout.LEFT);
				panelDFTParametersLayout.setVgap(2);
				panelDFTParameters.setLayout(panelDFTParametersLayout);
				getContentPane().add(panelDFTParameters);
				panelDFTParameters.setBounds(140, 7, 231, 105);
				panelDFTParameters.setBorder(BorderFactory.createTitledBorder("DFT Parameters"));
				{
					labelWindowType = new JLabel();
					panelDFTParameters.add(labelWindowType);
					labelWindowType.setText("Window Type:");
					labelWindowType.setPreferredSize(new java.awt.Dimension(102, 14));
				}
				{
					ComboBoxModel comboBoxWindowTypeModel = new DefaultComboBoxModel(
						new String[] { "Rectangular", "Hann", "Hamming",
								"Kallman", "Blackmann" });
					comboBoxWindowType = new JComboBox();
					panelDFTParameters.add(comboBoxWindowType);
					comboBoxWindowType.setModel(comboBoxWindowTypeModel);
					comboBoxWindowType.setBounds(259, 35, 98, 21);
				}
				{
					labelWindowSize = new JLabel();
					panelDFTParameters.add(labelWindowSize);
					labelWindowSize.setText("Window Size");
					labelWindowSize.setBounds(357, 42, 98, 14);
					labelWindowSize.setPreferredSize(new java.awt.Dimension(102, 14));
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
					comboBoxWindowSize.setPreferredSize(new java.awt.Dimension(
						78,
						20));
					comboBoxWindowSize.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							String str = ((String) ((JComboBox) evt.getSource())
								.getSelectedItem());
							windowSize = Integer.parseInt(str);
						}
					});
				}
				{
					labelNHops = new JLabel();
					panelDFTParameters.add(labelNHops);
					labelNHops.setText("# of Hops");
					labelNHops.setBounds(357, 7, 84, 14);
					labelNHops.setPreferredSize(new java.awt.Dimension(102, 14));
				}
				{
					ComboBoxModel ComboBoxNHopsModel = new DefaultComboBoxModel(
						new String[] { "1", "2", "3", "4", "5", "6", "7", "8" });
					ComboBoxNHops = new JComboBox();
					panelDFTParameters.add(ComboBoxNHops);
					ComboBoxNHops.setModel(ComboBoxNHopsModel);
					ComboBoxNHops.setSelectedIndex( numberOfHops-1 );
					ComboBoxNHops.setPreferredSize(new java.awt.Dimension(79, 20));
					ComboBoxNHops.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							int n = ((JComboBox)evt.getSource()).getSelectedIndex();
							numberOfHops = n+1;
						}
					});
				}
			}
			{
				panelDisplay = new JPanel();
				FlowLayout panelDisplayLayout = new FlowLayout();
				panelDisplayLayout.setAlignment(FlowLayout.LEFT);
				panelDisplayLayout.setVgap(2);
				getContentPane().add(panelDisplay);
				panelDisplay.setBounds(7, 126, 133, 84);
				panelDisplay.setBorder(BorderFactory.createTitledBorder(BorderFactory.createTitledBorder(""), "Display", TitledBorder.LEADING, TitledBorder.TOP));
				panelDisplay.setLayout(panelDisplayLayout);
				{
					textRefreshRate = new JTextField();
					panelDisplay.add(textRefreshRate);
					textRefreshRate.setText(Integer
						.toString(displayRefreshRate));
					textRefreshRate.setBounds(14, 154, 63, 21);
					textRefreshRate.setPreferredSize(new java.awt.Dimension(
						39,
						20));
					textRefreshRate.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							String input = ((JTextField) evt.getSource())
								.getText();
							displayRefreshRate = Integer.parseInt(input);
						}
					});
				}
				{
					labelRefreshRate = new JLabel();
					panelDisplay.add(labelRefreshRate);
					labelRefreshRate.setText("Refresh Rate (ms)");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void apply() {
		rm.getDisplay().setRefreshRate(displayRefreshRate);
		rm.getAudioEngine().getAudioAnalyser().setWindowSizeAndNumberOfHops(windowSize, numberOfHops);
	}

	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource()==buttonApply) {
			apply();
		}
		else if( ae.getSource()==buttonOK ) {
			apply();
			this.setVisible(false);
		}
		
	}

}
