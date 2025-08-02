package game.weekend.bankviewer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

/**
 * Приложение BankViewer.
 */
public class BankViewer {

	/** Название приложения */
	public static final String APP_NAME = "BankViewer";

	/** Версия */
	public static final String APP_VERSION = "Версия 01.30 от 02.08.2025";

	/** Copyright */
	public static final String APP_COPYRIGHT = "(c) Weekend Game, 2025";

	/** Назначение */
	public static final String APP_OTHER = "Просмотр банковских выписок";

	/** Путь к пиктограммам */
	public static final String IMAGE_PATH = "/game/weekend/bankviewer/images/";

	/** Строка состояния */
	public static final StatusBar status = new StatusBar();

	/**
	 * Создать приложение. Создаётся окно приложения, объекты необходимые для работы
	 * и элементы управления окна.
	 */
	public BankViewer() {
		// Хранитель настроек между сеансами работы приложения
		Proper.read(APP_NAME);

		// Frame приложения
		frame = new JFrame(APP_NAME);
		makeJFrame();

		// JEditorPane для отображения банковской выписки
		pane = new JEditorPane();
		makeJEditorPane();

		// Хранитель имен последних открытых файлов (пяти, например)
		lastFiles = new LastFiles(5);

		// Поиск в открытом файле
		Finder finder = new Finder(pane, frame);

		// Работа с файлами
		filer = new Filer(this, lastFiles, finder);

		// Look and Feels
		LaF laf = new LaF();

		// Работа с меню и инструментальной линейкой
		act = new Act(this, filer, finder, laf, lastFiles);

		// Меню
		frame.setJMenuBar(act.getMenuBar());

		// Меню по правой клавише мыши
		JPopupMenu popupMenu = act.getPopupMenu();
		pane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				if (me.isPopupTrigger()) {
					popupMenu.show(me.getComponent(), me.getX(), me.getY());
				}
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				if (me.isPopupTrigger()) {
					popupMenu.show(me.getComponent(), me.getX(), me.getY());
				}
			}
		});

		// Инструментальная линейка
		toolbarOn = Proper.getProperty("ToolbarON", "TRUE").equalsIgnoreCase("TRUE") ? true : false;
		toolbar = act.getToolBar();
		if (toolbarOn)
			frame.getContentPane().add(toolbar, BorderLayout.NORTH);

		// Строка состояния
		statusbarOn = Proper.getProperty("StatusbarON", "TRUE").equalsIgnoreCase("TRUE") ? true : false;
		statusbar = BankViewer.status.getPanel();
		if (statusbarOn)
			frame.getContentPane().add(statusbar, BorderLayout.SOUTH);

		laf.setupComponents(frame, popupMenu, toolbar, statusbar);
		laf.setLookAndFeel(laf.getLookAndFeel());
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

		// Перехватываю выделение/сброс выделения текста отображенной выписки
		pane.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent ce) {
				// Если имеется выделенный текст, то разрешить Copy иначе заблокировать.
				act.setEnabledCopy(pane.getSelectionStart() != pane.getSelectionEnd());
			}
		});

		// Перехватываю событие Drag and Drop. На самом деле Drop.
		new DropTarget(pane, new DropTargetListener() {

			public void dragEnter(DropTargetDragEvent e) {
			}

			public void dragExit(DropTargetEvent e) {
			}

			public void dragOver(DropTargetDragEvent e) {
			}

			public void dropActionChanged(DropTargetDragEvent e) {
			}

			public void drop(DropTargetDropEvent e) {
				try {
					e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					List<?> list = (List<?>) e.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					File file = (File) list.get(0);
					filer.open(file);
				} catch (Exception ignored) {
				}
			}
		});
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
	 * Отображать инструментальную линейку.
	 * 
	 * @param toolbarON true - отображать, false - не отображать
	 */
	public void setTooolbarON(boolean toolbarON) {
		this.toolbarOn = toolbarON;
		if (toolbarOn)
			frame.getContentPane().add(toolbar, BorderLayout.NORTH);
		else
			frame.getContentPane().remove(toolbar);

		frame.setVisible(true);
		Proper.setProperty("ToolbarON", toolbarOn ? "TRUE" : "FALSE");
	}

	/**
	 * Отображать строку состояния.
	 * 
	 * @param statusbarON true - отображать, false - не отображать
	 */
	public void setStatusbarON(boolean statusbarOn) {
		this.statusbarOn = statusbarOn;
		if (statusbarOn)
			frame.getContentPane().add(statusbar, BorderLayout.SOUTH);
		else
			frame.getContentPane().remove(statusbar);

		frame.setVisible(true);
		Proper.setProperty("StatusbarON", statusbarOn ? "TRUE" : "FALSE");
	}

	/**
	 * Закрыть приложение.
	 * 
	 * Сохраняет всё, что нужно сохранить для восстановления при следующем запуске
	 */
	public void close() {
		Proper.saveBounds(frame);
		frame.dispose();
		lastFiles.save();
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
	 * Переотобразить меню "Файл".
	 */
	public void refreshMenuFile() {
		act.refreshMenuFile();
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
	 * Получить JEditorPane.
	 * 
	 * @return JEditorPane.
	 */
	public JEditorPane getPane() {
		return pane;
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
	private JToolBar toolbar;
	private JPanel statusbar;
	private Act act;
	private Filer filer;
	private LastFiles lastFiles;

	private boolean toolbarOn;
	private boolean statusbarOn;
}
