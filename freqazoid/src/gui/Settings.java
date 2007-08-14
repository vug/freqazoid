package gui;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
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
public class Settings extends JFrame {
	private ResourceManager rm;
	private JComboBox comboBoxInputDevice;
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

	public Settings(ResourceManager rm) {
		this.rm = rm;
		initGUI();
	}
	
	private void initGUI() {
		try {
			{
				getContentPane().setLayout(null);
				this.setTitle("Settings");
			}
			{
				buttonApply = new JButton();
				getContentPane().add(buttonApply);
				buttonApply.setText("Apply");
				buttonApply.setBounds(210, 112, 77, 28);
			}
			{
				buttonOK = new JButton();
				getContentPane().add(buttonOK);
				buttonOK.setText("OK");
				buttonOK.setBounds(147, 112, 63, 28);
			}
			{
				buttonCancel = new JButton();
				getContentPane().add(buttonCancel);
				buttonCancel.setText("Cancel");
				buttonCancel.setBounds(287, 112, 77, 28);
			}
			{
				panelAudioDevices = new JPanel();
				FlowLayout panelAudioDevicesLayout = new FlowLayout();
				panelAudioDevicesLayout.setAlignment(FlowLayout.LEFT);
				panelAudioDevicesLayout.setVgap(0);
				panelAudioDevices.setLayout(panelAudioDevicesLayout);
				getContentPane().add(panelAudioDevices);
				panelAudioDevices.setBounds(7, 7, 133, 105);
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
				panelDFTParametersLayout.setHgap(2);
				panelDFTParameters.setLayout(panelDFTParametersLayout);
				getContentPane().add(panelDFTParameters);
				panelDFTParameters.setBounds(140, 7, 224, 105);
				panelDFTParameters.setBorder(BorderFactory.createTitledBorder("DFT Parameters"));
				{
					labelWindowType = new JLabel();
					panelDFTParameters.add(labelWindowType);
					labelWindowType.setText("Window Type:");
					labelWindowType.setPreferredSize(new java.awt.Dimension(84, 14));
				}
				{
					ComboBoxModel comboBoxWindowTypeModel = new DefaultComboBoxModel(
						new String[] { "Rectengular", "Hann", "Hamming",
								"Kallman", "Blackmann" });
					comboBoxWindowType = new JComboBox();
					panelDFTParameters.add(comboBoxWindowType);
					comboBoxWindowType.setModel(comboBoxWindowTypeModel);
					comboBoxWindowType.setBounds(259, 35, 98, 21);
				}
				{
					labelNHops = new JLabel();
					panelDFTParameters.add(labelNHops);
					labelNHops.setText("# of Hops");
					labelNHops.setBounds(357, 7, 84, 14);
				}
				{
					labelWindowSize = new JLabel();
					panelDFTParameters.add(labelWindowSize);
					labelWindowSize.setText("Window Size");
					labelWindowSize.setBounds(357, 42, 98, 14);
				}
			}
			{
				this.setSize(379, 237);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
