package otherlang;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

import com.darwinsys.util.Debug;

/**
 * ExecDemoNS shows how to execute a program from within Java.
 */
public class ExecDemoNS extends JFrame {
	/** The name of the help file. */
	protected final static String HELPFILE = "./help/index.html";

	/** A stack of process objects; each entry tracks one external running process */
	Stack<Process> pStack = new Stack<Process>();

	/** main - instantiate and run */
	public static void main(String av[]) throws Exception {
		String program = av.length == 0 ? "netscape" : av[0];
		new ExecDemoNS(program).setVisible(true);
	}

	/** The path to the binary executable that we will run */
	protected static String program;

	/** Constructor - set up strings and things. */
	public ExecDemoNS(String prog) {
		super("ExecDemo: " + prog);
		String osname = System.getProperty("os.name");
		if (osname == null)
			throw new IllegalArgumentException("no os.name");
		if (prog.equals("netscape"))
			program = // Windows or UNIX only for now, sorry Mac fans
				(osname.toLowerCase().indexOf("windows")!=-1) ?
				"c:/program files/netscape/communicator/program/netscape.exe" :
				"/usr/local/netscape/netscape";
		else
			program = prog;

		Container cp = getContentPane();
		cp.setLayout(new FlowLayout());
		JButton b;
		cp.add(b=new JButton("Exec"));
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				runProg();
			}
		});
		cp.add(b=new JButton("Wait"));
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				doWait();
			}
		});
		cp.add(b=new JButton("Exit"));
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(0);
			}
		});
		pack();
	}

	/** Start the help, in its own Thread. */
	public void runProg() {

		new Thread() {
			public void run() {

				try {
					// Get the URL for the Help File
					URL helpURL = this.getClass().getClassLoader().
						getResource(HELPFILE);

					// Start Netscape from the Java Application.

					pStack.push(Runtime.getRuntime().exec(program + " " + helpURL));

					Debug.println("trace", "In main after exec " + pStack.size());

				} catch (Exception ex) {
					JOptionPane.showMessageDialog(ExecDemoNS.this,
						"Error" + ex, "Error",
						JOptionPane.ERROR_MESSAGE);
				}
			}
		}.start();

	}

	public void doWait() {
		if (pStack.size() == 0) return;
		Debug.println("trace", "Waiting for process " + pStack.size());
		try {
			pStack.peek().waitFor();
				// wait for process to complete (does not work as expected for Windows programs)
			Debug.println("trace", "Process " + pStack.size() + " is done");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this,
				"Error" + ex, "Error",
				JOptionPane.ERROR_MESSAGE);
		}
		pStack.pop();
	}

}
