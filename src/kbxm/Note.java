
package kbxm;

/*
	A FastTracker 2 Note.
*/
public class Note {
	public int key, instrument, volume;
	public int effect, effectP, effectP1, effectP2;
	
	public final String toString() {
		char[] hex = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		StringBuffer s = new StringBuffer();
		s.append("[");
		s.append(hex[key>>4]);
		s.append(hex[key&0xF]);
		s.append(" ");
		s.append(hex[instrument>>4]);
		s.append(hex[instrument&0xF]);
		s.append(" ");
		s.append(hex[volume>>4]);
		s.append(hex[volume&0xF]);
		s.append(" ");
		s.append(hex[effect]);
		s.append(hex[effectP1]);
		s.append(hex[effectP2]);
		s.append("] ");
		return s.toString();
	}
}
