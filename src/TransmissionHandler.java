import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

final class TransmissionHandler implements Runnable {
	
	TransceiverPanel a;
	private PrintStream s_out;
	private FileOutputStream f_out = null;
	private FileInputStream f_in;
	private boolean compr;
	private int len;
	private boolean running = true;
	String fname;
	private MsgWindow msg;
	
	public TransmissionHandler (String cname, PrintStream s_out, TransceiverPanel a, MsgWindow msg) {
		this.a = a;
		this.s_out = s_out;
		this.msg = msg;
		a.send = true; a.setTitle("Datei wird gesendet"); a.cname = cname;
		fname = cname;
		cname = cname.substring(cname.lastIndexOf(File.separatorChar)+1);
		try {
			f_in = new FileInputStream(fname);
			len = (int) new File(fname).length();
			a.prog.setMaximum(len);
		}
		catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(a, "Die angegebene Datei konnte nicht gefunden werden:\n\n" + fname,
				"", JOptionPane.ERROR_MESSAGE);
			a.abort(); running = false;
		}
		a.name.setText(cname);
	}
	
	public TransmissionHandler (String cname, boolean compr, int len, TransceiverPanel a, MsgWindow msg) {
		String tmp;
		int n = 0;
		this.compr = compr; this.len = len; this.a = a;
		this.msg = msg;
		a.cname = cname; a.send = false; a.setTitle("Datei wird gespeichert"); a.prog.setMaximum(len);
		tmp = fname = Brieftaube.storage + File.separatorChar + cname.substring(cname.lastIndexOf(File.separatorChar)+1);
		if (tmp.contains("."))
			while (new File(tmp).exists())
				tmp = fname.substring(0, fname.lastIndexOf('.')) + "_" + ++n + fname.substring(fname.lastIndexOf('.'));
		else
			while (new File(tmp).exists())
				tmp = fname + "_" + ++n;
		fname = tmp;
		try {
			new File(fname).createNewFile();
		}
		catch (IOException e) {
			JFileChooser fc = new JFileChooser(Brieftaube.home);
			fc.setDialogType(JFileChooser.SAVE_DIALOG); fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setDialogTitle("WŠhlen Sie einen anderen Pfad"); fc.setApproveButtonText("Speichern");
			if (fc.showSaveDialog(a)==JFileChooser.APPROVE_OPTION)
				fname = fc.getSelectedFile().getAbsolutePath();
			else {
				a.abort();
				return;
			}
		}
		try {
			f_out = new FileOutputStream(fname);
		}
		catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(a, "Die angegebene Datei konnte nicht gefunden werden:\n\n" + fname,
				"", JOptionPane.ERROR_MESSAGE);
			a.abort();
			return;
		}
		a.name.setText(fname.substring(fname.lastIndexOf(File.separatorChar)+1));
	}

	public void run () {
		try {
			byte[] data = new byte[1024];
			if (Brieftaube.compr) {
				s_out.println("NEW! " + MsgWindow.quote(fname) + " " + len);
				ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
				GZIPOutputStream gz_out = new GZIPOutputStream(out);
				while (len>0 && running) {
					if (f_in.read(data, 0, 1024)==-1) break;
					len -= data.length;
					gz_out.write(data, 0, 1024);
					gz_out.finish(); gz_out.flush();
					a.prog.setValue(a.prog.getMaximum()-len);
					if (out.size()==0 || !running) break;
					data = out.toByteArray(); out.reset();
					s_out.println("FILE " + MsgWindow.quote(fname) + " " + MsgWindow.quote(new String(data)));
					data = new byte[1024]; gz_out = new GZIPOutputStream(out);
				}
				gz_out.close(); out.close();
			}
			else {
				s_out.println("NEW " + MsgWindow.quote(fname) + " " + len);
				while (len>0 && running) {
					if (f_in.read(data, 0, 1024)==-1) break;
					len -= data.length;
					a.prog.setValue(a.prog.getMaximum()-len);
					if (data.length==0 || !running) break;
					s_out.println("FILE " + MsgWindow.quote(fname) + " " + MsgWindow.quote(new String(data)));
					try { Thread.sleep(10); } catch (Exception e) { }
					data = new byte[1024];
				}
			}
			f_in.close();
			msg.append("<p class=\"remote\">Die Datei <a href=\"file://" + MsgWindow.to_html(fname) + "\">" +
				MsgWindow.to_html(a.name.getText()) + "</a> wurde erfolgreich &uuml;bertragen.</p>");
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(a, e, "I/O-Fehler", JOptionPane.ERROR_MESSAGE);
			if (Brieftaube.local)
				msg.append("<p class=\"local\">Beim &Uuml;bertragen der Datei <u>" + MsgWindow.to_html(a.name.getText()) +
					"</u> sind Fehler aufgetreten.</p>");
			a.abort();
		}
		a.finish();
	}
	
	void append (byte[] content) {
		try {
			if (compr) {
				int b;
				GZIPInputStream gz_in = new GZIPInputStream(new ByteArrayInputStream(content));
				while ((b = gz_in.read())!=-1 && len!=0) { f_out.write(b); --len; }
				gz_in.close();
			}
			else {
				f_out.write(content, 0, Math.min(len, 1024));
				len -= Math.min(len, 1024);
			}
			f_out.flush();
			a.prog.setValue(a.prog.getMaximum()-len);
			if (len==0) {
				f_out.close();
				msg.append("<p class=\"remote\">Die Datei <a href=\"file://" + MsgWindow.to_html(fname) + "\">" +
					MsgWindow.to_html(a.name.getText()) + "</a> wurde erfolgreich empfangen.</p>");
				a.finish();
			}
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(a, e, "I/O-Fehler", JOptionPane.ERROR_MESSAGE);
			if (Brieftaube.local)
				msg.append("<p class=\"local\">Beim Speichern der Datei <u>" + MsgWindow.to_html(a.name.getText()) +
					"</u> sind Fehler aufgetreten.</p>");
			a.abort();
		}
	}
	
	public void interrupt () {
		running = false;
		if (f_out!=null) try { f_out.close(); } catch (Exception e) { }
		try { finalize(); } catch (Throwable t) { }
	}

}