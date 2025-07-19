package game.weekend.bankviewer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Приложение BankViewer.
 */
public class BankViewer {

	/** Название приложения */
	public static final String APP_NAME = "BankViewer";

	/** Версия */
	public static final String APP_VERSION = "Версия 00.00 от 19.07.2025";

	/** Copyright */
	public static final String APP_COPYRIGHT = "(c) Weekend Game, 2025";

	/** Назначение */
	public static final String APP_OTHER = "Просмотр банковских выписок";

	/** Путь к пиктограммам */
	public static final String IMAGE_PATH = "/game/weekend/bankviewer/images/";

	/**
	 * Создать приложение. Создаётся окно приложения, объекты необходимые для работы
	 * и элементы управления окна.
	 */
	public BankViewer() {
		// Хранитель настроек между сеансами работы приложения
		Proper.read(APP_NAME);

		// Frame приложения
		this.frame = new JFrame(APP_NAME);
		makeJFrame();

		// JEditorPane для отображения банковской выписки
		this.pane = new JEditorPane();
		makeJEditorPane();

		// Работа с файлами
		Filer filer = new Filer(this);

		// Работа с меню и инструментальной линейкой
		this.act = new Act(this, filer);

		// Меню
		this.frame.setJMenuBar(act.getMenuBar());

		// Инструментальная линейка
		this.frame.getContentPane().add(act.getToolBar(), BorderLayout.NORTH);
	}

	/**
	 * Настройка основного окна приложения.
	 */
	private void makeJFrame() {
		// Ничего не делать при попытке закрыть окно, но
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		// перехватить это событие
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// и вызвать этот метод. В нем будут сохраняться настройки
				close();
			}
		});

		// Для ContentPane ставлю менеджер расположения BorderLayout
		// (в середине будет JEditorPane для отображения выписки, сверху toolbar)
		Container cp = frame.getContentPane();
		cp.setLayout(new BorderLayout());

		// Восстанавливаю расположение и размеры фрейма, которые он имел в прошлом
		// сеансе работы
		Proper.setBounds(frame);
	}

	/**
	 * Настройка панели отображения выписки.
	 */
	private void makeJEditorPane() {
		// Панель нередактируемая
		pane.setEditable(false);

		// Помещаю её в JScrollPane
		JScrollPane spane = new JScrollPane();
		spane.getViewport().add(pane);

		// и размещаю JScrollPane в центр ContentPane Frame-а
		frame.getContentPane().add(spane, BorderLayout.CENTER);
	}

	/**
	 * Запустить приложение.
	 *
	 * @param args не используется.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				BankViewer bv = new BankViewer();
				bv.getFrame().setVisible(true);
			}
		});
	}

	/**
	 * Закрыть приложение.
	 * 
	 * Сохраняет всё, что нужно сохранить для восстановления при следующем запуске
	 */
	public void close() {
		Proper.saveBounds(frame);
		frame.dispose();
		Proper.save();
	}

	/**
	 * Отобразить файл в JEditorPane главного окна.
	 * 
	 * @param file отображаемый файл
	 */
	public void showFile(File file) {
		String s = "file:" + file.getPath();
		try {
			pane.setPage(new URL(s));
		} catch (IOException ignored) {
		}
		pane.requestFocus();
	}

	/**
	 * Получить основное окно приложения.
	 * 
	 * @return основное окно приложения.
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Выдать сообщение об ошибке.
	 * 
	 * @param message текст сообщения.
	 */
	public void err(String message) {
		JOptionPane.showMessageDialog(frame, message, APP_NAME, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Выдать информационное сообщение.
	 * 
	 * @param message текст сообщения.
	 */
	public void inf(String message) {
		JOptionPane.showMessageDialog(frame, message, APP_NAME, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Выдать информационное сообщение.
	 * 
	 * @param message текст сообщения.
	 * @param title   заголовок окна.
	 */
	public void inf(String message, String title) {
		JOptionPane.showMessageDialog(frame, message, title, JOptionPane.INFORMATION_MESSAGE);
	}

	private JFrame frame;
	private JEditorPane pane;
	private Act act;
}
