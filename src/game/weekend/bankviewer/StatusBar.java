package game.weekend.bankviewer;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 * Строка состояния приложения.
 */
public class StatusBar {

	/**
	 * Создать строку состояния приложения.
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
	 * Получить панель, на основе которой реализована строка состояния.
	 * 
	 * @return панель строки состояния.
	 */
	public JPanel getPanel() {
		return panel;
	}

	/**
	 * Вывести текст в первой секции строки состояния.
	 * 
	 * @param txt текст.
	 */
	public void showText1(String txt) {
		text1.setText(txt);
	}

	/**
	 * Вывести текст во второй секции строки состояния.
	 * 
	 * @param txt текст.
	 */
	public void showText2(String txt) {
		text2.setText(txt);
	}

	/**
	 * Вывести сообщение в строке состояния и отображать его в течении пяти (DELAY)
	 * секунд.
	 * 
	 * @param mes текст сообщения.
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

	private static final int DELAY = 5000; // Время отображения сообщения
	private JPanel panel;
	private JTextField text1;
	private JTextField text2;
	private JTextField message;
	private Timer tmr;
}
