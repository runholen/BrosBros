
package kbxm;

/*
	KBXM Replay Engine (Third?, Fourth?, Fifth?, Sixth? Attempt ..)

	Features:
		Reasonable quality XM playback.
		Volume ramping for click reduction.
		Floating point output.

	Design Goals:
		Simplicity
		Play XMs :)
*/
public class KBXM {
	public static final String VERSION = "ALPHA 0.7";

	private XModule module;
	private Sequencer sequencer;
	private Mixer mixer;

	private double sampleRate, gain, time;

	private final int VOL_RAMP_LEN = 16;
	private float[] vRampL = new float[ VOL_RAMP_LEN ];
	private float[] vRampR = new float[ VOL_RAMP_LEN ];

	/*
		Initialise a KBXM replay object.
	*/
	public KBXM( XModule module ) {
		setSampleRate(44100);
		setResamplingQuality(1);
		setModule(module);
	}

	/*
		Set the sampling rate of playback (default 44100hz).
	*/
	public void setSampleRate( double sampleRate ) {
		this.sampleRate = sampleRate;
	}

	/*
		Set the quality level of the resampling routine.
		
			0           - Nearest Neighbour, fastest but low quality.
			1 and above - Linear Interpolation, fast, good quality.

		Better quality resampling may be implemented in future.
		The default is 1 (linear interpolation)
	*/
	public void setResamplingQuality( int n ) {
		if(n<0) n=0;
		switch(n){
			case 0:
				mixer = new NearestMixer();
				break;
			default:
				mixer = new LinearMixer();
		}
	}

	/*
		Render a tick of stereo audio into the buffers starting from 0.
		The number of samples calculated is returned, which is never
		more than 0.085*sample_rate (3749 samples at 44100hz).
	*/
	public int getAudio( float[] lBuf, float[] rBuf ) {
		double tickTime = sequencer.getTickTime();
		int len = (int)(sampleRate*tickTime);
		Channel[] channels = module.channels;
		for( int n=0; n<channels.length; n++ ) {
			Channel c = channels[n];
			XSample s = c.sample;
			double step = Math.pow(2,c.mixPitch)*44100/sampleRate;
			double volL = gain * c.mixVol * (0.5 - (0.5*c.mixPan));
			double volR = gain * c.mixVol * ((0.5*c.mixPan) + 0.5);
			if( ( c.mixVol > 0 ) && !( s.loopLength <= 1 && c.samplePos >= s.loopStart ) ){
				// Don't mix if silent
				mixer.mix( s, c.samplePos, step, volL, volR, lBuf, rBuf, len+VOL_RAMP_LEN );
			}
			c.samplePos += step*len;
		}
		for( int n=0; n<VOL_RAMP_LEN; n++ ) {
			double fade = n/(double)VOL_RAMP_LEN;
			lBuf[n] = (float)( lBuf[n]*fade + vRampL[n]*(1-fade) );
			rBuf[n] = (float)( rBuf[n]*fade + vRampR[n]*(1-fade) );
			vRampL[n] = lBuf[len+n];
			vRampR[n] = rBuf[len+n];
			lBuf[len+n] = rBuf[len+n] = 0;
		}
		sequencer.tick();
		time += tickTime;
		return len;
	}

	/*
		Set a different Module object to play.
	*/
	public void setModule( XModule module ) {
		this.module = module;
		sequencer = new Sequencer( module );
		gain = 0.25;
	}

	/*
		Seek playback to the specified time in seconds.
	*/
	public void seek( double t ) {
		if( t < time ) sequencer.reset();
		while( time < t ) {
			sequencer.tick();
			time += sequencer.getTickTime();
		}
	}
}

