import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.GregorianCalendar;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

final class TransceiverPanel extends JFrame implements WindowListener {

	private static final long serialVersionUID = 0L;
	private JPanel p;
	JProgressBar prog;
	JLabel name;
	private ConversationHandler h;
	private static int y = 0;
	String cname;
	boolean send;
	
	public TransceiverPanel (ConversationHandler h) {
		super();
		this.h = h;
		if (Brieftaube.local)
			h.a.append("<p class=\"local\">Um " + new GregorianCalendar().getTime() + " wurde eine neue Datei&uuml;bertragung mit "
				+ h.s.getInetAddress().getHostAddress() + " gestartet.</p>");
		setLayout(new GridLayout(2, 1));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p.add(name = new JLabel());
		add(p);
		prog = new JProgressBar();
		prog.setMinimum(0); prog.setStringPainted(false);
		add(prog);
		setAlwaysOnTop(true);
		setResizable(false); setSize(270, 70);
		setLocation(Brieftaube.scr_w-270, y);
		setFocusableWindowState(false); setVisible(true);
		y = getY() + 70;
	}

	public void windowActivated (WindowEvent e) { }

	public void windowClosed (WindowEvent e) {
		y -= 70;
	}

	public void windowClosing (WindowEvent e) {
		if (Brieftaube.local)
			h.a.append("<p class=\"local\">Die &Uuml;bertragung der Datei <u>" + MsgWindow.to_html(name.getText()) +  "</u>"
				+ " wurde abgebrochen.</p>");
		abort();
	}

	public void windowDeactivated (WindowEvent e) { }

	public void windowDeiconified (WindowEvent e) { }

	public void windowIconified (WindowEvent e) { }

	public void windowOpened (WindowEvent e) { }

	void abort () {
		if (send) {
			h.closeTransmission(cname);
			dispose();
			if (h.out!=null)
				h.out.println("ABORT " + MsgWindow.quote(cname));
		}
		else {
			h.closeFile(cname, true);
			if (h.out!=null)
				h.out.println("ABORT! " + MsgWindow.quote(cname));
		}
		try { finalize(); } catch (Throwable t) { }
	}
	
	void finish () {
		if (send)
			h.closeTransmission(cname);
		else
			h.closeFile(cname, false);
		dispose(); try { finalize(); } catch (Throwable t) { }
	}
	
}