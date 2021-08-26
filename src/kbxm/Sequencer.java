
package kbxm;

/*
	FastTracker/Protracker Sequencer
*/
class Sequencer {
	private XModule module;
	private Channel[] channels;
	private int position, row, tick, tempo, bpm;

	public Sequencer( XModule m ) {
		module = m;
		channels = module.channels;
		reset();
	}

	public final void reset() {
		bpm = module.defaultBPM;
		tick = tempo = module.defaultTempo;
		for( int n=0; n<channels.length; n++ ) channels[n].reset();
		position = 0;
		setrow(0);
	}

	public final double getTickTime() {
		return module.getTickTime(bpm);
	}

	public final void tick() {
		tick--;
		if( tick>0 ) { // Update the effects.
			for( int c=0; c<channels.length; c++ ) channels[c].tick();
		} else { // Next row
			int nextRow = row+1;
			int breakPos = -1;
			for( int c=0; c<channels.length; c++ ) {
				Note n = channels[c].note;
				if( n.effect == 0xE && n.effectP1 == 0x6 ) { // Pat loop
					if( n.effectP2 == 0 ) {
						channels[c].loopRow = row;
					} else if( row > channels[c].loopRow ) {
						if( channels[c].loopCount == n.effectP2 ) {
							channels[c].loopCount = 0;
							channels[c].loopRow = row + 1;
						} else {
							nextRow = channels[c].loopRow;
							channels[c].loopCount++;
						}
					}
				}
			}
			for( int c=0; c<channels.length; c++ ) {
				Note n = channels[c].note;
				if( n.effect == 0xB ) { // Pos jump
					breakPos = n.effectP;
					nextRow = 0;
				}
			}
			for( int c=0; c<channels.length; c++ ) {
				Note n = channels[c].note;
				if( n.effect == 0xD ) { // Pattern break
					if( breakPos < 0 ) breakPos = position+1;
					nextRow = n.effectP1*10 + n.effectP2;
				}
			}
			XPattern pattern = module.patterns[ module.sequence[position] ];
			if( nextRow >= pattern.rows ) { // Pattern-end break
				breakPos = position + 1;
				nextRow = 0;
			}
			if( breakPos > -1 ) { // Perform break
				if( breakPos >= module.songLength ) breakPos = module.restart;
				for( int n=0; n<channels.length; n++ ) { // Reset pattern loop
					channels[n].loopRow = 0;
					channels[n].loopCount = 0;
				}
				position = breakPos;
			}
			setrow( nextRow );
		}
	}

	private void setrow( int row ) {
		this.row = row;
		tick = tempo;
		XPattern pattern = module.patterns[module.sequence[position]];
		for( int c=0; c<channels.length; c++ ) {
			Note n = channels[c].note;
			pattern.getNote( n, row, c );
			channels[c].row();
			if( n.effect == 0xF ) { // Set speed
				if( n.effectP<32 ) tick = tempo = n.effectP;
				else bpm = n.effectP;
			}
		}
		for( int c=0; c<channels.length; c++ ) {
			Note n = channels[c].note;
			if( n.effect == 0xE && n.effectP1 == 0xE ) // Pattern delay
				tick = tempo + tempo*n.effectP2;
		}
	}
}

