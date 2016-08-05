import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;

final class NicheHandler implements Runnable {
	
	private static Socket s1 = null, s2 = null;
	private int listen = 0;
	private static int ver1, ver2;
	private static String user1 = null, user2 = null;
	
	public NicheHandler (Socket s) {
		if (s1==null) s1 = s;
		else s2 = s;
	}
	
	public NicheHandler (int listen) {
		this.listen = listen;
	}

	public void run () {
		try {
			String str, tmp;
			BufferedReader in;
			if (s2==null) {
				in = new BufferedReader(new InputStreamReader(s1.getInputStream()));
				do {
					while (!ConversationHandler.getField(str = in.readLine(), 1).equals("CIAO")) ;
					tmp = ConversationHandler.getField(str, 2);
					ver1 = new Integer(tmp);
					user1 = ConversationHandler.getField(str, 3);
				} while (tmp.equals("") || user1.equals(""));
			}
			else if (listen!=0) {
				while (s1==null || s2==null || user1==null || user2==null) ;
				PrintStream out;
				if (listen==1) {
					in = new BufferedReader(new InputStreamReader(s1.getInputStream()));
					out = new PrintStream(s2.getOutputStream());
				}
				else if (listen==2) {
					in = new BufferedReader(new InputStreamReader(s2.getInputStream()));
					out = new PrintStream(s1.getOutputStream());
				}
				else return;
				while (!(str = in.readLine().trim()).equals("CIAO"))
					out.println(str);
				try {
					s1.close(); s2.close();
				} catch (Exception e) { }
			}
			else {
				in = new BufferedReader(new InputStreamReader(s2.getInputStream()));
				PrintStream out1 = new PrintStream(s1.getOutputStream());
				PrintStream out2 = new PrintStream(s2.getOutputStream());
				do {
					while (!ConversationHandler.getField(str = in.readLine(), 1).equals("CIAO")) ;
					tmp = ConversationHandler.getField(str, 2);
					ver2 = new Integer(tmp);
					user2 = ConversationHandler.getField(str, 3);
				} while (tmp.equals("") || user2.equals(""));
				out1.println("ACK " + ver2 + " " + MsgWindow.quote(user2));
				out2.println("ACK " + ver1 + " " + MsgWindow.quote(user1));
			}
		}
		catch (SocketException e) {
			System.out.println("One client has gone offline.");
		}
		catch (Exception e) {
			System.err.println("Brieftaube Niche: " + e);
			System.exit(3);
		}
	}
	
}