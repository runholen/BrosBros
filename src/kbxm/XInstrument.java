
package kbxm;

import java.io.*;

class XInstrument {
	public String name;
	public int type;
	public XSample[] samples;
	public int[] sampleTable;
	public Envelope volEnv, panEnv;
	
	public int vibratoType, vibratoSweep, vibratoDepth, vibratoRate;
	public int fadeOut;

	/*
		Construct an empty instrument with a single, silent sample.
	*/
	public XInstrument() {
		name = "Empty XInstrument.";
		samples = new XSample[1];
		samples[0] = new XSample();
		sampleTable = new int[97];
		volEnv = new Envelope();
		volEnv.points = new int[24];
		panEnv = new Envelope();
		panEnv.points = new int[24];
	}

	public XInstrument( InputStream i ) throws IOException {
		this();
		// Read Instrument Header
		int size = i.read()|(i.read()<<8)|(i.read()<<16)|(i.read()<<24);
		byte[] buf = new byte[ size ];
		i.read( buf, 4, size-4 );
		// Read Instrument Name and Type
		name = new String( buf, 4, 22, "8859_1" );
		type = buf[26];
		// Read Number Of Samples
		int numSamples = (buf[27]&0xFF)|((buf[28]&0xFF)<<8);
		samples = new XSample[numSamples];
		// Read Extended Header
		if( numSamples > 0 ) {
			// Read Sample Table
			for( int n=0; n<96; n++ ) sampleTable[n+1] = buf[n+33]&0xFF;
			// Read Volume Envelope Points
			for( int n=0; n<24; n++ ) volEnv.points[n] = (buf[n*2+129]&0xFF)|((buf[n*2+130]&0xFF)<<8);
			// Read Panning Envelope Points
			for( int n=0; n<24; n++ ) panEnv.points[n] = (buf[n*2+177]&0xFF)|((buf[n*2+178]&0xFF)<<8);
			// Read Number of Envelope Points
			volEnv.numPoints = buf[225]&0xFF;
			panEnv.numPoints = buf[226]&0xFF;
			// Read Volume Sustain/Loop Points
			volEnv.susPoint = buf[227]&0xFF;
			volEnv.loopStart = buf[228]&0xFF;
			volEnv.loopEnd = buf[229]&0xFF;
			// Read Panning Sustain/Loop Points
			panEnv.susPoint = buf[230]&0xFF;
			panEnv.loopStart = buf[231]&0xFF;
			panEnv.loopEnd = buf[232]&0xFF;
			// Read Volume Envelope Flags
			volEnv.on = ((buf[233]&0x01)!=0);
			volEnv.sustain = ((buf[233]&0x02)!=0);
			volEnv.loop = ((buf[233]&0x04)!=0);
			// Read Panning Envelope Flags
			panEnv.on = ((buf[234]&0x01)!=0);
			panEnv.sustain = ((buf[234]&0x02)!=0);
			panEnv.loop = ((buf[234]&0x04)!=0);
			// Read Auto Vibrato Params
			vibratoType = buf[235]&0xFF;
			vibratoSweep = buf[236]&0xFF;
			vibratoDepth = buf[237]&0xFF;
			vibratoRate = buf[238]&0xFF;
			// Read Volume Fade Param
			fadeOut = (buf[239]&0xFF)|((buf[240]&0xFF)<<8);
			// Read Sample Headers
			for( int n=0; n<numSamples; n++ ) samples[n] = new XSample(i);
			// Read and Decode Sample Data
			for( int n=0; n<numSamples; n++ ) {
				buf = new byte[ samples[n].lengthBytes ];
				i.read( buf, 0, buf.length );
				short[] outbuf = samples[n].data;
				if( samples[n].sixteenBit ) {
					short sam, old = 0; 
					for( int s=0; s<buf.length; s+=2 ) {
						outbuf[s>>1] = (short)((buf[s]&0xFF)|(buf[s+1]<<8));
						sam = (short)(outbuf[s>>1] + old);
						outbuf[s>>1] = sam;
						old = sam;
					}
				} else {
					byte sam, old = 0;
					for( int s=0; s<buf.length; s++ ) {
						sam = (byte)(buf[s] + old);
						old = sam;
						outbuf[s] = (short)(sam<<8);
					}
				}
			}
			// Check and de-cruft envelope data
			// Not really necessary, but it makes me feel better :)
			volEnv.points[0] = 0;
			if( !volEnv.on ) {
				volEnv.numPoints = 1;
				volEnv.points[1] = 0;
				volEnv.sustain = volEnv.loop = false;
			}
			if( !volEnv.sustain ) volEnv.susPoint = 0;
			if( !volEnv.loop ) volEnv.loopStart = volEnv.loopEnd = 0;
			for( int n=1; n<volEnv.numPoints; n++ )
				if( volEnv.points[n*2] <= volEnv.points[(n-1)*2] )
					throw new IOException("Volume Envelope points are corrupt!");
			for( int n=volEnv.numPoints*2; n<volEnv.points.length; n++ ) volEnv.points[n] = 0;
			panEnv.points[0] = 0;
			if( !panEnv.on ) {
				panEnv.numPoints = 1;
				panEnv.points[1] = 0;
				panEnv.sustain = panEnv.loop = false;
			}
			if( !panEnv.sustain ) panEnv.susPoint = 0;
			if( !panEnv.loop ) panEnv.loopStart = panEnv.loopEnd = 0;
			for( int n=1; n<panEnv.numPoints; n++ )
				if( panEnv.points[n*2] <= panEnv.points[(n-1)*2] )
					throw new IOException("Panning Envelope points are corrupt!");
			for( int n=panEnv.numPoints*2; n<panEnv.points.length; n++ ) panEnv.points[n] = 0;
		}
	}
	
	public String toString() {
		String s;
		s  = "XInstrument. Name: "+name+" Type: "+type+" Samples: "+samples.length;
		if( samples.length > 0 ) {
			s += "\n             Sample Table: ";
			for( int n=1; n<97; n++ ) {
				s += sampleTable[n]+",";
				if( n%32 == 0 ) s+= "\n                           ";
			}
			s += "\n             Volume Envelope:\n"  + volEnv;
			s += "\n             Panning Envelope:\n" + panEnv;
			s += "\n             Vibrato.    Type: "+vibratoType+" Sweep: "+vibratoSweep;
			s += "\n                         Depth: "+vibratoDepth+" Rate: "+vibratoRate;
			s += "\n             Volume Fadeout: "+fadeOut+"\n";
			for( int m=0; m<samples.length; m++ ) s += samples[m] + "\n";
		}
		return s;
	}
}



