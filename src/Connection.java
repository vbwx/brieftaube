import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

final class Connection implements Runnable {
	
	private Socket remote = null;
	private ServerSocket local = null;
	
	public Connection () {
		remote = null;
		if (Brieftaube.host)
			try {
				local = new ServerSocket(new Integer(Brieftaube.port));
			}
			catch (BindException e) {
				JOptionPane.showMessageDialog(new CfgWindow(), "Der von Ihnen gewählte Port ist bereits in Verwendung!",
					"", JOptionPane.WARNING_MESSAGE);
				try { finalize(); } catch (Throwable t) { }
			}
			catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Eine Ausnahme ist aufgetreten:\n\n" + e,
					"Netzwerkproblem", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		else
			try {
				remote = new Socket(Inet4Address.getByName(Brieftaube.addr), new Integer(Brieftaube.port));
			}
			catch (UnknownHostException e) {
				JOptionPane.showMessageDialog(new CfgWindow(), "Der Host konnte nicht erreicht werden!\n"
					+ "Überprüfen Sie die angegebene Adresse, Portnummer und ob Sie mit dem\nNetzwerk oder dem "
					+ "Internet verbunden sind.", "Verbindung gescheitert", JOptionPane.WARNING_MESSAGE);
			}
			catch (ConnectException e) {
				JOptionPane.showMessageDialog(new CfgWindow(), "Der Host hat die Verbindung abgelehnt. Versuchen Sie es "
					+ "später noch einmal.", "Verbindung gescheitert", JOptionPane.ERROR_MESSAGE);
			}
			catch (Exception e) {
				JOptionPane.showMessageDialog(null, e, "Unbekannte Ausnahme", JOptionPane.ERROR_MESSAGE);
				System.exit(2);
			}
	}
	
	public void run () {
		ConversationHandler h = null;
		MsgWindow w = null;
		HostPanel p = null;
		if (Brieftaube.host) {
			if (local==null) {
				try { finalize(); } catch (Throwable t) { }
			}
			else {
				p = new HostPanel(this);
				try {
					while (true) {
						remote = local.accept();
						p.increase();
						w = new MsgWindow();
						h = new ConversationHandler(remote, w, p);
						w.init(h);
						new Thread(h).start();
					}
				}
				catch (IOException e) { }
			}
		}
		else {
			if (remote==null) {
				try { finalize(); } catch (Throwable t) { }
			}
			else {
				w = new MsgWindow();
				h = new ConversationHandler(remote, w);
				w.init(h);
				new Thread(h).start();
			}
		}
	}
	
	void close () {
		try { local.close(); finalize(); } catch (Throwable t) { }
	}

}