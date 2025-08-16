package game.weekend.bankviewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Convert from bank statement format to HTML.
 */
public class Convertor {

	public static final String LS = System.lineSeparator();

	/**
	 * Creating objects of this class is prohibited. The class contains only static
	 * methods.
	 */
	private Convertor() {
	}

	/**
	 * Convert from bank statement format to HTML.
	 * 
	 * @param src name of the original file.
	 * @param dst HTML file name.
	 */
	public static void convert(String src, File dstFile) {
		ArrayList<Substance> al = loadSource(src);
		ArrayList<String> title = createTitle(al);

		lineCounter = 0;
		createHTML(dstFile, al, title);

		BankViewer.status.showText1("Строк: " + lineCounter);
		BankViewer.status.showText2("Колонок: " + (title.size() - 1)); // Column "No" (row number) is not counted
	}

	/**
	 * Load source text into ArrayList&lt;Substance&gt;.
	 * 
	 * The source text is a key/value pair. Such pairs between a line with the key
	 * "СекцияДокумент" and the key "КонецДокумента" form a document, which can be
	 * represented as a line of a table displayed on the screen. I will call such a
	 * sequence of pairs a document. Each document may contain fields that are
	 * absent in other documents and vice versa. But, in general, the fields in the
	 * documents are the same.
	 * 
	 * Substance is a class containing a logical field isDocument and a list of
	 * fields in the form of key/value pairs. If it is a document, then the list of
	 * fields contains many records, if not, then only one. The very line that was
	 * NOT between "СекцияДокумент" and "КонецДокумента".
	 * 
	 * The list is made up of objects of the Substance class.
	 * ArrayList&lt;Substance&gt;.
	 * 
	 * @param src name of the original extract file.
	 * 
	 * @return disassembled bank statement.
	 */
	private static ArrayList<Substance> loadSource(String src) {
		String s, s1, s2;
		int pos;
		boolean inDoc = false;
		Substance sub = null;
		ArrayList<Substance> vid = new ArrayList<Substance>();
		int nn = 0;

		try {
			// I open the file
			// BufferedReader inp = new BufferedReader(new FileReader(src));
			BufferedReader inp = new BufferedReader(
					new InputStreamReader(new FileInputStream(src), Charset.forName("UTF-8")));

			// and I read the lines in sequence
			while ((s = inp.readLine()) != null) {
				if (s.trim().length() == 0)
					continue;

				// Since each line has a key=value pair, I'm looking for the '=' symbol.
				pos = s.indexOf('=');
				if (pos > 0) {
					// If found, then s1 is the key and s2 is the value
					s1 = s.substring(0, pos);
					s2 = s.substring(pos + 1);
				} else {
					// Otherwise, s1 is the entire string and s2 is empty.
					s1 = s.trim();
					s2 = "";
				}

				if (s1.equalsIgnoreCase("СекцияДокумент")) {
					// I create a new document object
					sub = new Substance(Substance.A_DOC);
					// its number comes first
					sub.addPair("No", "" + ++nn);
					// Then this inscription СекцияДокумент
					sub.addPair(s1, s2);
					// Adding a new document to the resulting list
					vid.add(sub);

					// "I am in the document" flag
					inDoc = true;
				} else {
					if (inDoc) {
						// If "I am in a document" and I encounter "КонецДокумента",
						if (s1.equalsIgnoreCase("КонецДокумента")) {
							// then I reset the sign.
							inDoc = false;
						} else {
							// Otherwise, I add another key/value pair to the current document
							sub.addPair(s1, s2);
						}
					} else {
						// If not in the document, then I create an object with the "non-document"
						// attribute
						sub = new Substance(Substance.NOT_A_DOC);
						// I add a key/value pair to it
						sub.addPair(s1, s2);
						// and add it to the resulting list.
						vid.add(sub);
					}
				}
			}
			inp.close();
		} catch (IOException e) {
		}
		return vid;
	}

	/**
	 * Generate a table header that is displayed on the screen based on the
	 * retrieved documents. The table header is an ArrayList&lt;String&gt;
	 * containing the keys of all the fields found in the source file documents. But
	 * since the source file has already been parsed and put into a list, I work
	 * with that list.
	 * 
	 * @param vid disassembled bank statement.
	 * 
	 * @return heading based on bank statement documents.
	 */
	private static ArrayList<String> createTitle(ArrayList<Substance> vid) {
		// Return value. Title. List of all fields that appear in documents.
		ArrayList<String> title = new ArrayList<String>();

		// The document containing the largest number of fields becomes the basis for
		// forming the list of fields (title).

		// I'm looking for one
		int curr = 0;
		Substance etal = null;
		for (Substance sub : vid) {
			if (sub.isDoc && sub.pairs.size() > curr) {
				curr = sub.pairs.size();
				etal = sub;
			}
		}

		// I copy his keys into the title
		if (etal != null) {
			for (Pair p : etal.pairs) {
				title.add(p.key);
			}
		}

		// I expand the title by adding keys from other documents
		for (Substance sub : vid) {
			if (sub.isDoc) {
				for (Pair p : sub.pairs) {
					if (hasName(title, p.key) < 0) {
						title.add(p.key);
					}
				}
			}
		}
		return title;
	}

	/**
	 * Create a bank statement HTML file.
	 * 
	 * @param dst   name of the HTML file
	 * @param vid   disassembled bank statement.
	 * @param title title based on bank statement documents.
	 */
	private static void createHTML(File dstFile, ArrayList<Substance> vid, ArrayList<String> title) {

		try {
			out = new PrintWriter(new FileWriter(dstFile));

			write("<html>");
			write(" <head>");
			write("   <title>");
			write("   </title>");
			write(" </head>");
			write("<body>");

			boolean inTable = false;
			for (Substance sb : vid) {
				if (sb.isDoc) {
					// Print title
					if (!inTable) {
						inTable = true;
						write("<table border='1'>");
						write("<tr>");
						for (String t : title) {
							// Some empirical rules for determining the column width. This makes it more
							// convenient. However, it would be possible to determine the actually necessary
							// column width...
							if (t.equalsIgnoreCase("Плательщик1") || t.equalsIgnoreCase("Получатель")
									|| t.equalsIgnoreCase("Получатель1") || t.equalsIgnoreCase("НазначениеПлатежа")
									|| t.equalsIgnoreCase("НазначениеПлатежа1")) {
								write("<th>" + spacing(t) + "&nbsp;".repeat(100) + "</th>");
							} else if (t.equalsIgnoreCase("Плательщик") || t.equalsIgnoreCase("ПлательщикБанк1")
									|| t.equalsIgnoreCase("ПолучательБанк") || t.equalsIgnoreCase("ПолучательБанк1")) {
								write("<th>" + spacing(t) + "&nbsp;".repeat(50) + "</th>");
							} else {
								write("<th>" + spacing(t) + "</th>");
							}
						}
						write("</tr>");
					}

					// Forming a document line
					String[] d = new String[title.size()];
					for (Pair p : sb.pairs) {
						int ps = hasName(title, p.key);
						d[ps] = p.value;
					}

					// Output of a document line
					write("<tr>");
					for (int i = 0; i < d.length; ++i) {
						write("<td>" + spacing(d[i]) + "</td>");
					}
					write("</tr>");

					++lineCounter;

				} else {
					if (inTable) {
						write("</table>");
						inTable = false;
					}
					// Output of other things
					for (Pair p : sb.pairs) {
						String ss = "<b>" + spacing(p.key) + "</b> = " + spacing(p.value) + "<br>";
						write(ss);
					}
				}
			}
			if (inTable) {
				write("</table>");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		write("");
		write("</body>");
		write("</html>");

		out.close();
	}

	/**
	 * Replace the empty string with &nbsp;.
	 * 
	 * @param s string
	 * @return "&nbsp;" if the line was empty and the same if it wasn't.
	 */
	private static String spacing(String s) {
		if (s == null) {
			s = "&nbsp;";
		}
		if (s.trim().length() == 0) {
			s = "&nbsp;";
		}
		return s;
	}

	/**
	 * Output a line to the file this.out ending with a line feed.
	 * 
	 * @param s string for output.
	 */
	private static void write(String s) {
		Convertor.out.write(s + LS);
	}

	/**
	 * Determine the position of the key in the title.
	 * 
	 * Used when forming the document header. The header is formed from the keys of
	 * the document containing the largest number of fields. Then fields of other
	 * documents that may not have been in the document taken as a basis are added
	 * to it. This method is used to answer the question about the presence of such
	 * a field in the list.
	 * 
	 * The efficiency of such a search is low, but acceptable for this task.
	 * 
	 * @param title title based on bank statement documents.
	 * @param key   key.
	 * 
	 * @return - position of the key in the title or -1.
	 */
	private static int hasName(ArrayList<String> title, String key) {
		int i = -1;
		for (String str : title) {
			++i;
			if (str.equalsIgnoreCase(key))
				return i;
		}
		return -1;
	}

	/**
	 * Contains a logical field isDocument and a list of fields as key/value pairs.
	 * If it is a document, the list of fields contains many entries, if not, then
	 * only one. That same line that was NOT between "СекцияДокумент" and
	 * "КонецДокумента".
	 * 
	 * The class is very simple, so instead of getters I use public final fields.
	 */
	static class Substance {

		// Using constants instead of enum. What a wild thing...
		public static final boolean A_DOC = true;
		public static final boolean NOT_A_DOC = false;

		public Substance(boolean isDoc) {
			this.isDoc = isDoc;
		}

		public void addPair(String key, String value) {
			pairs.add(new Pair(key, value));
		}

		public final boolean isDoc;
		public final ArrayList<Pair> pairs = new ArrayList<Pair>();
	}

	static class Pair {
		public Pair(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public final String key;
		public final String value;
	}

	private static PrintWriter out;
	private static int lineCounter = 0;
}
