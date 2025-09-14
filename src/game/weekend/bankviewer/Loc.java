package game.weekend.bankviewer;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Loc {

	private Loc() {
	}

	public static void setLanguage(String language) {
		try {
			Loc.language = language;
			bundle = ResourceBundle.getBundle("messages", new Locale(language));
		} catch (MissingResourceException ignored) {
		}
	}

	public static String getLanguage() {
		return language;
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
	private static String language;
}
