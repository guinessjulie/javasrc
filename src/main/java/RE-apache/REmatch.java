package RE-apache;

import org.apache.regexp.*;

/**
 * REsubstr -- demonstrate RE Match -> getParen()
 * @author Ian F. Darwin, http://www.darwinsys.com/
 * @version $Id$
 */
public class REmatch {
	public static void main(String[] argv) throws RESyntaxException {
		
		String patt = "Q[^u]\\d+\\.";
		RE r = new RE(patt);
		String line = "Order QT300. Now!";
		if (r.match(line)) {
			System.out.println(patt + " matches \"" + 
				r.getParen(0) +
				"\" in \"" + line + "\"");
		} else {
			System.out.println("NO MATCH");
		}
	}
}
