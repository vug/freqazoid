package gui;
import java.awt.BorderLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;

import javax.swing.JFrame;
import javax.swing.JLabel;

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
				ComboBoxModel comboBoxInputDeviceModel = new DefaultComboBoxModel(
					new String[] { "AudioDevice1", "Input2", "Soundcard3" });
				comboBoxInputDevice = new JComboBox();
				getContentPane().add(comboBoxInputDevice);
				comboBoxInputDevice.setModel(comboBoxInputDeviceModel);
				comboBoxInputDevice.setBounds(7, 21, 119, 21);				
			}
			{
				ComboBoxModel comboBoxOutputDevicesModel = new DefaultComboBoxModel(
					new String[] { "Outputcard1", "Item Two" });
				comboBoxOutputDevices = new JComboBox();
				getContentPane().add(comboBoxOutputDevices);
				comboBoxOutputDevices.setModel(comboBoxOutputDevicesModel);
				comboBoxOutputDevices.setBounds(7, 56, 119, 21);
			}
			{
				labelInputDevice = new JLabel();
				getContentPane().add(labelInputDevice);
				labelInputDevice.setText("Input Device");
				labelInputDevice.setBounds(7, 7, 119, 14);
			}
			{
				labelOutputDevice = new JLabel();
				getContentPane().add(labelOutputDevice);
				labelOutputDevice.setText("Output Device");
				labelOutputDevice.setBounds(7, 42, 119, 14);
			}
			{
				labelWindowType = new JLabel();
				getContentPane().add(labelWindowType);
				labelWindowType.setText("Window Type");
				labelWindowType.setBounds(147, 7, 98, 14);
			}
			{
				labelWindowSize = new JLabel();
				getContentPane().add(labelWindowSize);
				labelWindowSize.setText("Window Size");
				labelWindowSize.setBounds(357, 42, 98, 14);
			}
			{
				labelNHops = new JLabel();
				getContentPane().add(labelNHops);
				labelNHops.setText("# of Hops");
				labelNHops.setBounds(357, 7, 84, 14);
			}
			{
				buttonApply = new JButton();
				getContentPane().add(buttonApply);
				buttonApply.setText("Apply");
				buttonApply.setBounds(301, 84, 77, 28);
			}
			{
				buttonOK = new JButton();
				getContentPane().add(buttonOK);
				buttonOK.setText("OK");
				buttonOK.setBounds(238, 84, 63, 28);
			}
			{
				buttonCancel = new JButton();
				getContentPane().add(buttonCancel);
				buttonCancel.setText("Cancel");
				buttonCancel.setBounds(378, 84, 77, 28);
			}
			{
				ComboBoxModel comboBoxWindowTypeModel = new DefaultComboBoxModel(
					new String[] { "Rectengular", "Hann", "Hamming", "Kallman", "Blackmann" });
				comboBoxWindowType = new JComboBox();
				getContentPane().add(comboBoxWindowType);
				comboBoxWindowType.setModel(comboBoxWindowTypeModel);
				comboBoxWindowType.setBounds(147, 21, 98, 21);
			}
			{
				this.setSize(491, 160);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
