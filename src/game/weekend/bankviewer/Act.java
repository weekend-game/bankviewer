package game.weekend.bankviewer;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

/**
 * Линейка меню и инструментальная линейка. А так же все Actions которые есть в
 * приложении.
 */
public class Act {

	/**
	 * Создать объект линейки меню и инструменальной линейки.
	 * 
	 * @param viewer приложение.
	 * @param filer  работв с фвйлом.
	 */
	public Act(BankViewer viewer, Filer filer) {
		// Actions могут использоваться как в меню, так и в инструментальной линейке.
		// Так что лучше их создать и запомнить один раз в конструкторе.
		this.open = getActOpen(filer);
		this.exit = getActExit(viewer);
		this.about = getActAbout(viewer);
	}

	/**
	 * Получить меню приложения.
	 * 
	 * @return меню приложения.
	 */
	public JMenuBar getMenuBar() {
		JMenuBar menu = new JMenuBar();

		JMenu fileMenu = new JMenu("Файл");
		fileMenu.add(open);
		fileMenu.add(new JSeparator());
		fileMenu.add(exit);

		JMenu helpMenu = new JMenu("Помощь");
		helpMenu.add(about);

		menu.add(fileMenu);
		menu.add(helpMenu);

		return menu;
	}

	/**
	 * Получить Toolbar приложения.
	 * 
	 * @return Toolbar приложения.
	 */
	public JToolBar getToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setRollover(true);
		toolBar.setFloatable(false);

		toolBar.add(open);

		return toolBar;
	}

	private ImageIcon getImageIcon(String fileName) {
		return new ImageIcon(getClass().getResource(BankViewer.IMAGE_PATH + fileName));
	}

	/**
	 * "Открыть..."
	 * 
	 * @param filer управление файлом программы.
	 * 
	 * @return Action "Открыть..."
	 */
	@SuppressWarnings("serial")
	public AbstractAction getActOpen(Filer filer) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Открыть...");
				putValue(Action.SHORT_DESCRIPTION, "Открыть файл");
				putValue(Action.SMALL_ICON, getImageIcon("open.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				File newFile = filer.showDialogue();
				if (newFile != null) {
					filer.open(newFile);
				}
			}
		};

	}

	/**
	 * "Выход из программы"
	 * 
	 * @param viewer приложение.
	 *
	 * @return Action "Выход из программы"
	 */
	@SuppressWarnings("serial")
	public AbstractAction getActExit(BankViewer viewer) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Выход");
				putValue(Action.SHORT_DESCRIPTION, "Выход из программы");
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				viewer.close();
			}
		};
	}

	/**
	 * "О программе"
	 * 
	 * @param viewer приложение.
	 * 
	 * @return Action "О программе"
	 */
	@SuppressWarnings("serial")
	public AbstractAction getActAbout(BankViewer viewer) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "О программе...");
				putValue(Action.SHORT_DESCRIPTION, "О программе");
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				String str = "\n" + BankViewer.APP_NAME + "\n" + BankViewer.APP_VERSION + "\n" + BankViewer.APP_COPYRIGHT
						+ "\n\n" + BankViewer.APP_OTHER + "\n\n";
				viewer.inf(str, "О программе...");
			}
		};
	}

	private AbstractAction open;
	private AbstractAction exit;
	private AbstractAction about;
}
