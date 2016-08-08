import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

final class CfgWindow extends JFrame implements ActionListener, ItemListener {

	private static final long serialVersionUID = 0L;
	private JPanel p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11;
	private JCheckBox chk_host, chk_compr, chk_local, chk_smiley, chk_html;
	private JTextField user, port, addr, storage;
	private JButton btn_storage, btn_info, btn_save, btn_ok, btn_quit;
	private JFileChooser fc;
	private String s_addr = "";

	public CfgWindow () {
		super(Brieftaube.macosx ? "Konfiguration" : "Brieftaube: Konfiguration");
		setResizable(false);
		setLayout(new GridLayout(11, 1));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fc = new JFileChooser(); fc.setDialogType(JFileChooser.OPEN_DIALOG);
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setDialogTitle("Speicherort für übertragene Dateien");
		fc.setApproveButtonText("Auswählen");
		p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p1.add(new JLabel("Benutzername"));
		user = new JTextField(Brieftaube.user); user.setColumns(10);
		p1.add(user);
		p1.setToolTipText("Der Benutzername dient zur Identifizierung bei anderen Kommunikationsteilnehmern.");
		add(p1);
		p6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		chk_host = new JCheckBox("Dieser Computer ist der Host", Brieftaube.host);
		chk_host.setToolTipText("Andere Computer werden sich zu diesem Programm verbinden, nicht umgekehrt.");
		chk_host.addItemListener(this);
		p6.add(chk_host); add(p6);
		p3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p3.add(new JLabel("Adresse"));
		addr = new JTextField(Brieftaube.addr); addr.setColumns(13);
		p3.setToolTipText("Die Adresse gibt an, wer der Gesprächspartner ist.");
		p3.add(addr);
		add(p3);
		p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p2.add(new JLabel("TCP-Port"));
		port = new JTextField(Brieftaube.port); port.setColumns(4);
		port.setToolTipText("Durch einen Port wird das Programm von außen erreicht.");
		p2.add(port);
		p2.setToolTipText("Wenn dieser Computer der Host ist, muss die Firewall Anfragen an diesen Port durchlassen.");
		add(p2);
		p4 = new JPanel(new FlowLayout(FlowLayout.LEFT, FlowLayout.BOTTOM));
		p4.add(new JLabel("Speicherort für übertragene Dateien:"));
		add(p4);
		p5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		storage = new JTextField(Brieftaube.storage);
		storage.setColumns(22);
		storage.setBorder(new EmptyBorder(2, 2, 2, 2));
		p5.add(storage);
		btn_storage = new JButton("...");
		btn_storage.setToolTipText("Hier wählen Sie einen anderen Pfad für erhaltene Dateien aus.");
		btn_storage.addActionListener(this);
		p5.add(btn_storage);
		add(p5);
		p7 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		chk_compr = new JCheckBox("Kompression", Brieftaube.compr);
		chk_compr.setToolTipText("Die Kompression (GZIP) ist beim Senden von Dateien sehr sinnvoll.");
		p7.add(chk_compr); add(p7);
		p8 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		chk_local = new JCheckBox("Lokalen Status anzeigen", Brieftaube.local);
		chk_local.setToolTipText("Dieser Status zeigt erweiterte Informationen über die Kommunikation an.");
		p8.add(chk_local); add(p8);
		p9 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		chk_smiley = new JCheckBox("Smileys als Bilder darstellen", Brieftaube.smiley);
		chk_smiley.setToolTipText("Zeigt in einer Nachricht z.B. anstelle von :-) das Emoticon als Bild an.");
		p9.add(chk_smiley); add(p9);
		p11 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		chk_html = new JCheckBox("HTML in Nachrichten auswerten", Brieftaube.html);
		chk_html.setToolTipText("Verwendet anstelle bestimmter HTML-Tags die entsprechende Formatierung.");
		p11.add(chk_html); add(p11);
		p10 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		btn_info = new JButton("Info");
		btn_info.setToolTipText("Zeigt Informationen über das Programm an.");
		btn_info.addActionListener(this);
		p10.add(btn_info);
		btn_save = new JButton("Speichern");
		btn_save.setToolTipText("Ist die Konfiguration gesichert, wird dieses Fenster nicht mehr angezeigt.");
		btn_save.addActionListener(this);
		p10.add(btn_save);
		btn_ok = new JButton("OK");
		btn_ok.setToolTipText("Hiermit starten Sie die Kommunikation bzw. den Host.");
		btn_ok.addActionListener(this);
		p10.add(btn_ok);
		btn_quit = new JButton("Schließen");
		btn_quit.setToolTipText("Beendet das Programm und löscht alle bisherigen Eingaben.");
		btn_quit.addActionListener(this);
		p10.add(btn_quit);
		add(p10);
		getRootPane().setDefaultButton(btn_ok);
		pack();
		setLocation((Brieftaube.scr_w-getWidth())/2, (Brieftaube.scr_h-getHeight())/2);
		setVisible(true);
	}

	public void actionPerformed (ActionEvent e) {
		if (e.getSource()==btn_quit)
			System.exit(0);
		else if (e.getSource()==btn_storage) {
			fc.setSelectedFile(new File(storage.getText()));
			if (fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
				storage.setText(fc.getSelectedFile().getAbsolutePath());
		}
		else if (e.getSource()==btn_info)
			new InfoDialog(this);
		else if (e.getSource()==btn_ok || e.getSource()==btn_save) {
			boolean errors = false;
			String info = "";
			if (!storage.getText().matches("[^\\?\\*<>\"|]+")) {
				if (storage.getText().equals(""))
					info += "\n• Sie müssen einen Speicherort für empfangene Dateien angeben!";
				else
					info += "\n• Pfade zu Verzeichnissen dürfen bestimmte reservierte Zeichen nicht enthalten:\n"
						+ "   ? * < > \" |";
				errors = true;
			}
			else if (!new File(storage.getText()).isDirectory() && !new File(storage.getText()).mkdirs()) {
				info += "\n• Das als Speicherort angegebenes Verzeichnis existiert nicht\n"
					+ "   und konnte auch nicht erstellt werden!";
				errors = true;
			}
			if (!port.getText().matches("\\d{1,5}")) {
				if (port.getText().equals(""))
					info += "\n• Sie müssen eine Portnummer angeben!";
				else
					info += "\n• Ports sind ein- bis fünfstellige Zahlen.";
				errors = true;
			}
			if (!user.getText().matches(".{1,32}")) {
				if (user.getText().equals(""))
					info += "\n• Sie müssen einen Benutzernamen eingeben!";
				else
					info += "\n• Benutzernamen dürfen maximal 32 Zeichen lang sein.";
				errors = true;
			}
			if (!addr.getText().matches("([a-zA-Z0-9\\-]\\.?)+")) {
				if (addr.getText().equals(""))
					info += "\n• Sie müssen die IP-Adresse oder den Domänen-Namen des Hosts eingeben!";
				else
					info += "\n• Für die Adresse sind nur Buchstaben, Zahlen, Bindestriche und Punkte erlaubt.\n"
						+ "   Bsp. DNS: der-host.domaene.tld\n"
						+ "   Bsp. IP: 10.11.12.13";
				errors = true;
			}
			if (errors)
				JOptionPane.showMessageDialog(this, "Bei der Überprüfung der Felder sind folgende Fehler aufgetreten:\n"
					+ info, "", JOptionPane.WARNING_MESSAGE);
			else {
				Brieftaube.user = user.getText();
				Brieftaube.addr = addr.getText();
				Brieftaube.port = port.getText();
				Brieftaube.storage = storage.getText();
				Brieftaube.host = chk_host.isSelected();
				Brieftaube.compr = chk_compr.isSelected();
				Brieftaube.local = chk_local.isSelected();
				Brieftaube.smiley = chk_smiley.isSelected();
				Brieftaube.html = chk_html.isSelected();
				if (e.getSource()==btn_save)
					if (Brieftaube.storeConfig())
						JOptionPane.showMessageDialog(this, "Die aktuelle Konfiguration wurde erfolgreich gesichert!",
							"", JOptionPane.INFORMATION_MESSAGE);
					else
						JOptionPane.showMessageDialog(this, "Beim Speichern der Konfiguration ist ein Fehler aufgetreten!",
							"Schreibfehler", JOptionPane.ERROR_MESSAGE);
				else {
					dispose();
					new Thread(new Connection()).start();
					try { finalize(); } catch (Throwable t) { }
				}
			}
		}
	}

	public void itemStateChanged (ItemEvent e) {
		if (e.getStateChange()==ItemEvent.SELECTED) {
			s_addr = addr.getText(); addr.setEnabled(false); addr.setText("localhost");
		}
		else if (e.getStateChange()==ItemEvent.DESELECTED) {
			addr.setText(s_addr); addr.setEnabled(true);
		}
	}

}
