
package kbxm;

/*
	A low quality mixer/resampler using nearest neighbour resampling.
*/
class NearestMixer implements Mixer {

	public final void mix(
			XSample xsam, double samplePos, double step,
			double volumeL, double volumeR,
			float[] outputL, float[] outputR, int length ) {

		short[] samples = xsam.data;
		int loopStart = xsam.loopStart;
		int loopLength = xsam.loopLength;
		boolean bidi = xsam.bidi;
		double sample;
		for( int pos=0; pos<length; pos++ ) {
			int spos = (int)samplePos;
			// Get sample
			if( spos >= loopStart ) {
				if( bidi ) {
					int lidx = (spos-loopStart)%(loopLength<<1);
					if( lidx >= loopLength ) // Reverse
						sample = samples[ loopStart+(loopLength<<1)-lidx-1 ];
					else // Forward
						sample = samples[ loopStart+lidx ];
				} else {
					sample = samples[ loopStart + (spos-loopStart)%loopLength ];
				}
			} else { // Not in loop
				sample = samples[spos];
			}
			sample /= 32768;
			outputL[pos] += (float)(sample * volumeL);
			outputR[pos] += (float)(sample * volumeR);
			samplePos += step;
		}
	}
}



