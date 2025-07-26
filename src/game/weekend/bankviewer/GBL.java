package game.weekend.bankviewer;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Упрощение менеджера расположения GridBagLayout.
 * <P>
 * Использование GridBagLayout требует написания большого количества строк кода
 * и большого внимания. GBL оставляет только необходимую функциональность и
 * требует ввода небольшого количества кода.
 * <P>
 * Панель делится на некоторое количество строк и колонок. Затем слева направо
 * последовательно указываются элементы управления, для которых задается способ
 * выравнивания (посредством выбора подходящего метода) и количество занимаемых
 * в строке клеток (посредством указания второго параметра метода). Высота
 * элементов управления всегда подразумевается равной 1. После заполнения
 * текущей строки вызывается метод newLine() и заполняется следующая строка.
 */
public class GBL {

	/**
	 * Создать менеджер расположения GBL.
	 * 
	 * @param p      панель, на которой будут располагаться элементы управления.
	 * @param border true - если элементы следует располагать с некоторым зазором
	 *               между собой, false - если без зазора.
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
	 * Добавить в текущую строку и позицию компонент с выравниванием влево.
	 * 
	 * @param c     компонент.
	 * @param width ширина компонента.
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
	 * Добавить в текущую строку и позицию компонент с выравниванием вправо.
	 * 
	 * @param c     компонент.
	 * @param width ширина компонента.
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
	 * Добавить в текущую строку и позицию компонент с растягиванием его по
	 * горизонтали.
	 * 
	 * @param c     компонент.
	 * @param width ширина компонента.
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
	 * Перейти к следующей строке.
	 */
	public void newLine() {
		gbc.gridx = 0;
		++gbc.gridy;
	}

	private GridBagConstraints gbc;
	private GridBagLayout gbl;
	private JPanel pane;
}
