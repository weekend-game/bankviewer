package game.weekend.bankviewer;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 * Application status bar.
 */
public class StatusBar {

	/**
	 * Create an application status bar.
	 */
	public StatusBar() {
		panel = new JPanel();
		panel.setLayout(new GridLayout(1, 3));

		text1 = new JTextField("");
		text1.setEditable(false);
		panel.add(text1);

		text2 = new JTextField("");
		text2.setEditable(false);
		panel.add(text2);

		message = new JTextField("");
		message.setEditable(false);
		panel.add(message);
	}

	/**
	 * Get the panel on which the status bar is based.
	 * 
	 * @return status bar panel.
	 */
	public JPanel getPanel() {
		return panel;
	}

	/**
	 * Display text in the first section of the status bar.
	 * 
	 * @param text text.
	 */
	public void showText1(String text) {
		text1.setText(text);
	}

	/**
	 * Display text in the second section of the status bar.
	 * 
	 * @param text text.
	 */
	public void showText2(String text) {
		text2.setText(text);
	}

	/**
	 * Display a message in the status bar and keep it displayed for five (DELAY)
	 * seconds.
	 * 
	 * @param mes message text.
	 */
	public void showMessage(String mes) {
		if (tmr != null && tmr.isRunning()) {
			tmr.stop();
		}
		message.setText(mes);
		tmr = new Timer(DELAY, new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				message.setText("");
			}
		});
		tmr.start();
	}

	private static final int DELAY = 5000; // Message display time
	private JPanel panel;
	private JTextField text1;
	private JTextField text2;
	private JTextField message;
	private Timer tmr;
}
