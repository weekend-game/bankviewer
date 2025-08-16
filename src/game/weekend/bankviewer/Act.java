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
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * Menu bar, context menu and toolbar. And also all Actions that are in the
 * application.
 */
public class Act {

	/**
	 * Create a menu bar, context menu and toolbar object.
	 */
	public Act(BankViewer viewer, Filer filer, Finder finder, LaF laf, LastFiles lastFiles) {
		this.filer = filer;
		this.laf = laf;
		this.lastFiles = lastFiles;

		// Actions can be used both in the menu and in the toolbar, so it is better to
		// create and remember them once in the designer.
		open = getActOpen(filer);
		exit = getActExit(viewer);

		cut = getActCut();
		copy = getActCopy(viewer.getPane());
		paste = getActPaste();
		selectAll = getActSelectAll(viewer.getPane());
		find = getActFind(finder);
		findForward = getActFindForward(finder);
		findBack = getActFindBack(finder);

		toolbarOn = getActToolbarOn(viewer);
		statusbarOn = getActStatusbarOn(viewer);

		about = getActAbout(viewer);
	}

	/**
	 * Get the application menu.
	 * 
	 * @return the application menu.
	 */
	@SuppressWarnings("serial")
	public JMenuBar getMenuBar() {
		menu = new JMenuBar();

		refreshMenuFile();

		JMenu editMenu = new JMenu(Loc.get("edit"));
		editMenu.add(cut);
		editMenu.add(copy);
		editMenu.add(paste);
		editMenu.add(new JSeparator());
		editMenu.add(selectAll);
		editMenu.add(new JSeparator());
		editMenu.add(find);
		editMenu.add(findForward);
		editMenu.add(findBack);

		JMenu viewMenu = new JMenu(Loc.get("view"));
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
			viewMenu.add(mi);
		}

		viewMenu.add(new JSeparator());

		JCheckBoxMenuItem i = null;

		i = new JCheckBoxMenuItem(toolbarOn);
		i.setSelected(Proper.getProperty("ToolbarON", "TRUE").equalsIgnoreCase("TRUE") ? true : false);
		viewMenu.add(i);

		i = new JCheckBoxMenuItem(statusbarOn);
		i.setSelected(Proper.getProperty("StatusbarON", "TRUE").equalsIgnoreCase("TRUE") ? true : false);
		viewMenu.add(i);

		JMenu helpMenu = new JMenu(Loc.get("help"));
		helpMenu.add(about);

		menu.add(fileMenu);
		menu.add(editMenu);
		menu.add(viewMenu);
		menu.add(helpMenu);

		return menu;
	}

	/**
	 * Get the application toolbar.
	 * 
	 * @return the application toolbar.
	 */
	@SuppressWarnings("serial")
	public JToolBar getToolBar() {

		JToolBar toolBar = new JToolBar() {
			// Toolbar buttons must not receive focus, otherwise the text selection in the
			// JEditorPane will be lost.
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
		toolBar.add(paste);
		toolBar.addSeparator();
		toolBar.add(find);
		toolBar.add(findForward);
		toolBar.add(findBack);

		return toolBar;
	}

	public JPopupMenu getPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();

			popupMenu.add(open);
			popupMenu.add(new JSeparator());
			popupMenu.add(cut);
			popupMenu.add(copy);
			popupMenu.add(paste);
			popupMenu.add(new JSeparator());
			popupMenu.add(selectAll);
			popupMenu.add(new JSeparator());
			popupMenu.add(find);
			popupMenu.add(findForward);
			popupMenu.add(findBack);
		}

		return popupMenu;

	}

	/**
	 * Create/recreate the File menu.
	 * <p>
	 * After opening a file, Filer adds it to the list of names of the last opened
	 * files (LastFiles) and updates the File menu. Therefore, a separate method is
	 * needed to create/update this menu.
	 */
	public void refreshMenuFile() {
		// If the menu has not yet been created, then I will create it
		if (fileMenu == null) {
			fileMenu = new JMenu(Loc.get("file"));
			menu.add(fileMenu);
		} else
			// Otherwise, I clear all points
			fileMenu.removeAll();

		// Adding the menu item "Open"
		fileMenu.add(open);

		// I get a list of recently opened files
		List<String> list = this.lastFiles.getList();

		// And if there were any
		if (list.size() > 0) {
			// I add a separator to the menu
			fileMenu.add(new JSeparator());

			// and a list of open files
			int i = 1;
			for (String s : list)
				fileMenu.add(getActOpenFile(filer, i++, s));
		}

		fileMenu.add(new JSeparator());
		fileMenu.add(exit);
	}

	/**
	 * Activate/deactivate the Copy menu item.
	 * <p>
	 * In the displayed bank statement, the Copy menu item is disabled. But if the
	 * user selects a text fragment, Copy should be activated; if the user clears
	 * the selection, Copy should be disabled. This is implemented by a listener on
	 * the JEditorPane (see the BankViewer constructor, pane.addCaretListener...),
	 * which calls this method.
	 * <p>
	 * 
	 * @param enabled true - activate, flase - deactivate the Copy menu item.
	 */
	public void setEnabledCopy(boolean enabled) {
		copy.setEnabled(enabled);
	}

	private ImageIcon getImageIcon(String fileName) {
		return new ImageIcon(getClass().getResource(BankViewer.IMAGE_PATH + fileName));
	}

	/**
	 * "Open..."
	 * 
	 * @param filer program file management.
	 * 
	 * @return Action "Open..."
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActOpen(Filer filer) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("open") + "...");
				putValue(Action.SHORT_DESCRIPTION, Loc.get("open_file"));
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
	 * Action for names of recently opened files in the application menu.
	 * 
	 * @param no   file number 1..N.
	 * @param name path and file name.
	 * @return Action to open the specified file.
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActOpenFile(Filer filer, int no, String name) {
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
	 * "Exiting the program"
	 * 
	 * @param viewer application.
	 *
	 * @return Action "Exiting the program"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActExit(BankViewer viewer) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("exit"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("exiting_the_program"));
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				viewer.close();
			}
		};
	}

	/**
	 * "Cut"
	 * 
	 * @return Action "Cut"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActCut() {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("cut"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("cut_fragment"));
				putValue(Action.SMALL_ICON, getImageIcon("cut.gif"));
				putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
				setEnabled(false);
			}

			public void actionPerformed(ActionEvent actionEvent) {
			}
		};
	}

	/**
	 * "Copy"
	 * 
	 * @return Action "Copy"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActCopy(JEditorPane pane) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("copy"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("copy_fragment"));
				putValue(Action.SMALL_ICON, getImageIcon("copy.gif"));
				putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
				setEnabled(false);
			}

			public void actionPerformed(ActionEvent actionEvent) {
				pane.copy();
			}
		};
	}

	/**
	 * "Paste"
	 * 
	 * @return Action "Paste"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActPaste() {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("paste"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("paste_fragment"));
				putValue(Action.SMALL_ICON, getImageIcon("paste.gif"));
				putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
				setEnabled(false);
			}

			public void actionPerformed(ActionEvent actionEvent) {
			}
		};
	}

	/**
	 * "Select all"
	 * 
	 * @return Action "Select all"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActSelectAll(JEditorPane pane) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("select_all"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("select_all"));
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
				putValue(Action.ACCELERATOR_KEY,
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				pane.requestFocus();
				pane.selectAll();
			}
		};
	}

	/**
	 * "Find..."
	 * 
	 * @return Action "Find..."
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActFind(Finder finder) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("find..."));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("find..."));
				putValue(Action.SMALL_ICON, getImageIcon("find.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				finder.find();
			}
		};
	}

	/**
	 * "Continue finding forward"
	 * 
	 * @return Action "Continue finding forward"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActFindForward(Finder finder) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("continue_finding_forward"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("continue_finding_forward"));
				putValue(Action.SMALL_ICON, getImageIcon("findforward.gif"));
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				finder.findForward();
			}
		};
	}

	/**
	 * "Continue finding backward"
	 * 
	 * @return Action "Continue finding backward"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActFindBack(Finder finder) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("continue_finding_backward"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("continue_finding_backward"));
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
	 * "Show toolbar"
	 * 
	 * @param viewer application.
	 *
	 * @return Action "Show toolbar"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActToolbarOn(BankViewer viewer) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("show_toolbar"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("show_toolbar"));
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				JCheckBoxMenuItem i = (JCheckBoxMenuItem) actionEvent.getSource();
				viewer.setTooolbarON(i.isSelected());
			}
		};
	}

	/**
	 * "Show status bar"
	 * 
	 * @param viewer application.
	 *
	 * @return Action "Show status bar"
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActStatusbarOn(BankViewer viewer) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("show_status_bar"));
				putValue(Action.SHORT_DESCRIPTION, Loc.get("show_status_bar"));
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				JCheckBoxMenuItem i = (JCheckBoxMenuItem) actionEvent.getSource();
				viewer.setStatusbarON(i.isSelected());
			}
		};
	}

	/**
	 * "About..."
	 * 
	 * @param viewer приложение.
	 * 
	 * @return Action "About..."
	 */
	@SuppressWarnings("serial")
	private AbstractAction getActAbout(BankViewer viewer) {
		return new AbstractAction() {
			{
				putValue(Action.NAME, Loc.get("about") + "...");
				putValue(Action.SHORT_DESCRIPTION, Loc.get("about") + "...");
				putValue(Action.SMALL_ICON, getImageIcon("empty.gif"));
			}

			public void actionPerformed(ActionEvent actionEvent) {
				String str = "\n" + BankViewer.APP_NAME + "\n" + BankViewer.APP_VERSION + "\n"
						+ BankViewer.APP_COPYRIGHT + "\n\n" + BankViewer.APP_OTHER + "\n\n";
				viewer.inf(str, Loc.get("about"));
			}
		};
	}

	private JMenuBar menu;
	private JMenu fileMenu;

	private JPopupMenu popupMenu;

	private AbstractAction open;
	private AbstractAction exit;

	private AbstractAction cut;
	private AbstractAction copy;
	private AbstractAction paste;
	private AbstractAction selectAll;

	private AbstractAction find;
	private AbstractAction findForward;
	private AbstractAction findBack;

	private AbstractAction toolbarOn;
	private AbstractAction statusbarOn;

	private AbstractAction about;

	private Filer filer;
	private LastFiles lastFiles;
	private LaF laf;
}
