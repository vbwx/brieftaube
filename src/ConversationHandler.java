import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;

import javax.swing.JOptionPane;

final class ConversationHandler implements Runnable {

	MsgWindow a;
	Socket s;
	private String user;
	PrintStream out;
	HostPanel p;
	private BufferedReader in;
	private String tmp;
	private int ver;
	private Hashtable<String, TransmissionHandler> get, send;
	Calendar cal;
	private boolean running = true;

	public ConversationHandler (Socket s, MsgWindow a, HostPanel p) {
		this(s, a);
		this.p = p;
	}

	public ConversationHandler (Socket s, MsgWindow a) {
		this.a = a;
		this.s = s;
		get = new Hashtable<String, TransmissionHandler>();
		send = new Hashtable<String, TransmissionHandler>();
		try {
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new PrintStream(s.getOutputStream());
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Eine Ausnahme ist aufgetreten:\n\n" + e, "I/O-Fehler", JOptionPane.ERROR_MESSAGE);
			System.exit(4);
		}
	}

	public void run () {
		try {
			TransmissionHandler ptr;
			String str;
			if (Brieftaube.host) {
				if (Brieftaube.local) {
					cal = new GregorianCalendar();
					a.append("<p class=\"local\">" + s.getInetAddress().getHostAddress()
						+ " hat um " + cal.getTime() + " eine Verbindung zu diesem Host aufgebaut.</p>");
				}
				do {
					while (!getField(str = in.readLine(), 1).equals("CIAO")) ;
					tmp = getField(str, 2);
					if ((ver = new Integer(tmp))>0 && Brieftaube.local)
						a.append("<p class=\"local\">Ihr(e) Gespr&auml;chspartner(in) hat eine neuere Version von Brieftaube.</p>");
					else if (ver<0 && Brieftaube.local)
						a.append("<p class=\"local\">Ihr(e) Gespr&auml;chspartner(in) hat eine &auml;ltere Version von Brieftaube.\n"
							+ "Eventuell werden einige Features nicht funktionieren.</p>");
					user = getField(str, 3);
				} while (tmp.equals("") || user.equals(""));
				out.println("ACK 0 " + MsgWindow.quote(Brieftaube.user));
			}
			else {
				out.println("CIAO 0 " + MsgWindow.quote(Brieftaube.user));
				if (Brieftaube.local) {
					cal = new GregorianCalendar();
					a.append("<p class=\"local\">Verbindung zu " +
						s.getInetAddress().getHostAddress() + " wurde um " + cal.getTime() + " aufgebaut.</p>");
				}
				do {
					while (!getField(str = in.readLine(), 1).equals("ACK")) ;
					tmp = getField(str, 2);
					if ((ver = new Integer(tmp))>0 && Brieftaube.local)
						a.append("<p class=\"local\">Ihr(e) Gespr&auml;chspartner(in) hat eine neuere Version von Brieftaube.</p>");
					else if (ver<0 && Brieftaube.local)
						a.append("<p class=\"local\">Ihr(e) Gespr&auml;chspartner(in) hat eine &auml;ltere Version von Brieftaube.\n"
							+ "Eventuell werden einige Features nicht funktionieren.</p>");
					user = getField(str, 3);
				} while (tmp.equals("") || user.equals(""));
			}
			a.append("<p class=\"remote\">" + MsgWindow.to_html(user) + " ist online.</p>");
			a.msg.setEnabled(true); a.msg.grabFocus();
			a.setTitle(Brieftaube.user + " Ñ " + user);
			while (!(str = in.readLine().trim()).equals("CIAO") && running) {
				tmp = null;
				if (str.startsWith("`")) tmp = getField(str, 1);
				else if (getField(str, 1).equals("RING")) {
					tmp = getField(str, 2);
					a.shake();
					if (tmp.equals("")) continue;
				}
				else if (getField(str, 1).equals("NEW") && !getField(str, 2).equals("") && !getField(str, 3).equals(""))
					get.put(getField(str, 2), new TransmissionHandler(getField(str, 2), false, new Integer(getField(str, 3)),
						new TransceiverPanel(this), a));
				else if (getField(str, 1).equals("NEW!") && !getField(str, 2).equals("") && !getField(str, 3).equals(""))
					get.put(getField(str, 2), new TransmissionHandler(getField(str, 2), true, new Integer(getField(str, 3)),
						new TransceiverPanel(this), a));
				else if (getField(str, 1).equals("FILE") && !getField(str, 2).equals("") && !getField(str, 3).equals("")) {
					if ((ptr = get.get(getField(str, 2)))!=null) {
						ptr.append(getField(str, 3).getBytes());
						ptr = null;
					}
				}
				else if (getField(str, 1).equals("ABORT") && !getField(str, 2).equals("")) {
					if ((ptr = get.get(getField(str, 2)))!=null)
						a.append("<p class=\"remote\">Ihr(e) Gespr&auml;chspartner(in) hat die &Uuml;bertragung der Datei <u>" +
							MsgWindow.to_html(ptr.a.name.getText()) + "</u> abgebrochen.</p>");
					closeFile(getField(str, 2), true);
				}
				else if (getField(str, 1).equals("ABORT!") && !getField(str, 2).equals("")) {
					if ((ptr = send.get(getField(str, 2)))!=null)
						a.append("<p class=\"remote\">Ihr(e) Gespr&auml;chspartner(in) hat die Datei <a href=\"file://" +
							MsgWindow.to_html(getField(str, 2)) + "\">" + MsgWindow.to_html(ptr.a.name.getText()) + "</a>" +
							" abgelehnt.</p>");
					closeTransmission(getField(str, 2));
				}
				else continue;
				if (tmp==null) continue;
				cal = new GregorianCalendar();
				if (tmp.matches(".*<(script|embed|object|form|input|a).*")) tmp = MsgWindow.to_html(tmp);
				a.append("<p class=\"msg\"><b>" + MsgWindow.to_html(user) + " schrieb um " + cal.get(Calendar.HOUR_OF_DAY) + ":"
					+ cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND) + "</b><br>" + tmp + "</p>");
			}
			in.close(); out.close(); s.close();
			a.msg.setEnabled(false);
			in = null; out = null;
			if (Brieftaube.local && s!=null && a!=null) {
				cal = new GregorianCalendar();
				a.append("<p class=\"local\">Die Verbindung zu " + s.getInetAddress().getHostAddress() + " wurde um " +
					cal.getTime() + " ordnungsgem&auml;&szlig; abgebaut.</p>");
			}
			s = null;
		}
		catch (Exception e) {
			if (Brieftaube.local && s!=null && a!=null) {
				cal = new GregorianCalendar();
				a.append("<p class=\"local\">Die Verbindung zu " + s.getInetAddress().getHostAddress() + " ist um "
					+ cal.getTime() + " abgebrochen.</p>");
			}
			try { s.close(); s = null; } catch (Exception x) { }
		}
		if (Brieftaube.host) p.decrease();
		if (a!=null) a.append("<p class=\"remote\">" + MsgWindow.to_html(user) + " ist offline.</p>");
	}

	static String getField (String src, int num) {
		String field = "";
		char[] y = new char[src.trim().length()];
		src.trim().getChars(0, src.trim().length(), y, 0);
		int i = 0;
		boolean b = false, esc = false;
		do {
			for (; i<y.length; i++) {
				if (!esc && b && y[i]=='\\') esc = true;
				else if (!esc && y[i]=='`') b = !b;
				else if (num==1 && esc && y[i]=='n') { field += '\n'; esc = false; }
				else if (num==1 && esc && y[i]=='r') { field += '\r'; esc = false; }
				else if (!b && y[i]==' ') break;
				else if (num==1) {
					field += y[i];
					if (esc) esc = false;
				}
			}
			for (; i<y.length && (y[i]==' ' || y[i]=='\n'); i++) ;
		} while (--num!=0);
		return field;
	}

	void interrupt () {
		try {
			out.println("CIAO");
			running = false;
			s.close(); s = null;
			out.close(); in.close();
			out = null; in = null;
		} catch (Exception x) { }
		try { finalize(); } catch (Throwable t) { }
	}

	void closeFile (String cname, boolean unlink) {
		TransmissionHandler ptr = null;
		if ((ptr = get.get(cname))!=null) {
			ptr.a.dispose();
			ptr.interrupt();
			if (unlink) new File(ptr.fname).delete();
			get.remove(cname);
		}
	}

	void closeTransmission (String cname) {
		TransmissionHandler ptr = null;
		if ((ptr = send.get(cname))!=null) {
			ptr.interrupt();
			send.remove(cname);
		}
	}

	void newTransmission (String cname) {
		TransmissionHandler h;
		send.put(cname, h = new TransmissionHandler(cname, out, new TransceiverPanel(this), a));
		new Thread(h).start();
	}

}
