
package kbxm;

import java.io.*;

public class XModule {
	public String songName, trackerName;
	public int songLength, restart;
	
	public Channel[] channels;
	public XPattern[] patterns;
	public XInstrument[] instruments;

	public boolean linearPeriods;
	public int defaultTempo, defaultBPM;
	public int[] sequence;

	public XModule( InputStream i ) throws IOException {
		// Read XM Header
		byte[] buf = new byte[60];
		
		for (int t = 0; t < buf.length; t++) buf[t] = (byte)i.read();
		//i.read( buf, 0, 60 ); //Didn't work well with jar-file.
		
		// Check Module ID
		String id = new String( buf, 0, 17, "8859_1" );
		if( !id.equals( "Extended Module: " ) )
			throw new IOException( "Not a valid Module!" );
		// Read Module Name
		songName = new String( buf, 17, 20, "8859_1" );
		// Check Magic Number
		if( buf[37]!=0x1A )
			throw new IOException( "Bad Magic Number!" );
		// Read Tracker Name
		trackerName = new String( buf, 38, 20, "8859_1" );
		// Check Format Version
		int version = (buf[58]&0xFF) | ((buf[59]&0xFF)<<8);
		if( version != 0x0104 )
			throw new IOException( "Unsupported XM Version! ("+Integer.toHexString(version)+")" );
		// Read Sequence Header
		int size = i.read()|(i.read()<<8)|(i.read()<<16)|(i.read()<<24);
		buf = new byte[ size ];
		
		i.read( buf, 4, size-4 ); //Didn't work well with jar-file, but much faster than other way. Trying BufferedInputStream instead.
		//for (int t = 4; t < size; t++){
		//	buf[t] = (byte)i.read();
		//}
		
		// Read Song Length
		songLength = (buf[4]&0xFF)|((buf[5]&0xFF)<<8);
		// Read Restart Position
		restart = (buf[6]&0xFF)|((buf[7]&0xFF)<<8);
		// Read Number Of Channels
		int numChan = (buf[8]&0xFF)|((buf[9]&0xFF)<<8);
		channels = new Channel[numChan];
		for( int n=0; n<channels.length; n++ ) channels[n] = new Channel(this,n);
		// Read Number Of Patterns
		int numPat = (buf[10]&0xFF)|((buf[11]&0xFF)<<8);
		patterns = new XPattern[numPat];
		// Read Number Of Instruments
		int numInst = (buf[12]&0xFF)|((buf[13]&0xFF)<<8);
		instruments = new XInstrument[numInst+1];
		// Read Flags
		linearPeriods = ((buf[14]&0x01)!=0);
		// Read Default Tempo
		defaultTempo = (buf[16]&0xFF)|((buf[17]&0xFF)<<8);
		// Read Default BPM
		defaultBPM = (buf[18]&0xFF)|((buf[19]&0xFF)<<8);
		// Read Pattern Order Table
		sequence = new int[256];
		for( int n=0; n<256; n++ ) sequence[n] = buf[n+20]&0xFF;
		// Read Patterns
		for( int n=0; n<patterns.length; n++ ) patterns[n] = new XPattern( i, channels.length );
		// Read Instruments
		for( int n=1; n<instruments.length; n++ ) instruments[n] = new XInstrument(i);
	}

	/*
		Calculate XM period value from specified key (normally in range 1-119).
		This depends upon whether or not linear periods are being employed.
	*/
	public final int getPeriod( int key ) {
		int period;
		if( linearPeriods )
			period = /*7680*/7744 - ( key*64 );
		else
			period = (int)Math.round( /*14512*/29024 / Math.pow( 2, key/12d ) );
		return period;
	}
	
	/*
		Calculate the pitch (in octaves from 44100hz) corresponding to the
		specified XM period value. This depends upon whether or not linear
		periods are being employed.
	*/
	public final double getPitch( int period ) {
		double pitch;
		if( linearPeriods ) {
			pitch  = Math.log( 8363d/44100 ) / Math.log( 2 );
			pitch += ( 4608d - period ) / 768;
		} else {
			pitch  = Math.log( (8363d * 1712d) / (period * 44100) );
			pitch /= Math.log( 2 );
		}
		return pitch;
	}

	/*
		Calculate the tick-time in seconds from the specified BPM value.
	*/
	public final double getTickTime( int bpm ) {
		// One "beat" in ft2 and PAL soundtracker is 4 rows.
		return 2.5/bpm;
	}

	public String toString() {
		String s;
		s  =   "XModule. Name: "+songName+" Tracker Used: "+trackerName;
		s += "\n         Num Channels: "+channels.length+" Linear Periods: "+linearPeriods;
		s += "\n         Num Patterns: "+patterns.length+" Song Length: "+songLength+" Restart: "+restart;
		s += "\n         Default Tempo: "+defaultTempo+" Default BPM: "+defaultBPM;
		s += "\n         Sequence: ";
		for( int n=1; n<=songLength; n++ ) {
			if( n%16 == 0 ) s += "\n                   ";
			s += sequence[n]+", ";
		}
		for( int n=0; n<instruments.length; n++ ) s += "\n"+instruments[n];
		return s;
	}

	public static void main( String[] args ) throws Exception {
		FileInputStream fs = new FileInputStream( args[0] );
		XModule xm = new XModule( fs );
		System.out.println(xm);
	}
}


