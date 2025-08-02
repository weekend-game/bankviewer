package game.weekend.bankviewer;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/**
 * Фрейм для указания подстроки и аттрибутов поиска.
 */
@SuppressWarnings("serial")
public abstract class FinderFrame extends JDialog {

	/**
	 * Создать фрейм для указания подстроки и аттрибутов поиска.
	 * 
	 * @param owner фрейм в котором расположена JEditorFrame.
	 */
	public FinderFrame(JFrame owner) {
		super(owner, "Найти", false);

		// Размер и расположение по умолчанию
		Proper.setBounds(this, 360, 180, 460, 135);

		// Перехват нажатия Esc для закрытия фрейма
		InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke("pressed ESCAPE"), "Exit");
		ActionMap aMap = getRootPane().getActionMap();
		aMap.put("Exit", new AbstractAction() {
			public void actionPerformed(ActionEvent actionEvent) {
				close();
			}
		});

		// Перезват закрытия фрейма для сохранения его размеров и расположния
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				Proper.saveBounds(FinderFrame.this);
			}
		});

		// Отображаю компоненты окна
		createComponents();
	}

	/**
	 * Отобразить компоненты окна.
	 */
	private void createComponents() {
		JLabel lblWhat = new JLabel("Что:");
		fldWhat = new JTextField(50);

		chkCase = new JCheckBox("С учётом регистра");

		opbUp = new JRadioButton("Назад");
		opbDown = new JRadioButton("Вперёд");
		ButtonGroup bngDir = new ButtonGroup();
		bngDir.add(opbUp);
		bngDir.add(opbDown);
		JPanel panDir = new JPanel();
		panDir.setBorder(BorderFactory.createEtchedBorder());
		panDir.setLayout(new FlowLayout());
		panDir.add(opbUp);
		panDir.add(opbDown);

		JButton btnFind = new JButton("Найти далее");
		getRootPane().setDefaultButton(btnFind);
		Dimension psFind = btnFind.getPreferredSize();
		psFind.width = 110;
		btnFind.setMinimumSize(psFind);
		btnFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				find();
			}
		});

		JButton btnCancel = new JButton("Отмена");
		Dimension psCancel = btnCancel.getPreferredSize();
		psCancel.width = 110;
		btnCancel.setMinimumSize(psCancel);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				close();
			}
		});

		GBL g = new GBL((JPanel) getContentPane(), true);

		g.addFixL(lblWhat, 1);
		g.addExtX(fldWhat, 5);
		g.addFixR(btnFind, 1);

		g.newLine();
		g.addFixL(chkCase, 4);
		g.addFixL(panDir, 2);
		g.addFixR(btnCancel, 1);
	}

	/**
	 * Фокус в поле What.
	 */
	public void whatFocus() {
		fldWhat.requestFocus();
	}

	/**
	 * Собственно поиск. Будет определен в Action.
	 */
	public abstract void find();

	/**
	 * Закрыть фрейм.
	 */
	public void close() {
		Proper.saveBounds(FinderFrame.this);
		dispose();
	}

	/**
	 * Получить подстроку для поиска в тексте.
	 * 
	 * @return подстрока для поиска.
	 */
	public String getPattern() {
		return fldWhat.getText();
	}

	/**
	 * Установить подстроку для поиска.
	 * 
	 * @param pattern подстрока для поиска.
	 */
	public void setPattern(String pattern) {
		fldWhat.setText(pattern);
	}

	/**
	 * Получить признак поиска без учёта регистра.
	 * 
	 * @return признак поиска без учёта регистра.
	 */
	public boolean getCase() {
		return chkCase.isSelected();
	}

	/**
	 * Установить признак поиска без учёта регистра.
	 * 
	 * @param caseSensitive признак поиска без учёта регистра.
	 */
	public void setCase(boolean caseSensitive) {
		chkCase.setSelected(caseSensitive);
	}

	/**
	 * Получить признак поиска вниз.
	 * 
	 * @return признак поиска вниз.
	 */
	public boolean getFindDown() {
		return opbDown.isSelected();
	}

	/**
	 * Установить признак поиска вниз.
	 * 
	 * @param findDown признак поиска вниз.
	 */
	public void setFindDown(boolean findDown) {
		opbDown.setSelected(findDown);
	}

	protected JTextField fldWhat;
	private JCheckBox chkCase;
	private JRadioButton opbUp;
	private JRadioButton opbDown;
}
