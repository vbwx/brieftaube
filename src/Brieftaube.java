import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

final public class Brieftaube {

	public static boolean macosx, running = true, windows;
	public static int scr_w, scr_h;
	public static boolean host = false, compr = true, smiley = true, local = false, html = false;
	public static String user = "", port = "2226", addr = "", storage, home, dir;
	private static float ver = 0.96F;
	
	public static void main (String[] args) {
		if (args.length==1)
			new Niche(new Integer(args[0]));
		else if (args.length>1) {
			System.err.println("Brieftaube Niche: Too many arguments!");
			System.exit(1);
		}
		else
			new Brieftaube();
	}
	
	Brieftaube () {
		macosx = System.getProperty("os.name").equals("Mac OS X");
		windows = System.getProperty("os.name").equals("Windows");
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		scr_w = d.width; scr_h = d.height;
		storage = home = System.getProperty("user.home");
		dir = System.getProperty("user.dir");
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(dir + "/configuration"));
			if (in.readFloat()!=ver) throw new Exception();
			user = (String)in.readObject();
			port = (String)in.readObject();
			addr = (String)in.readObject();
			storage = (String)in.readObject();
			host = in.readBoolean();
			compr = in.readBoolean();
			local = in.readBoolean();
			smiley = in.readBoolean();
			html = in.readBoolean();
			in.close();
			new Thread(new Connection()).start();
		}
		catch (Exception e) {
			new CfgWindow();
		}
	}

	public static boolean storeConfig () {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dir + "/configuration"));
			out.writeFloat(ver);
			out.writeObject(user);
			out.writeObject(port);
			out.writeObject(addr);
			out.writeObject(storage);
			out.writeBoolean(host);
			out.writeBoolean(compr);
			out.writeBoolean(local);
			out.writeBoolean(smiley);
			out.writeBoolean(html);
			out.flush(); out.close();
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}
	
}