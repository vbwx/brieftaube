import java.awt.Font;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

final class InfoDialog extends JDialog {
	
	private static final long serialVersionUID = 0L;

	public InfoDialog (JFrame owner) {
		super(owner, "", true);
		setResizable(false);
		setLayout(new VerticalFlowLayout());
		JLabel l = new JLabel("Brieftaube");
		add(new JLabel(new ImageIcon(getClass().getResource("/res/brieftaube.png"))));
		l.setFont(l.getFont().deriveFont(Font.BOLD));
		add(l);
		add(Box.createGlue()); add(Box.createGlue()); add(Box.createGlue());
		add(new JLabel("Version 0.9.6 beta"));
		add(new JLabel("   Copyright © 2006 Bernhard Waldbrunner   "));
		add(Box.createGlue());
		pack();
		setLocation((Brieftaube.scr_w-getWidth())/2, (Brieftaube.scr_h-getHeight())/2);
		setVisible(true);
	}
	
}