
package kbxm;

/*
	A FastTracker 2 Envelope.
*/
class Envelope {
	public int[] points;
	public int numPoints, susPoint, loopStart, loopEnd;
	public boolean on, sustain, loop;

	public Envelope() {
		points = new int[] {0,0};
		numPoints = 1;
	}

	public String toString() {
		String s;
		s  = "Envelope. On: "+on+" Num Points: "+numPoints;
		s += "\n        Sustain On: "+sustain+" Sus Point: "+susPoint;
		s += "\n        Loop On: "+loop+" LStart: "+loopStart+" LEnd: "+loopEnd;
		s += "\n        Points: ";
		for( int e=0; e<numPoints; e++ ) { 
			s += "("+points[e*2]+","+points[e*2+1]+"), ";
		}
		return s;
	}
}

