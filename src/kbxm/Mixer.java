
package kbxm;

/*
	Floating-point mixer interface for FastTracker 2 style samples.
*/
interface Mixer {
	/*
		sample     - The XSample object containing the source samples.
		samplePos  - fractional starting position in input.
		step       - number of input samples per output sample.
		volumeL    - amount to multiply left output samples by.
		volumeR    - amount to multiply right output samples by.
		outputL    - left output buffer.
		outputR    - right output buffer.
		length     - number of output samples to produce.
	*/
	public void mix(
			XSample sample, double samplePos, double step,
			double volumeL, double volumeR,
			float[] outputL, float[] outputR, int length );
}

