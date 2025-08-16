package game.weekend.bankviewer;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Simplifying the GridBagLayout layout manager.
 * <P>
 * Using GridBagLayout requires a lot of code and attention. GBL leaves only the
 * necessary functionality and requires a small amount of code.
 * <P>
 * The panel is divided into a number of rows and columns. Then, from left to
 * right, the controls are sequentially specified, for which the alignment
 * method is specified (by selecting the appropriate method) and the number of
 * cells occupied in the row (by specifying the second parameter of the method).
 * The height of the controls is always assumed to be 1. After the current row
 * is filled, the newLine() method is called and the next row is filled.
 */
public class GBL {

	/**
	 * Create GBL Location Manager.
	 * 
	 * @param p      the panel on which the components will be located.
	 * @param border true - if the components should be placed with some gap between
	 *               them, false - if there is no gap.
	 */
	public GBL(JPanel p, boolean border) {
		pane = p;

		gbl = new GridBagLayout();
		pane.setLayout(gbl);

		gbc = new GridBagConstraints();

		if (border) {
			pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			gbc.insets = new Insets(3, 5, 3, 5);
		}

		gbc.gridx = 0;
		gbc.gridy = 0;
	}

	/**
	 * Add a left-aligned component to the current row and position.
	 * 
	 * @param c     component.
	 * @param width component width.
	 */
	public void addFixL(Component c, int width) {
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.gridwidth = width;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbl.setConstraints(c, gbc);
		pane.add(c);

		gbc.gridx += width;
	}

	/**
	 * Add a right-aligned component to the current row and position.
	 * 
	 * @param c     component.
	 * @param width component width.
	 */
	public void addFixR(Component c, int width) {
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.gridwidth = width;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		gbl.setConstraints(c, gbc);
		pane.add(c);

		gbc.gridx += width;
	}

	/**
	 * Add a component to the current row and position, stretching it horizontally.
	 * 
	 * @param c     component.
	 * @param width component width.
	 */
	public void addExtX(Component c, int width) {
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.gridwidth = width;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.CENTER;
		gbl.setConstraints(c, gbc);
		pane.add(c);

		gbc.gridx += width;
	}

	/**
	 * Go to the next line.
	 */
	public void newLine() {
		gbc.gridx = 0;
		++gbc.gridy;
	}

	private GridBagConstraints gbc;
	private GridBagLayout gbl;
	private JPanel pane;
}
