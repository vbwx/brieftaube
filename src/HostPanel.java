import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

final class HostPanel extends JFrame implements ActionListener, WindowListener {

	private static final long serialVersionUID = 1L;
	
	private JLabel num;
	private static int quant = 0;
	private JButton stop, config, info;
	private Connection a;
	private JPanel p1, p2;
	
	public HostPanel (Connection a) {
		super(Brieftaube.macosx ? "Host-Paneel" : "Brieftaube: Host");
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		this.a = a;
		setLayout(new VerticalFlowLayout());
		p1 = new JPanel(new FlowLayout());
		p1.add(new JLabel("Anzahl der Verbindungen:"));
		num = new JLabel(new Integer(quant).toString());
		p1.add(num);
		add(p1);
		p2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		info = new JButton("Info");
		info.setToolTipText("Zeigt Informationen über das Programm an.");
		info.addActionListener(this);
		p2.add(info);
		stop = new JButton("Stopp");
		stop.setToolTipText("Beenden des Hosts.");
		stop.addActionListener(this);
		p2.add(stop);
		config = new JButton("Konfig.");
		config.setToolTipText("Zeigt die aktuelle Konfiguration an bzw. ändert diese.");
		config.addActionListener(this);
		p2.add(config);
		add(p2);
		setFocusableWindowState(false);
		setLocation(0, 0); pack(); setVisible(true);
	}

	public void actionPerformed (ActionEvent e) {
		if (e.getSource()==stop) {
			a.close(); a = null;
			stop.setEnabled(false);
		}
		else if (e.getSource()==info)
			new InfoDialog(this);
		else if (e.getSource()==config) {
			String s;
			s = "Geöffneter Port: " + Brieftaube.port + "\n"
				+ "Benutzername: " + Brieftaube.user + "\n"
				+ "Speicherort: " + Brieftaube.storage + "\n"
				+ "Optionen:\n" + (Brieftaube.local ? "   • Lokaler Status\n" : "")
				+ (Brieftaube.smiley ? "   • Smileys als Bilder\n" : "")
				+ (Brieftaube.compr ? "   • Kompression\n" : "")
				+ (Brieftaube.html ? "   • HTML in Nachrichten auswerten\n" : "")
				+ "\nWollen Sie die aktuelle Konfiguration verändern?";
			if (JOptionPane.showConfirmDialog(this, s, "", JOptionPane.YES_NO_OPTION,
				JOptionPane.PLAIN_MESSAGE)==JOptionPane.YES_OPTION) {
				dispose();
				if (a!=null) { a.close(); a = null; }
				new CfgWindow();
				try { finalize(); } catch (Throwable t) { }
			}
		}
	}
	
	void increase () {
		num.setText(new Integer(++quant).toString());
	}
	
	void decrease () {
		num.setText(new Integer(--quant).toString());
	}

	public void windowActivated (WindowEvent e) { }

	public void windowClosed (WindowEvent e) { }

	public void windowClosing (WindowEvent e) {
		if (JOptionPane.showConfirmDialog(this, "Wollen Sie Brieftaube wirklich beenden?\nDabei werden sämtliche "
			+ "Verbindungen geschlossen!", "", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)==JOptionPane.YES_OPTION) {
			dispose();
			a.close();
			System.exit(0);
		}
	}

	public void windowDeactivated (WindowEvent e) { }

	public void windowDeiconified (WindowEvent e) { }

	public void windowIconified (WindowEvent e) { }

	public void windowOpened (WindowEvent e) { }

}