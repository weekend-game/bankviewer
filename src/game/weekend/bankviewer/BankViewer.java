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
 * BankViewer application.
 */
public class BankViewer {

	/** Application name */
	public static final String APP_NAME = "BankViewer";

	/** Version */
	public static final String APP_VERSION = "01.50";

	/** Date */
	public static final String APP_DATE = "14.09.2025";

	/** Copyright */
	public static final String APP_COPYRIGHT = "(c) Weekend Game, 2025";

	/** Purpose */
	public static final String APP_OTHER = "view_bank_statements";

	/** Path to images */
	public static final String IMAGE_PATH = "/game/weekend/bankviewer/images/";

	/** Status bar */
	public static final StatusBar status = new StatusBar();

	/**
	 * Create an application. The application frame, frame controls, and objects
	 * required for operation are created.
	 */
	public BankViewer() {
		// Keeper of settings between application sessions
		Proper.read(APP_NAME);

		// Interface language
		Loc.setLanguage(Proper.getProperty("Language", "en"));

		// Applicatein Frame
		frame = new JFrame(APP_NAME);
		makeJFrame();

		// JEditorPane for displaying bank statement
		pane = new JEditorPane();
		makeJEditorPane();

		// Keeper of names of the last opened files (five, for example)
		lastFiles = new LastFiles(5);

		// Search in an open file
		Finder finder = new Finder(pane, frame);

		// Working with files
		filer = new Filer(this, lastFiles, finder);

		// Look and Feels
		LaF laf = new LaF();

		// Working with menu and toolbar
		act = new Act(this, filer, finder, laf, lastFiles);

		// Menu
		frame.setJMenuBar(act.getMenuBar());

		// Context menu
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

		// Toolbar
		toolbarOn = Proper.getProperty("ToolbarON", "TRUE").equalsIgnoreCase("TRUE") ? true : false;
		toolbar = act.getToolBar();
		if (toolbarOn)
			frame.getContentPane().add(toolbar, BorderLayout.NORTH);

		// Status bar
		statusbarOn = Proper.getProperty("StatusbarON", "TRUE").equalsIgnoreCase("TRUE") ? true : false;
		statusbar = BankViewer.status.getPanel();
		if (statusbarOn)
			frame.getContentPane().add(statusbar, BorderLayout.SOUTH);

		laf.setupComponents(frame, popupMenu, toolbar, statusbar);
		laf.setLookAndFeel(laf.getLookAndFeel());
	}

	/**
	 * Customizing the main application frame.
	 */
	private void makeJFrame() {
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close();
			}
		});

		Container cp = frame.getContentPane();
		cp.setLayout(new BorderLayout());

		Proper.setBounds(frame);
	}

	/**
	 * Customizing the statement display panel.
	 */
	private void makeJEditorPane() {
		// The panel is not editable
		pane.setEditable(false);

		JScrollPane spane = new JScrollPane();
		spane.getViewport().add(pane);

		frame.getContentPane().add(spane, BorderLayout.CENTER);

		// Intercepting text selection/reset
		pane.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent ce) {
				// If text is selected, allow Copy, otherwise block.
				act.setEnabledCopy(pane.getSelectionStart() != pane.getSelectionEnd());
			}
		});

		// Intercepting the Drag and Drop event. Actually Drop.
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
	 * Launch the application.
	 *
	 * @param args not used.
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
	 * Display the toolbar.
	 * 
	 * @param toolbarON true/false
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
	 * Display the status bar.
	 * 
	 * @param statusbarON true/false
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
	 * Close the application.
	 * 
	 * Saves everything that needs to be saved for restoration on next startup
	 */
	public void close() {
		Proper.saveBounds(frame);
		frame.dispose();
		lastFiles.save();
		Proper.save();
	}

	/**
	 * Display the file in the JEditorPane of the main frame.
	 * 
	 * @param file file to display
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
	 * Redisplay the File menu.
	 */
	public void refreshMenuFile() {
		act.refreshMenuFile();
	}

	/**
	 * Get the main application frame.
	 * 
	 * @return the main application frame.
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Get JEditorPane.
	 * 
	 * @return JEditorPane.
	 */
	public JEditorPane getPane() {
		return pane;
	}

	/**
	 * Issue an error message.
	 * 
	 * @param message text of message.
	 */
	public void err(String message) {
		JOptionPane.showMessageDialog(frame, message, APP_NAME, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Issue an informational message.
	 * 
	 * @param message текст сообщения.
	 */
	public void inf(String message) {
		JOptionPane.showMessageDialog(frame, message, APP_NAME, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Issue an informational message.
	 * 
	 * @param message message text.
	 * @param title   title of frame.
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
