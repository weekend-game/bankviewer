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
 * Frame for specifying the substring and search attributes.
 */
@SuppressWarnings("serial")
public abstract class FinderFrame extends JDialog {

	/**
	 * Create a frame to specify the substring and search attributes.
	 * 
	 * @param owner the frame in which the JEditorFrame is located.
	 */
	public FinderFrame(JFrame owner) {
		super(owner, Loc.get("find"), false);

		// Default size and location
		Proper.setBounds(this, 360, 180, 460, 135);

		// Intercepting Esc key press to close frame
		InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(KeyStroke.getKeyStroke("pressed ESCAPE"), "Exit");
		ActionMap aMap = getRootPane().getActionMap();
		aMap.put("Exit", new AbstractAction() {
			public void actionPerformed(ActionEvent actionEvent) {
				close();
			}
		});

		// Intercepting a frame closing to save its size and position
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				Proper.saveBounds(FinderFrame.this);
			}
		});

		// Displaying frame components
		createComponents();
	}

	/**
	 * Show frame components.
	 */
	private void createComponents() {
		JLabel lblWhat = new JLabel(Loc.get("what") + ":");
		fldWhat = new JTextField(50);

		chkCase = new JCheckBox(Loc.get("case_sensitive"));

		opbUp = new JRadioButton(Loc.get("back"));
		opbDown = new JRadioButton(Loc.get("forward"));
		ButtonGroup bngDir = new ButtonGroup();
		bngDir.add(opbUp);
		bngDir.add(opbDown);
		JPanel panDir = new JPanel();
		panDir.setBorder(BorderFactory.createEtchedBorder());
		panDir.setLayout(new FlowLayout());
		panDir.add(opbUp);
		panDir.add(opbDown);

		JButton btnFind = new JButton(Loc.get("find_next"));
		getRootPane().setDefaultButton(btnFind);
		Dimension psFind = btnFind.getPreferredSize();
		psFind.width = 110;
		btnFind.setMinimumSize(psFind);
		btnFind.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				find();
			}
		});

		JButton btnCancel = new JButton(Loc.get("cancel"));
		Dimension psCancel = btnCancel.getPreferredSize();
		psCancel.width = 110;
		btnCancel.setMinimumSize(psCancel);
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
	 * Focus in the What field.
	 */
	public void whatFocus() {
		fldWhat.requestFocus();
	}

	/**
	 * The actual search. Will be defined in Action.
	 */
	public abstract void find();

	/**
	 * Close frame.
	 */
	public void close() {
		Proper.saveBounds(FinderFrame.this);
		dispose();
	}

	/**
	 * Get a string to search for in text.
	 * 
	 * @return string to search for.
	 */
	public String getPattern() {
		return fldWhat.getText();
	}

	/**
	 * Set the string to search for.
	 * 
	 * @param pattern string to search for.
	 */
	public void setPattern(String pattern) {
		fldWhat.setText(pattern);
	}

	/**
	 * Get the case-insensitive search flag.
	 * 
	 * @return case-insensitive search flag.
	 */
	public boolean getCase() {
		return chkCase.isSelected();
	}

	/**
	 * Set the search flag to be case-insensitive.
	 * 
	 * @param caseSensitive case-insensitive search flag.
	 */
	public void setCase(boolean caseSensitive) {
		chkCase.setSelected(caseSensitive);
	}

	/**
	 * Get the search down sign.
	 * 
	 * @return search down sign.
	 */
	public boolean getFindDown() {
		return opbDown.isSelected();
	}

	/**
	 * Set the search flag down.
	 * 
	 * @param findDown search down sign.
	 */
	public void setFindDown(boolean findDown) {
		opbDown.setSelected(findDown);
	}

	protected JTextField fldWhat;
	private JCheckBox chkCase;
	private JRadioButton opbUp;
	private JRadioButton opbDown;
}
