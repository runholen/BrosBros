package kbxm;

import java.io.*;

import javax.sound.sampled.*;

public class XmPlayer extends Thread{

	private String file;
	private int id;
	private boolean running = true;
	private boolean paused = false;
	
	public XmPlayer(String file, int id){
		this.file = file;
		this.id = id;
	}
	
	public int getFileId(){
		return id;
	}
	
	public boolean getPaused(){
		return paused;
	}

	public void setPaused(boolean paused){
		this.paused = paused;
	}
	
	public void stopNow(){
		running = false;
	}
	
	public void run(){
		try{
			InputStream f = new BufferedInputStream(getClass().getResourceAsStream("/resources/"+file));
			XModule m = new XModule(f);
			f.close();
	
			float[] lBuf = new float[3600];
			float[] rBuf = new float[3600];
			byte[] output = new byte[14400];
			
			AudioFormat format = new AudioFormat( 44100, 16, 2, true, false );
			DataLine.Info lineInfo = new DataLine.Info( SourceDataLine.class, format );
			SourceDataLine line = (SourceDataLine)AudioSystem.getLine(lineInfo);
			
			KBXM xmplayer = new KBXM( m );
			line.open( format );
			line.start();
			
			while(running) {
				if (paused){
					Thread.sleep(50);
					continue;
				}
				int len = xmplayer.getAudio( lBuf, rBuf );
				int outpos = 0;
				for( int n=0; n<len; n++ ) {
					short l = (short)( lBuf[n]*32767 );
					short r = (short)( rBuf[n]*32767 );
					lBuf[n] = 0;
					rBuf[n] = 0;
					output[outpos++] = (byte)(l&0xFF);
					output[outpos++] = (byte)(l>>8);
					output[outpos++] = (byte)(r&0xFF);
					output[outpos++] = (byte)(r>>8);
				}
				line.write( output, 0, len<<2 );
			}
			line.flush();
			line.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}

