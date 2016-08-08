import java.applet.AudioClip;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

final class MsgWindow extends JFrame
	implements ActionListener, ComponentListener, KeyListener, FocusListener, WindowListener, HyperlinkListener {

	private static final long serialVersionUID = 0L;
	private JSplitPane center;
	private JButton btn_info, btn_config, btn_file;
	JToggleButton btn_smileys;
	private JPanel south;
	private JFileChooser fc;
	JTextArea msg;
	private JEditorPane log;
	private JScrollPane scroll1, scroll2;
	private SmileyPalette sp;
	private Calendar cal;
	private AudioClip ac_shake;
	private ConversationHandler h;
	private HTMLEditorKit kit;

	public MsgWindow () {
		super("Warten auf Teilnehmer ...");
		setLayout(new BorderLayout());
		addComponentListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		fc = new JFileChooser(); fc.setDialogType(JFileChooser.OPEN_DIALOG);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setDialogTitle("Datei senden");
		fc.setApproveButtonText("Auswählen");
		sp = new SmileyPalette(this);
		ac_shake = java.applet.Applet.newAudioClip(getClass().getResource("/res/ringer.aiff"));
		log = new JTextPane();
		log.addFocusListener(this); log.addHyperlinkListener(this);
		log.setEditable(false); log.setContentType("text/html");
		log.setText(
			 "<html>\n"
			+"<head>\n"
			+"<base href=\"" + getClass().getResource("/res/") + "\">\n"
			+"<style type=\"text/css\">\n"
			+"	body {\n"
			+"		background-image: url(\"sky.gif\");\n"
			+"		background-position: top left;\n"
			+"		font-family: sans-serif;\n"
			+"		font-size: 11pt;\n"
			+"	}\n"
			+"	.local {\n"
			+"		color: navy;\n"
			+"	}\n"
			+"	.remote {\n"
			+"		color: navy;\n"
			+"		font-weight: bold;\n"
			+"	}\n"
			+"	.msg {\n"
			+"		margin-left: 1cm;\n"
			+"		text-indent: -1cm;\n"
			+"	}\n"
			+"</style>\n"
			+"</head>\n"
			+"<body>\n\n</body></html>"
		);
		kit = (HTMLEditorKit)log.getEditorKit();
		scroll1 = new JScrollPane(log);
		msg = new JTextArea();
		msg.setEnabled(false);
		msg.setTabSize(4);
		msg.setLineWrap(true); msg.setWrapStyleWord(true);
		msg.addKeyListener(this);
		msg.addComponentListener(this);
		scroll2 = new JScrollPane(msg);
		center = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, scroll1, scroll2);
		center.setOneTouchExpandable(false);
		center.setDividerLocation(260);
		add(center, BorderLayout.CENTER);
		south = new JPanel(new FlowLayout(FlowLayout.CENTER));
		if (!Brieftaube.host) {
			btn_info = new JButton("Info");
			btn_info.setToolTipText("Zeigt Informationen über das Programm an.");
			btn_info.addActionListener(this);
			south.add(btn_info);
			btn_config = new JButton("Konfiguration");
			btn_config.setToolTipText("Zeigt die aktuelle Konfiguration an bzw. ändert diese.");
			btn_config.addActionListener(this);
			south.add(btn_config);
		}
		btn_smileys = new JToggleButton("Smileys", false);
		btn_smileys.setToolTipText("Zeigt/versteckt alle verfügbaren Smileys.");
		btn_smileys.addActionListener(this);
		south.add(btn_smileys);
		btn_file = new JButton("Datei senden...");
		btn_file.setToolTipText("Hier können Sie eine zu übertragende Datei auswählen.");
		btn_file.addActionListener(this);
		south.add(btn_file);
		add(south, BorderLayout.SOUTH);
		setSize(440, 380);
		setLocation((Brieftaube.scr_w-getWidth())/2, (Brieftaube.scr_h-getHeight())/2);
		setVisible(true);
	}

	void init (ConversationHandler h) {
		this.h = h;
	}

	void append (String html) {
		if (Brieftaube.smiley)
			for (int i = 0; i<SmileyPalette.alias.length; i++)
				html = html.replaceAll("(<br>|\\s)\\Q" + to_html(SmileyPalette.alias[i]) + "\\E(\\s|</p>|<br>)",
					"$1<img src=\"" + SmileyPalette.canonical[i] + ".gif\" alt=\"" + SmileyPalette.alias[i] + "\">$2");
		try {
			kit.read(new StringReader("\n" + html), log.getDocument(), log.getDocument().getLength());
		}
		catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Ein schwerwiegendes Problem ist aufgetreten:\n\n" + e,
				"Unbekannte Ausnahme", JOptionPane.ERROR_MESSAGE);
		}
		log.setCaretPosition(log.getDocument().getLength());
	}

	public void actionPerformed (ActionEvent e) {
		if (e.getSource()==btn_info)
			new InfoDialog(this);
		else if (e.getSource()==btn_file) {
			if (fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
				h.newTransmission(fc.getSelectedFile().getAbsolutePath());
		}
		else if (e.getSource()==btn_smileys)
			sp.setVisible(!sp.isVisible());
		else if (e.getSource()==btn_config) {
			String s;
			s = "Adresse des Hosts: " + Brieftaube.addr + "\n"
				+ "Geöffneter Port: " + Brieftaube.port + "\n"
				+ "Benutzername: " + Brieftaube.user + "\n"
				+ "Speicherort: " + Brieftaube.storage + "\n"
				+ "Optionen:\n" + (Brieftaube.local ? "   • Lokaler Status\n" : "")
				+ (Brieftaube.smiley ? "   • Smileys als Bilder\n" : "")
				+ (Brieftaube.compr ? "   • Kompression\n" : "")
				+ (Brieftaube.html ? "   • HTML in Nachrichten auswerten\n" : "")
				+ "\nWollen Sie die aktuelle Konfiguration verändern?\n"
				+ "(Achtung! Damit wird die laufende Kommunikation beendigt!)";
			if (JOptionPane.showConfirmDialog(this, s, "", JOptionPane.YES_NO_OPTION,
				JOptionPane.PLAIN_MESSAGE)==JOptionPane.YES_OPTION) {
				dispose();
				new CfgWindow();
				try { finalize(); } catch (Throwable t) { }
				return;
			}
		}
		msg.requestFocus();
	}

	public void componentHidden (ComponentEvent e) { }

	public void componentMoved (ComponentEvent e) { }

	public void componentResized (ComponentEvent e) {
		if (e.getSource()==this)
			center.setDividerLocation(260+getHeight()-380);
	}

	public void componentShown (ComponentEvent e) {
		msg.requestFocus();
	}

	void shake () {
		int x = getX(), y = getY();
		ac_shake.play();
		for (int i = 0; i<7; i++) {
			setLocation(x-3, y-3);
			setLocation(x-3, y+3);
			setLocation(x+3, y);
			setLocation(x, y+3);
			setLocation(x, y);
		}
		//ac_shake.stop();
	}

	static String quote (String str) {
		return ('`' + str.replace("\\", "\\\\").replace("`", "\\`").replace("\n", "\\n").replace("\r", "\\r") + '`');
	}

	static String to_html (String str) {
		return str
			.replace("&", "&amp;")
			.replace("<", "&lt;")
			.replace(">", "&gt;")
			.replace("\"", "&quot;");
	}

	public void keyPressed (KeyEvent e) {
		if (e.isShiftDown() && e.getKeyCode()==KeyEvent.VK_ENTER)
			msg.insert("\n", msg.getCaretPosition());
		else if (e.getKeyCode()==KeyEvent.VK_ENTER) {
			String tmp = msg.getText();
			if (tmp.matches("^\\s*$")) {
				if (e.isControlDown()) h.out.println("RING");
				return;
			}
			if (!Brieftaube.html || tmp.matches(".*<(script|embed|object|form|input|a).*")) tmp = to_html(tmp);
			tmp = tmp
				.replaceAll("\\s+$", "")
				.replaceAll("^\\s+", "")
				.replace("\n", "<br>");
			cal = new GregorianCalendar();
			msg.setText("");
			append("<p class=\"msg\"><b>" + to_html(Brieftaube.user) + " schrieb um " + cal.get(Calendar.HOUR_OF_DAY) + ":"
				+ cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + "</b><br>" + tmp + "</p>");
			tmp = quote(tmp);
			if (e.isControlDown()) tmp = "RING " + tmp;
			h.out.println(tmp);
		}
		else if (e.getKeyCode()==KeyEvent.VK_BACK_SPACE) {
			int pos = msg.getCaretPosition();
			if (pos==0 || msg.getText().length()<2 || msg.getText().length()<pos+1) return;
			char c = msg.getText().charAt(pos-1);
			if ((c = matchingParen(c))==0) return;
			if (msg.getText().charAt(pos)!=c) return;
			msg.moveCaretPosition(pos+1);
			msg.cut();
		}
	}

	public void keyReleased (KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_ENTER && !e.isShiftDown()) msg.setText("");
	}

	public void keyTyped (KeyEvent e) {
		char ins = e.getKeyChar();
		if ((ins = matchingParen(ins))==0) return;
		msg.insert(new Character(ins).toString(), msg.getCaretPosition());
		msg.setCaretPosition(msg.getCaretPosition()-1);
	}

	private static char matchingParen (char p) {
		if      (p=='(') return ')';
		else if (p=='[') return ']';
		else if (p=='{') return '}';
		else if (p=='<') return '>';
		else if (p=='"') return   p;
		else if (p=='`') return   p;
		else             return   0;
	}

	public void focusGained (FocusEvent e) {
		msg.grabFocus();
	}

	public void focusLost (FocusEvent e) { }

	public void windowActivated (WindowEvent e) { }

	public void windowClosed (WindowEvent e) { }

	public void windowClosing (WindowEvent e) {
		h.interrupt(); h = null;
		dispose();
		if (!Brieftaube.host) System.exit(0);
		try { finalize(); } catch (Throwable t) { }
	}

	public void windowDeactivated (WindowEvent e) { }

	public void windowDeiconified (WindowEvent e) { }

	public void windowIconified (WindowEvent e) { }

	public void windowOpened (WindowEvent e) { }

	public void hyperlinkUpdate (HyperlinkEvent e) {
		if (e.getEventType()!=HyperlinkEvent.EventType.ACTIVATED) return;
		System.out.println(e.getURL());
		try {
			if (Brieftaube.windows)
				Runtime.getRuntime().exec("start " + e.getURL());
			else if (Brieftaube.macosx)
				Runtime.getRuntime().exec("open " + e.getURL());
			else
				JOptionPane.showMessageDialog(this, "Die übertragene Datei ist unter folgendem Pfad zu finden:\n\n"
					+ e.getURL().getPath(), "Dateiübertragung", JOptionPane.PLAIN_MESSAGE);
		}
		catch (IOException x) {
			JOptionPane.showMessageDialog(this, x, "Fehler", JOptionPane.ERROR_MESSAGE);
		}
	}

}
