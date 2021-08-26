
package kbxm;

import java.io.*;

/*
	A FastTracker 2 Sample
*/
public class XSample {
	public int lengthBytes; // To assist loading
	
	public int loopStart, loopLength; // Samples (not bytes)
	public int volume, finetune;
	public boolean bidi, sixteenBit;
	public int panning, relativeNote;
	public String name;
	public short[] data;

	/*
		Construct a silent sample
	*/
	public XSample() {
		name = "Empty XSample.";
		loopStart = 0;
		loopLength = 1;
		data = new short[1];
	}

	public XSample( InputStream i ) throws IOException {
		// Read Sample Header
		byte[] buf = new byte[40];
		i.read( buf, 0, 40 );
		// Read Sample Length
		lengthBytes = (buf[0]&0xFF)|((buf[1]&0xFF)<<8)|((buf[2]&0xFF)<<16)|((buf[3]&0xFF)<<24);
		loopStart = (buf[4]&0xFF)|((buf[5]&0xFF)<<8)|((buf[6]&0xFF)<<16)|((buf[7]&0xFF)<<24);
		loopLength = (buf[8]&0xFF)|((buf[9]&0xFF)<<8)|((buf[10]&0xFF)<<16)|((buf[11]&0xFF)<<24);
		// Read Volume
		volume = buf[12]&0xFF;
		// Read Fine Tune
		finetune = buf[13];
		// Read Flags
		if( (buf[14]&0x03)==0 ) loopLength = 0;
		bidi = ((buf[14]&0x02)!=0);
		sixteenBit = ((buf[14]&0x10)!=0);
		// Read Panning
		panning = (buf[15]&0xFF)-128;
		// Read Relative Note Number
		relativeNote = buf[16];
		// Read Name
		name = new String( buf, 18, 22, "8859_1" );
		// Set up samples
		if( sixteenBit ) {
			data = new short[ lengthBytes/2 + 1 ];
			loopStart /= 2;
			loopLength /= 2;
		} else {
			data = new short[ lengthBytes + 1 ];
		}
		if( loopLength<2 || (loopStart+loopLength-1)>=(data.length-1) ) {
			loopStart = data.length-1; // Disable loop
			loopLength = 1;
			bidi = false;
		}
	}
	
	public String toString() {
		String s;
		s  =   "XSample. Name: "+name;
		s += "\n         NumS:"+data.length+" LS:"+loopStart+" LL:"+loopLength;
		s += "\n         Vol:"+volume+" Pan:"+panning+" Fine:"+finetune+" Rel:"+relativeNote;
		s += "\n         16Bit:"+sixteenBit+" Bidi:"+bidi;
		return s;
	}
}

