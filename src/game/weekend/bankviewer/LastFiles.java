package game.weekend.bankviewer;

import java.util.LinkedList;
import java.util.List;

/**
 * List of names of recently opened files.
 * <p>
 * The names of the last opened files are stored between application sessions
 * using the application's stored properties object (Proper).
 * <p>
 * Usage: The object is created when the application is launched. At the same
 * time, it reads the previously saved list of files using the Proper method.
 * The Act class object uses the getList() method to form a list of the most
 * recently opened files in the File menu. If the next file is successfully
 * opened, Filer places the name of the open file in the list using the put()
 * method. If the file cannot be opened, Filer removes such a file from the list
 * using the remove() method. When the application is terminated, the BankViewer
 * class calls the save() method from its close() method, thereby saving the
 * list of files until the next application launch.
 */
public class LastFiles {

	/**
	 * Create an object to store the names of the last opened files.
	 * 
	 * @param maxSize maximum number of file names to store.
	 */
	public LastFiles(int maxSize) {
		// Maximum number of stored file names
		this.maxSize = maxSize;

		// The specified quantity of file names will be read into the list of saved
		// application properties. These are properties named File1, File2, ...
		for (int i = 1; i <= maxSize; ++i) {
			String s = Proper.getProperty("File" + i, "");
			if (s.length() > 0) {
				list.add(s);
			}
		}
	}

	/**
	 * Remember the file name in the list.
	 * 
	 * @param value path and name of the open file.
	 */
	public void put(String value) {
		// The file may already be in the list and needs to be deleted.
		int i = list.indexOf(value);
		if (i >= 0) {
			list.remove(i);
		}
		list.addFirst(value); // And place first in the list

		// If the list size is beyond maxSize, then I delete the last one (I forget)
		if (list.size() > maxSize) {
			list.remove(maxSize);
		}
	}

	/**
	 * Remove file name from list.
	 */
	public void remove(String value) {
		int pos = list.indexOf(value);
		list.remove(pos);
	}

	/**
	 * Get a list of recently opened files. The method is used to generate a list of
	 * these files in the application's File menu.
	 * 
	 * @return list of recently opened files.
	 */
	public List<String> getList() {
		return list;
	}

	/**
	 * Save the list of files in the remembered properties of the application. These
	 * will be properties named File1, File2, ... They will be read again when this
	 * object is created (when the application is launched).
	 */
	public void save() {
		int i = 1;
		for (String s : list) {
			Proper.setProperty("File" + i, s);
			++i;
		}
		while (i <= maxSize) {
			Proper.setProperty("File" + i, "");
			++i;
		}
	}

	private final int maxSize;
	private final LinkedList<String> list = new LinkedList<String>();
}
