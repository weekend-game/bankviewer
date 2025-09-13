package game.weekend.bankviewer;

import java.awt.Component;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Working with Look and feels.
 */
public class LaF {

	public static String DEFAULT_LAF = "javax.swing.plaf.metal.MetalLookAndFeel";

	/**
	 * Create an object for working with Look and feels.
	 */
	public LaF() {
	}

	/**
	 * Set the specified L&amp;F.
	 * 
	 * @param className L&amp;F.
	 */
	public void setLookAndFeel(String className) {
		try {
			UIManager.setLookAndFeel(className);

			for (Component c : components)
				SwingUtilities.updateComponentTreeUI(c);

			Proper.setProperty("LaF", className);
		} catch (Exception e) {
			setLookAndFeel(DEFAULT_LAF);
		}
	}

	/**
	 * Get current L&amp;F.
	 * 
	 * @return current L&amp;F.
	 */
	public String getLookAndFeel() {
		return Proper.getProperty("LaF", DEFAULT_LAF);
	}

	/**
	 * Provide a list of components for L&amp;F renewal.
	 * 
	 * @param components list of components.
	 */
	public void setupComponents(Component... components) {
		this.components = components;
	}

	private Component[] components;
}
