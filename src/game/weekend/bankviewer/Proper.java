package game.weekend.bankviewer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.JFrame;

/**
 * Locally stored application properties.
 */
public class Proper {

	/**
	 * Creating objects of this class is prohibited. The class contains only static
	 * methods.
	 */
	private Proper() {
	}

	/**
	 * Read previously saved application properties.
	 * 
	 * @param name name of the application properties file without specifying the
	 *             type.
	 */
	public static void read(String name) {
		fileName = name.toLowerCase() + ".properties";
		try {
			InputStream inp = new FileInputStream(fileName);
			properties.load(inp);
			inp.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * Save application properties.
	 */
	public static void save() {
		OutputStream out;
		try {
			out = new FileOutputStream(fileName);
			properties.store(out, "");
			out.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * Save the property with an integer value.
	 * 
	 * @param name  property name.
	 * @param value integer value.
	 */
	public static void setProperty(String name, int value) {
		properties.setProperty(name, "" + value);
	}

	/**
	 * Get the integer name property.
	 * 
	 * @param name property name.
	 * @param def  default property value.
	 * @return integer value of the property.
	 */
	public static int getProperty(String name, int def) {
		return Integer.parseInt(properties.getProperty(name, "" + def));
	}

	/**
	 * Save the property with the string value.
	 * 
	 * @param name  имя свойства.
	 * @param value строковое значение.
	 */
	public static void setProperty(String name, String value) {
		properties.setProperty(name, value);
	}

	/**
	 * Получить строковое свойство name.
	 * 
	 * @param name property name.
	 * @param def  default property value.
	 * @return string value of the property.
	 */
	public static String getProperty(String name, String def) {
		return properties.getProperty(name, def);
	}

	/**
	 * Save component location.
	 * 
	 * @param c component.
	 */
	public static void saveBounds(Component c) {
		String name = c.getClass().getName();
		Point gl = c.getLocation();
		setProperty(name + "_X", gl.x);
		setProperty(name + "_Y", gl.y);
		Dimension gs = c.getSize();
		setProperty(name + "_W", gs.width);
		setProperty(name + "_H", gs.height);
	}

	/**
	 * Place the component in a previously saved position. If the component has not
	 * been saved before, it will be located at the coordinates specified in the
	 * parameters.
	 * 
	 * @param c  component.
	 * @param dx х by default.
	 * @param dy y by default.
	 * @param dw default component width.
	 * @param dh default component height.
	 */
	public static void setBounds(Component c, int dx, int dy, int dw, int dh) {
		String name = c.getClass().getName();
		int x = getProperty(name + "_X", dx);
		int y = getProperty(name + "_Y", dy);
		c.setLocation(x, y);
		int w = getProperty(name + "_W", dw);
		int h = getProperty(name + "_H", dh);
		c.setSize(w, h);
	}

	/**
	 * Save the location of the main application frame.
	 * 
	 * @param frame frame.
	 */
	public static void saveBounds(JFrame frame) {
		saveBounds((Component) frame);
		setProperty(frame.getClass().getName() + "_STATE", frame.getExtendedState());
	}

	/**
	 * Place the main application window in a previously saved or default location.
	 * 
	 * @param frame frame.
	 */
	public static void setBounds(JFrame frame) {
		final int INSET = 40;

		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		int def_x = INSET;
		int def_y = INSET;
		int def_w = ss.width - INSET * 2;
		int def_h = ss.height - INSET * 3;

		String name = frame.getClass().getName();
		int x = getProperty(name + "_X", def_x);
		int y = getProperty(name + "_Y", def_y);
		int w = getProperty(name + "_W", def_w);
		int h = getProperty(name + "_H", def_h);
		int st = getProperty(name + "_STATE", JFrame.NORMAL);

		if (st == JFrame.MAXIMIZED_BOTH) {
			frame.setBounds(def_x, def_y, def_w, def_h);
			frame.setExtendedState(st);
		} else {
			if (x < 0 || x >= ss.width || y < 0 || y >= ss.height || w < INSET || h < INSET) {
				x = def_x;
				y = def_y;
				w = def_w;
				h = def_h;
			}
			frame.setBounds(x, y, w, h);
		}
	}

	private static Properties properties = new Properties();
	private static String fileName = "application.properties";
}
