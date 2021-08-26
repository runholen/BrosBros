
package kbxm;

/*
	A floating point mixer/resampler using linear interpolation.
*/
class LinearMixer implements Mixer {

	public final void mix(
			XSample xsam, double samplePos, double step,
			double volumeL, double volumeR,
			float[] outputL, float[] outputR, int length ) {

		short[] samples = xsam.data;
		int loopStart = xsam.loopStart;
		int loopLength = xsam.loopLength;
		boolean bidi = xsam.bidi;
		double sample, input1, input2;
		for( int pos=0; pos<length; pos++ ) {
			int spos = (int)samplePos;
			// Get first sample
			if( spos >= loopStart ) {
				if( bidi ) {
					int lidx = (spos-loopStart)%(loopLength<<1);
					if( lidx >= loopLength ) // Reverse
						input1 = samples[ loopStart+(loopLength<<1)-lidx-1 ];
					else // Forward
						input1 = samples[ loopStart+lidx ];
				} else {
					input1 = samples[ loopStart + (spos-loopStart)%loopLength ];
				}
			} else { // Not in loop
				input1 = samples[spos];
			}
			// Get second sample
			spos++;
			if( spos >= loopStart ) {
				if( bidi ) {
					int lidx = (spos-loopStart)%(loopLength<<1);
					if( lidx >= loopLength ) // Reverse
						input2 = samples[ loopStart+(loopLength<<1)-lidx-1 ];
					else // Forward
						input2 = samples[ loopStart+lidx ];
				} else {
					input2 = samples[ loopStart + (spos-loopStart)%loopLength ];
				}
			} else { // Not in loop
				input2 = samples[spos];
			}
			// Interpolate
			sample = ( (input2-input1)*(samplePos%1) + input1 ) / 32768;
			outputL[pos] += (float)(sample * volumeL);
			outputR[pos] += (float)(sample * volumeR);
			samplePos += step;
		}
	}
}



