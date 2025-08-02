package game.weekend.bankviewer;

import java.awt.Component;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Работа с LookAndFeel-ами.
 */
public class LaF {

	public static String DEFAULT_LAF = "javax.swing.plaf.metal.MetalLookAndFeel";

	/**
	 * Создать объект для работы с LookAndFeel-ами.
	 */
	public LaF() {
	}

	/**
	 * Установить указанный L&amp;F.
	 * 
	 * @param className L&amp;F.
	 */
	public void setLookAndFeel(String className) {
		try {
			UIManager.setLookAndFeel(className);
			// increaseFont();

			for (Component c : components)
				SwingUtilities.updateComponentTreeUI(c);

			Proper.setProperty("LaF", className);
		} catch (Exception e) {
			setLookAndFeel(DEFAULT_LAF);
		}
	}

	/**
	 * Получить текущий L&amp;F.
	 * 
	 * @return текущий L&amp;F.
	 */
	public String getLookAndFeel() {
		return Proper.getProperty("LaF", DEFAULT_LAF);
	}

	/**
	 * Указать перечень компонентов для обнавления L&F.
	 * 
	 * @param components перечень компонентов
	 */
	public void setupComponents(Component... components) {
		this.components = components;
	}

	/**
	 * Увеличить шрифт. Просто попробовал.
	 */
//	private void increaseFont() {
//		float scale = (float) (Toolkit.getDefaultToolkit().getScreenResolution() / 96.0);
//
//		scale = (float) (scale > 1.5F ? 1.1 : scale);
//
//		Enumeration<?> keys = UIManager.getDefaults().keys();
//		while (keys.hasMoreElements()) {
//			Object key = keys.nextElement();
//			Object value = UIManager.get(key);
//			if (value instanceof FontUIResource) {
//				Font font = (Font) value;
//				font = font.deriveFont((float) font.getSize() * scale);
//				UIManager.put(key, font);
//			}
//		}
//	}

	private Component[] components;
}
