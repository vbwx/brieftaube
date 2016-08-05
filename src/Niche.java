import java.net.ServerSocket;
import java.net.Socket;

final class Niche {
	
	private ServerSocket h;
	private Socket s1, s2;
	
	public Niche (int port) {
		try {
			h = new ServerSocket(port);
			System.out.println("Brieftaube Niche has been instantiated.");
			s1 = h.accept();
			new Thread(new NicheHandler(s1)).start();
			s2 = h.accept();
			new Thread(new NicheHandler(s2)).start();
			h.close();
			new Thread(new NicheHandler(1)).start();
			new Thread(new NicheHandler(2)).start();
		}
		catch (Exception e) {
			System.err.println("Brieftaube Niche: " + e);
			System.exit(2);
		}
	}

}