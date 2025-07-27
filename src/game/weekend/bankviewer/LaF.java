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
	 * 
	 * @param component компонент UI для обновления внешнего вида.
	 */
	public LaF(Component component) {
		this.component = component;
		setLookAndFeel(getLookAndFeel());
	}

	/**
	 * Установить указанный L&amp;F.
	 * 
	 * @param className L&amp;F.
	 */
	public void setLookAndFeel(String className) {
		try {
			UIManager.setLookAndFeel(className);
			SwingUtilities.updateComponentTreeUI(component);
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

	private Component component;
}
