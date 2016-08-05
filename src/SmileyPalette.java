import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

final class SmileyPalette extends JDialog implements MouseListener, WindowListener {

	private static final long serialVersionUID = 0L;
	private JPanel p;
	private MsgWindow owner;
	private JScrollPane scroll;
	private JLabel l[];
	public static String alias[] = {
		"<_<", "-_-",        ":!",   ":(",  ":)",    ":/",       ":?",   ":D",      ":P",     ":angel:", "0:)",      ">:(",
		":blink:", ":blush:", ":clap:",   ":cry:",  ":dance:", ":devil:", ":flowers:", ":head:",   ":huh:", ":lol:", ":lookleft:",
		":mate:", ":mellow:", ":o",   ":photo:", ">_<",   ":rofl:", ":rolleyes:", ":sailor:", ":shy:", ":sleep:",  ":sly:", "(:",
		":x",     ":thumbsup:", ":tomato:", ":twothumbs:", ":unsure:", ":w00t:", ":whistle:", ":wub:", ";)",   "B)",   "^_^",
		":angry:", "->",    ":|",         ":heart:", ":sick:"
	}, canonical[] = {
		"dry", "closedeyes", "excl", "sad", "smile", "confused", "ques", "biggrin", "tongue", "angel",   "innocent", "mad",
		"blink",   "blush",   "clapping", "crying", "kicking", "devil",   "flowers",   "wallbash", "huh",   "lol",   "lookleft",
		"mate",   "mellow",   "ohmy", "photo",   "pinch", "rofl",   "rolleyes",   "sailor",   "shy",   "sleeping", "sly",   "upsidedown",
		"shutup", "thumbup",    "tomato",   "thumbsup",    "unsure",   "w00t",   "whistling", "wub",   "wink", "cool", "happy",
		"annoyed", "arrow", "displeased", "heart",   "sick"
	};
	
	public SmileyPalette (MsgWindow owner) {
		super((Frame)owner, "Smileys", false);
		this.owner = owner;
		p = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.CENTER, VerticalFlowLayout.LEFT));
		l = new JLabel[alias.length];
		for (int i = 0; i < alias.length; i++) {
			l[i] = new JLabel("   "+alias[i], new ImageIcon(getClass().getResource("/res/"+canonical[i]+".gif")), JLabel.LEFT);
			l[i].addMouseListener(this);
			p.add(l[i]);
		}
		scroll = new JScrollPane(p);
		add(scroll);
		addWindowListener(this);
		owner.toFront(); setFocusableWindowState(false);
	}

	public void setVisible (boolean visible) {
		if (visible) {
			setSize(170, owner.getHeight());
			int x, y, w, scr;
			x = owner.getX(); y = owner.getY();
			w = owner.getWidth();
			scr = Brieftaube.scr_w;
			if (x+w+170 <= scr)
				setLocation(x+w, y);
			else if (x-170 >= 0)
				setLocation(x-170, y);
			else {
				owner.setLocation(Math.max(scr-(w+170), 0), y);
				x = owner.getX();
				if (x+w+170 > scr) {
					w = scr-170;
					owner.setSize(w, owner.getHeight());
				}
				setLocation(x+w, y);
			}
		}
		super.setVisible(visible);
	}
	
	public void mouseClicked (MouseEvent e) {
		for (int i = 0; i < l.length; i++)
			if (e.getSource()==l[i]) {
				owner.msg.insert(" " + (Brieftaube.html ? MsgWindow.to_html(alias[i]) : alias[i]) + " ", owner.msg.getCaretPosition());
				break;
			}
	}

	public void mouseEntered (MouseEvent e) { }

	public void mouseExited (MouseEvent e) { }

	public void mousePressed (MouseEvent e) { }

	public void mouseReleased (MouseEvent e) { }

	public void windowActivated (WindowEvent e) { }

	public void windowClosed (WindowEvent e) { }

	public void windowClosing (WindowEvent e) {
		owner.btn_smileys.doClick();
	}

	public void windowDeactivated (WindowEvent e) { }

	public void windowDeiconified (WindowEvent e) { }

	public void windowIconified (WindowEvent e) { }

	public void windowOpened (WindowEvent e) { }

}