package game.weekend.bankviewer;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

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
	public Act(BankViewer viewer, Filer filer, Finder finder, LaF laf, LastFiles lastFiles) {
		this.filer = filer;
		this.laf = laf;
		this.lastFiles = lastFiles;

		// Actions могут использоваться как в меню, так и в инструментальной линейке.
		// Так что лучше их создать и запомнить один раз в конструкторе.
		open = getActOpen(filer);
		exit = getActExit(viewer);

		cut = getActCut();
		copy = getActCopy(viewer.getPane());
		past = getActPaste();
		delete = getActDelete();
		selectAll = getActSelectAll(viewer.getPane());
		find = getActFind(finder);
		findForward = getActFindForward(finder);
		findBack = getActFindBack(finder);

		about = getActAbout(viewer);
	}

	/**
	 * Получить меню приложения.
	 * 
	 * @return меню приложения.
	 */
	@SuppressWarnings("serial")
	public JMenuBar getMenuBar() {
		menu = new JMenuBar();

		refreshMenuFile();

		JMenu viewMenu = new JMenu("Просмотр");
		viewMenu.add(cut);
		viewMenu.add(copy);
		viewMenu.add(past);
		viewMenu.add(delete);
		viewMenu.add(new JSeparator());
		viewMenu.add(selectAll);
		viewMenu.add(new JSeparator());
		viewMenu.add(find);
		viewMenu.add(findForward);
		viewMenu.add(findBack);

		JMenu lafMenu = new JMenu("Вид"); // Меню LaF представляет собой радиокнопки
		ButtonGroup btgLaf = new ButtonGroup();
		for (UIManager.LookAndFeelInfo lafi : UIManager.getInstalledLookAndFeels()) {
			JMenuItem mi = new JRadioButtonMenuItem();
			mi.setAction(new AbstractAction() {
				{
					putValue(Action.NAME, lafi.getName());
				}

				public void actionPerformed(ActionEvent ae) {
					laf.setLookAndFeel(lafi.getClassName());
				}
			});
			mi.setSelected(laf.getLookAndFeel().equals(lafi.getClassName()));
			btgLaf.add(mi);
			lafMenu.add(mi);
		}

		JMenu helpMenu = new JMenu("Помощь");
		helpMenu.add(about);

		menu.add(fileMenu);
		menu.add(viewMenu);
		menu.add(lafMenu);
		menu.add(helpMenu);

		return menu;
	}

	/**
	 * Получить Toolbar приложения.
	 * 
	 * @return Toolbar приложения.
	 */
	@SuppressWarnings("serial")
	public JToolBar getToolBar() {

		JToolBar toolBar = new JToolBar() {
			// Кнопки toolbar-а не должны брать на себя фокус.
			// Иначе теряется выделение текста в JEditorPane.
			@Override
			protected JButton createActionComponent(Action a) {
				JButton b = super.createActionComponent(a);
				b.setRequestFocusEnabled(false);
				return b;
			}
		};

		toolBar.setRollover(true);
		toolBar.setFloatable(false);

		toolBar.add(open);
		toolBar.addSeparator();
		toolBar.add(cut);
		toolBar.add(copy);
		toolBar.add(past);
		toolBar.addSeparator();
		toolBar.add(find);
		toolBar.add(findForward);
		toolBar.add(findBack);

		return toolBar;
	}

	/**
	 * Создать/пересоздать меню "Файл".
	 * <p>
	 * После открытия файла Filer добавляет его в список имен последних открытых
	 * файлов (LastFiles) и обновляет меню File меню. Поэтому нужен отдельный метод
	 * для создания/обновления этого меню.
	 */
	public void refreshMenuFile() {
		// Если меню ещё не создано, то создаю его
		if (fileMenu == null) {
			fileMenu = new JMenu("Файл");
			menu.add(fileMenu);
		} else
			// А иначе, очищаю от всех пунктов
			fileMenu.removeAll();

		// Добавляю пункт меню "Открыть"
		fileMenu.add(open);

		// Получаю список последних открытых файлов
		List<String> list = this.lastFiles.getList();

		// И если таковые были
		if (list.size() > 0) {
			// добавляю в меню сепаратор
			fileMenu.add(new JSeparator());

			// и список открытых файлов
			int i = 1;
			for (String s : list)
				fileMenu.add(getActOpenFile(filer, i++, s));
		}

		fileMenu.add(new JSeparator());
		fileMenu.add(exit);
	}

	/**
	 * Активировать/деактивировать пункт меню Copy.
	 * <p>
	 * В отображенной выписке пункт меню Copy деактивирован. Но если пользователь
	 * выделит фрагмент текста, то следует активировать Copy, если пользователь
	 * сбросит выделение, то следует деактивировать Copy. Это реализуется слушателем
	 * на JEditorPane (см. конструктор BankViewer, фрагмент
	 * pane.addCaretListener...), который и вызывает этот метод.
	 * <p>
	 * 
	 * @param enabled true - активировать, flase деактивировать пункт меню Copy.
	 */
	public void setEnableCopy(boolean enabled) {
		copy.setEnabled(enabled);
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
	 * Action для имен последних открытых файлов в меню приложения.
	 * 
	 * @param no   номер файла 1..N.
	 * @param name путь и имя файла.
	 * @return Action для открытия указанного файла.
	 */
	@SuppressWarnings("serial")
	public AbstractAction getActOpenFile(Filer filer, int no, String name) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "" + no + " " + name);
				putValue(Action.SHORT_DESCRIPTION, name);
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.MNEMONIC_KEY, KeyEvent.VK_0 + no);
			}

			public void actionPerformed(ActionEvent actionEvent) {
				filer.open(new File(name));
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
	 * "Вырезать"
	 * 
	 * @return Action "Вырезать"
	 */
	@SuppressWarnings("serial")
	public AbstractAction getActCut() {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Вырезать");
				putValue(Action.SHORT_DESCRIPTION, "Вырезать фрагмент");
				putValue(Action.SMALL_ICON, getImageIcon("cut.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, 2));
				setEnabled(false);
			}

			public void actionPerformed(ActionEvent actionEvent) {
			}
		};
	}

	/**
	 * "Копировать"
	 * 
	 * @return Action "Копировать"
	 */
	@SuppressWarnings("serial")
	public AbstractAction getActCopy(JEditorPane pane) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Копировать");
				putValue(Action.SHORT_DESCRIPTION, "Копировать фрагмент");
				putValue(Action.SMALL_ICON, getImageIcon("copy.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, 2));
				setEnabled(false);
			}

			public void actionPerformed(ActionEvent actionEvent) {
				pane.copy();
			}
		};
	}

	/**
	 * "Вставить"
	 * 
	 * @return Action "Вставить"
	 */
	@SuppressWarnings("serial")
	public AbstractAction getActPaste() {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Вставить");
				putValue(Action.SHORT_DESCRIPTION, "Вставить фрагмент");
				putValue(Action.SMALL_ICON, getImageIcon("paste.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, 2));
				setEnabled(false);
			}

			public void actionPerformed(ActionEvent actionEvent) {
			}
		};
	}

	/**
	 * "Удалить"
	 * 
	 * @return Action "Удалить"
	 */
	@SuppressWarnings("serial")
	public AbstractAction getActDelete() {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Удалить");
				putValue(Action.SHORT_DESCRIPTION, "Удалить фрагмент");
				putValue(Action.SMALL_ICON, getImageIcon("delete.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
				setEnabled(false);
			}

			public void actionPerformed(ActionEvent actionEvent) {
			}
		};
	}

	/**
	 * "Выделить всё"
	 * 
	 * @return Action "Выделить всё"
	 */
	@SuppressWarnings("serial")
	public AbstractAction getActSelectAll(JEditorPane pane) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Выделить всё");
				putValue(Action.SHORT_DESCRIPTION, "Выделить всё");
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, 2));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				pane.requestFocus();
				pane.selectAll();
			}
		};
	}

	/**
	 * "Поиск..."
	 * 
	 * @return Action "Поиск..."
	 */
	@SuppressWarnings("serial")
	public AbstractAction getActFind(Finder finder) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Поиск...");
				putValue(Action.SHORT_DESCRIPTION, "Поиск записи по заданному критерию");
				putValue(Action.SMALL_ICON, getImageIcon("find.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				finder.find();
			}
		};
	}

	/**
	 * "Продолжить поиск вперёд"
	 * 
	 * @return Action "Продолжить поиск вперёд"
	 */
	@SuppressWarnings("serial")
	public AbstractAction getActFindForward(Finder finder) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Продолжить поиск вперёд");
				putValue(Action.SHORT_DESCRIPTION, "Продолжить поиск вперёд");
				putValue(Action.SMALL_ICON, getImageIcon("findforward.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				finder.findForward();
			}
		};
	}

	/**
	 * "Продолжить поиск назад"
	 * 
	 * @return Action "Продолжить поиск назад"
	 */
	@SuppressWarnings("serial")
	public AbstractAction getActFindBack(Finder finder) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, "Продолжить поиск назад");
				putValue(Action.SHORT_DESCRIPTION, "Продолжить поиск назад");
				putValue(Action.SMALL_ICON, getImageIcon("findback.gif"));
				putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				finder.findBack();
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
				String str = "\n" + BankViewer.APP_NAME + "\n" + BankViewer.APP_VERSION + "\n"
						+ BankViewer.APP_COPYRIGHT + "\n\n" + BankViewer.APP_OTHER + "\n\n";
				viewer.inf(str, "О программе...");
			}
		};
	}

	private JMenuBar menu;
	private JMenu fileMenu;

	private AbstractAction open;
	private AbstractAction exit;

	private AbstractAction cut;
	private AbstractAction copy;
	private AbstractAction past;
	private AbstractAction delete;
	private AbstractAction selectAll;

	private AbstractAction find;
	private AbstractAction findForward;
	private AbstractAction findBack;

	private AbstractAction about;

	private Filer filer;
	private LastFiles lastFiles;
	private LaF laf;
}
