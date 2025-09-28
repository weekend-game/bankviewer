package game.weekend.bankviewer;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * Working with files.
 */
public class Filer {

	/** File extension */
	public static final String EXTENSION = "txt";

	/** Name */
	public static final String DESCRIPTION = "*.txt - " + Loc.get("bank_statement_text_file");

	/**
	 * Create a file handling object.
	 * 
	 * @param viewer the main object of the application.
	 */
	public Filer(BankViewer viewer, LastFiles lastFiles, Finder finder) {
		this.viewer = viewer;
		this.lastFiles = lastFiles;
		this.finder = finder;
	}

	/**
	 * Open the specified file and display it.
	 * 
	 * @param file file to open
	 */
	public void open(File file) {
		if (file != null) {
			if (file.exists()) {
				this.file = file;

				// Displaying the name of the open file in the application title
				viewer.getFrame().setTitle(BankViewer.APP_NAME + " - " + file.getPath());

				// I remember it in the list of recently opened files
				lastFiles.put(file.getPath());

				try {
					// The original file will be processed and written into an html file like this
					String dst = "temp_bankviewer_" + no++;
					File tempFile = File.createTempFile(dst, ".html");

					Convertor.convert(file.getPath(), tempFile);

					// Displaying an html file
					viewer.showFile(tempFile);

					// Deleting the now unnecessary html file.
					tempFile.deleteOnExit();

					// If the search window is open, the search will start from the beginning of the
					// new file
					finder.resetPosition();

				} catch (IOException e) {
					Mes.err(Loc.get("failed_to_create_temporary_file_to_display_table") + ".\n" + e);
				}

			} else {
				// If the file does not open, I delete it from the list of recently opened files
				lastFiles.remove(file.getPath());

				// I am issuing a message about this unpleasant event.
				Mes.err(Loc.get("file") + " " + file.getPath() + " " + Loc.get("not_found") + ".");
			}

			viewer.refreshMenuFile();
		}
	}

	/**
	 * Get a file to open via the file open dialog box.
	 * 
	 * @return the file specified by the user, or null if the user declined to open
	 *         the file.
	 */
	public File showDialogue() {
		JFileChooser chooser = getChooser(this.file);
		int result = chooser.showOpenDialog(viewer.getFrame());

		File selectedFile = null;
		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = chooser.getSelectedFile();
		}
		return selectedFile;
	}

	/**
	 * Get a standard dialog box for opening a program file, customized according to
	 * the needs of the program.
	 * 
	 * @param currentFile the current file being edited.
	 * 
	 * @return customized dialog box.
	 */
	private JFileChooser getChooser(File currentFile) {
		JFileChooser chooser = new JFileChooser();
		String curDir = (currentFile == null) ? "." : currentFile.getPath();
		chooser.setCurrentDirectory(new File(curDir));

		chooser.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File file) {
				if (file.getName().toLowerCase().endsWith("." + EXTENSION)) {
					return true;
				}
				if (file.isDirectory()) {
					return true;
				}
				return false;
			}

			@Override
			public String getDescription() {
				return DESCRIPTION;
			}
		});

		return chooser;
	}

	private File file;
	private BankViewer viewer;
	private LastFiles lastFiles;
	private Finder finder;

	private static int no = 1;
}
