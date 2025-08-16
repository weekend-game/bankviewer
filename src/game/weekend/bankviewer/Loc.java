package game.weekend.bankviewer;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Loc {
	static {
		try {
			bundle = ResourceBundle.getBundle("messages", new Locale("ru"));
		} catch (MissingResourceException ignored) {
		}
	}

	private Loc() {
	}

	public static String get(String name) {
		if (bundle != null)
			try {
				return bundle.getString(name);
			} catch (MissingResourceException e) {
			}

		return getDefString(name);
	}

	private static String getDefString(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1).replace('_', ' ');
	}

	private static ResourceBundle bundle;
}
