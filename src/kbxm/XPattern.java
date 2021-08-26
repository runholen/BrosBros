
package kbxm;

import java.io.*;

public class XPattern {
	public int rows, channels;
	public byte[] patternData;

	/*
		Initialise an empty pattern.
	*/
	public XPattern( int numRows, int numChannels ) {
		rows = numRows;
		channels = numChannels;
		patternData = new byte[ rows*channels*5 ];
	}

	/*
		Initialise Pattern Structure From an XM File
	*/
	public XPattern( InputStream i, int numChannels ) throws IOException {
		channels = numChannels;
		// Read Pattern Header
		int size = i.read()|(i.read()<<8)|(i.read()<<16)|(i.read()<<24);
		byte[] buf = new byte[size];
		i.read( buf, 4, size-4 );
		// Check Packing Type
		if( buf[4]!=0 )
			throw new IOException( "Wrong Pattern Packing Type!" );
		// Read Number of Rows
		rows = (buf[5]&0xFF)|((buf[6]&0xFF)<<8);
		// Read Packed Pattern Data
		size = (buf[7]&0xFF)|((buf[8]&0xFF)<<8);
		buf = new byte[size];
		i.read( buf, 0, size );
		// Copy/Unpack pattern data
		patternData = new byte[ rows*channels*5 ];
		if( size > 0 ) { // Empty pattern if size=0
			int pos = 0; // Position in packed
			for( int r=0; r<rows; r++ ) {
				for( int c=0; c<channels; c++ ) {
					int outpos = (r*channels+c)*5; // Position in unpacked
					int flags = 0x1F;
					if( (buf[pos]&0x80)!=0 ) flags = buf[pos++]; // Packed
					if( (flags&0x01)!=0 ) patternData[outpos  ] = buf[pos++]; // Key
					if( (flags&0x02)!=0 ) patternData[outpos+1] = buf[pos++]; // Vol
					if( (flags&0x04)!=0 ) patternData[outpos+2] = buf[pos++]; // Inst
					if( (flags&0x08)!=0 ) patternData[outpos+3] = buf[pos++]; // FX
					if( (flags&0x10)!=0 ) patternData[outpos+4] = buf[pos++]; // FXP
				}
			}
		}
	}
	
	public final void getNote( Note n, int row, int channel ) {
		int idx = (row*channels + channel)*5;
		n.key = patternData[idx]&0xFF;
		n.instrument = patternData[idx+1]&0xFF;
		n.volume = patternData[idx+2]&0xFF;
		n.effect = patternData[idx+3]&0xFF;
		n.effectP = patternData[idx+4]&0xFF;
		n.effectP1 = (patternData[idx+4]&0xF0)>>4;
		n.effectP2 = patternData[idx+4]&0x0F;
	}
	
	public String toString() {
		String s = "Pattern. ";
		Note note = new Note();
		for( int r=0; r<rows; r++ ) {
			s += "\n";
			for( int c=0; c<channels; c++ ){
				getNote(note,r,c);
				s += note;
			}
		}
		return s;
	}
}

