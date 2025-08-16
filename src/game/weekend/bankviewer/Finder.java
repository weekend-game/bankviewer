package game.weekend.bankviewer;

import java.awt.Rectangle;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * Search for text displayed in a JEditorPane.
 */
public class Finder {

	/**
	 * Create an object to search for text displayed in a JEditorPane.
	 *
	 * @param pane  JEditorPane itself.
	 * @param frame frame containing the JEditorPane.
	 */
	public Finder(JEditorPane pane, JFrame frame) {
		this.pane = pane;
		this.appFrame = frame;

		this.pattern = Proper.getProperty("Pattern", "");

		this.caseSensitive = Proper.getProperty("CaseSensitive", "FALSE").equalsIgnoreCase("TRUE") ? true : false;
	}

	/**
	 * Reset the search start position to its original state.
	 */
	public void resetPosition() {
		position = -1;
	}

	/**
	 * Displays a dialog box for specifying a search pattern, and then searches for
	 * the specified pattern.
	 */
	@SuppressWarnings("serial")
	public void find() {
		if (finderFrame == null) {
			finderFrame = new FinderFrame(appFrame) {
				{
					resetPosition();
					setPattern(pattern);
					setCase(caseSensitive);
					setFindDown(true);
				}

				@Override
				public void find() {
					pattern = getPattern();
					Proper.setProperty("Pattern", pattern);

					caseSensitive = getCase();
					Proper.setProperty("CaseSensitive", caseSensitive ? "TRUE" : "FALSE");

					findDown = getFindDown();

					if (findDown) {
						findForward();
					} else {
						findBack();
					}

					whatFocus();
				}

				@Override
				public void close() {
					super.close();
					finderFrame = null;
				}
			};
		}
		finderFrame.setVisible(true);
	}

	/**
	 * Search current line forward.
	 */
	public void findForward() {
		try {
			String content = getContent();
			String pattern = getPattern();
			if (pattern.trim().length() > 0) {
				int i = content.indexOf(pattern, position + 1);
				showResult(i);
			}
		} catch (BadLocationException e) {
			System.out.println(e);
		}
	}

	/**
	 * Search backwards for the current line.
	 */
	public void findBack() {
		try {
			String content = getContent();
			String pattern = getPattern();
			if (pattern.trim().length() > 0) {
				int i = content.lastIndexOf(pattern, position - 1);
				showResult(i);
			}
		} catch (BadLocationException e) {
			System.out.println(e);
		}
	}

	/**
	 * Get the string to search for.
	 * 
	 * @return search string.
	 */
	private String getPattern() {
		if (!caseSensitive) {
			return pattern.toUpperCase();
		} else {
			return pattern;
		}
	}

	/**
	 * Get the text displayed in the JEditorPane.
	 * 
	 * @return text displayed in JEditorPane.
	 * @throws BadLocationException inherited exception.
	 */
	private String getContent() throws BadLocationException {
		Document d = pane.getDocument();
		String s = d.getText(0, d.getLength());
		if (!caseSensitive) {
			s = s.toUpperCase();
		}
		return s;
	}

	/**
	 * Select the found string in JEditorPane.
	 * 
	 * @param i the position at which the found string begins.
	 * @throws BadLocationException inherited exception.
	 */
	private void showResult(int i) throws BadLocationException {
		if (i >= 0) {
			position = i;
			BankViewer.status.showMessage("");
		} else
			BankViewer.status.showMessage(Loc.get("unable_to_find") + " \"" + pattern + "\"");

		if (position >= 0) {
			pane.scrollRectToVisible((Rectangle) pane.modelToView2D(position));
			pane.select(position, position + pattern.length());
			pane.requestFocus();
		}
	}

	private JFrame appFrame;
	private JEditorPane pane;

	private FinderFrame finderFrame;

	private String pattern = "";
	private boolean caseSensitive = false;
	private boolean findDown = true;

	private int position = -1;
}
