
package kbxm;

class Channel {
	public XModule module;
	public Note note;
	public XSample sample;
	public double samplePos, mixPitch, mixVol, mixPan;
	public int loopRow, loopCount;

	private int chanID; // For debugging
	private XInstrument instrument;
	private EnvGen volumeEnv, panningEnv;

	private static int globalVol;
	private int period, porta, volume, fadeOut, panning, counter;
	private int volSlideP, tpVolSlideP, vibVolSlideP, gVolSlideP;
	private int portaP, portaUpP, portaDownP, vibSpeed, vibDepth;
	private int fPortaUpP, fPortaDownP, efPortaUpP, efPortaDownP;
	private int fVolUpP, fVolDownP, panSlideP, tremSpeed, tremDepth;
	private int mRetrigP1, mRetrigP2;

	private int vibrato, tremolo;
	private double tuning, arpeggio;

	public Channel( XModule m, int id ) {
		chanID = id;
		module = m;
		reset();
	}

	public void reset() {
		note = new Note();
		volumeEnv = new EnvGen();
		panningEnv = new EnvGen();
		instrument = new XInstrument();
		sample = instrument.samples[0];
		globalVol = 64;
	}

	public void row() {
		// Handle trigger if not note delay
		if( note.effect == 0xE && note.effectP1 == 0xD ) {
			counter=0;
		} else {
			trigger();
			if( note.key > 0 ) counter = 0;
		}
	
		vibrato = 0;
		tremolo = 0;
		arpeggio = 0;
		// Volume column effects
		if( note.volume>=0x10 && note.volume<=0x50)
			volume = note.volume-0x10;
		switch( note.volume&0xF0 ) {
			case 0x80: // Fine vol slide down
				volume -= note.volume&0xF;
				break;
			case 0x90: // Fine vol slide up
				volume += note.volume&0xF;
				break;
			case 0xA0: // Set vibrato speed
				vibSpeed = note.volume&0xF;
				break;
			case 0xB0: // Vibrato
				vibDepth = note.volume&0xF;
				updateVibrato();
				break;
			case 0xC0: // Set panning
				panning = (note.volume&0x0F)*16 - 128;
				break;
			case 0xF0: // Tone porta
				portaP = note.volume&0xF;
				break;
		}

		// Normal effects
		switch( note.effect ) {
			case 0x0: // Arpeggio
				if( counter%3 == 1 ) arpeggio = note.effectP1/12d;
				if( counter%3 == 2 ) arpeggio = note.effectP2/12d;
				break;
			case 0x1: // Porta up
				if( note.effectP > 0 ) portaUpP = note.effectP;
				break;
			case 0x2: // Porta Down
				if( note.effectP > 0 ) portaDownP = note.effectP;
				break;
			case 0x3: // Tone porta
				if( note.effectP > 0 ) portaP = note.effectP;
				break;
			case 0x4: // Vibrato
				if( note.effectP1 > 0 ) vibSpeed = note.effectP1;
				if( note.effectP2 > 0 ) vibDepth = note.effectP2;
				updateVibrato();
				break;
			case 0x5: // Tone porta + vol slide
				if( note.effectP != 0 ) tpVolSlideP = note.effectP1 - note.effectP2;
				break;
			case 0x6: // Vibrato + vol slide
				if( note.effectP != 0 ) vibVolSlideP = note.effectP1 - note.effectP2;
				break;
			case 0x7: // Tremolo
				if( note.effectP1 > 0 ) tremSpeed = note.effectP1;
				if( note.effectP2 > 0 ) tremDepth = note.effectP2;
				updateTremolo();
				break;
			case 0x8: // Set panning
				panning = note.effectP - 128;
				break;
			case 0x9: // Set sample offset
				samplePos = note.effectP * 256;
				break;
			case 0xA: // Volume slide
				if( note.effectP != 0 ) volSlideP = note.effectP1 - note.effectP2;
				break;
			case 0xC: // Set volume
				volume = note.effectP;
				break;
			case 0xE: // Misc FX
				switch( note.effectP1 ) {
					case 0x1: // Fine porta up
						if( note.effectP2 != 0 ) fPortaUpP = note.effectP2;
						period -= fPortaUpP * 4;
						break;
					case 0x2: // Fine porta down
						if( note.effectP2 != 0 ) fPortaDownP = note.effectP2;
						period += fPortaDownP * 4;
						break;
					case 0x3: // Glissando
						System.out.println("set glissando");
						break;
					case 0x4: // Set vibrato type
						System.out.println("set vibtype");
						break;
					case 0x5: // Set finetune
						System.out.println("set finetune");
						break;
					case 0x7: // Set tremolo type
						System.out.println("set tremtype");
						break;
					case 0xA: // Fine volume slide up
						if( note.effectP2 != 0 ) fVolUpP = note.effectP2;
						volume += fVolUpP;
						break;
					case 0xB: // Fine volume slide down
						if( note.effectP2 != 0 ) fVolDownP = note.effectP2;
						volume -= fVolDownP;
						break;
				}
				break;
			case 0x10: // Set global volume
				globalVol = note.effectP;
				break;
			case 0x11: // Global volume slide
				if( note.effectP != 0 ) gVolSlideP = note.effectP1 - note.effectP2;
				break;
			case 0x14: // Key off
				volumeEnv.keyOn = panningEnv.keyOn = false;
				break;
			case 0x15: // Set envelope position
				volumeEnv.reset(note.effectP);
				panningEnv.reset(note.effectP);
				break;
			case 0x19: // Panning slide
				if( note.effectP != 0 ) panSlideP = note.effectP2 - note.effectP1;
				break;
			case 0x1B: // Multi retrig
				if( note.effectP1 > 0 ) mRetrigP1 = note.effectP1;
				if( note.effectP2 > 0 ) mRetrigP2 = note.effectP2;
				break;
			case 0x1D: // "Tremor"
				System.out.println("Tremor");
				break;
			case 0x21: // Extra fine porta
				switch( note.effectP1 ) {
					case 0x1: // Up
						if( note.effectP2 != 0 ) efPortaUpP = note.effectP2;
						period -= efPortaUpP;
						break;
					case 0x2: // Down
						if( note.effectP2 != 0 ) efPortaDownP = note.effectP2;
						period += efPortaDownP;
						break;
				}
				break;
		}
		tickUpdate();
	}
	
	public void tick() {
		vibrato = 0;
		tremolo = 0;
		arpeggio = 0;
		
		switch( note.volume & 0xF0 ) {
			case 0x60: // Vol slide down
				volume -= note.volume&0xF;
				break;
			case 0x70: // Vol slide up
				volume += note.volume&0xF;
				break;
			case 0xB0: // Vibrato
				updateVibrato();
				break;
			case 0xD0: // Panning slide left
				panning -= note.volume&0xF;
				break;
			case 0xE0: // Panning slide right
				panning += note.volume&0xF;
				break;
			case 0xF0: // Tone porta
				updatePorta();
				break;
		}
		
		switch( note.effect ) {
			case 0x0: // Arpeggio
				if( counter%3 == 1 ) arpeggio = note.effectP1/12d;
				if( counter%3 == 2 ) arpeggio = note.effectP2/12d;
				break;
			case 0x1: // Porta up
				period -= portaUpP * 4;
				break;
			case 0x2: // Porta down
				period += portaDownP * 4;
				break;
			case 0x3: // Tone porta
				updatePorta();
				break;
			case 0x4: // Vibrato
				updateVibrato();
				break;
			case 0x5: // Tone porta + vol slide
				volume += tpVolSlideP;
				updatePorta();
				break;
			case 0x6: // Vibrato + vol slide
				volume += vibVolSlideP;
				updateVibrato();
				break;
			case 0x7: // Tremolo
				updateTremolo();
				break;
			case 0xA: // Volume slide
				volume += volSlideP;
				break;
			case 0xE: // Misc FX
				switch( note.effectP1 ) {
					case 0x9: // Retrig
						if( note.effectP2 > 0 && counter%note.effectP2 == 0 ) samplePos = 0;
						break;
					case 0xC: // Note Cut
						if( counter == note.effectP2 ) volume = 0;
						break;
					case 0xD: // Note delay
						if( counter == note.effectP2 ) trigger();
						break;
				}
				break;
			case 0x11: // Global volume slide
				globalVol += gVolSlideP;
				break;
			case 0x19: // Panning slide
				panning += panSlideP;
				break;
			case 0x1B: // Multi retrig
				if( mRetrigP1 > 0 && counter%mRetrigP1 == 0 ) {
					samplePos = 0;
					switch( mRetrigP2 ) {
						case 0x1: volume -= 1; break;
						case 0x2: volume -= 2; break;
						case 0x3: volume -= 4; break;
						case 0x4: volume -= 8; break;
						case 0x5: volume -= 16; break;
						case 0x6: volume *= 2; volume /= 3; break;
						case 0x7: volume /= 2; break;
						case 0x9: volume += 1; break;
						case 0xA: volume += 2; break;
						case 0xB: volume += 4; break;
						case 0xC: volume += 8; break;
						case 0xD: volume += 16; break;
						case 0xE: volume *= 3; volume /= 2; break;
						case 0xF: volume *= 2; break;
					}
				}
				break;
		}
		tickUpdate();
	}

	private void updateVibrato() {
		vibrato += (int)( Math.sin( 2*Math.PI*counter*vibSpeed/64 ) * vibDepth * 8 );
	}
	
	private void updateTremolo() {
		tremolo += (int)( Math.sin( 2*Math.PI*counter*tremSpeed/64 ) * tremDepth * 2 );
	}

	private void updatePorta() {
		if( period > porta ) {
			period -= portaP * 4;
			if( period < porta ) period = porta;
		}
		if( period < porta ) {
			period += portaP * 4;
			if( period > porta ) period = porta;
		}
	}

	private void trigger() {
		if( note.instrument > 0 && note.instrument < module.instruments.length )  {
			instrument = module.instruments[ note.instrument ];
			volumeEnv.setEnv( instrument.volEnv );
			panningEnv.setEnv( instrument.panEnv );
			volumeEnv.keyOn = panningEnv.keyOn = true;
			fadeOut = 65536;
		}
		if( note.key > 0 && note.key < 97 ) {
			int sampleIdx = instrument.sampleTable[ note.key ];
			if( sampleIdx < instrument.samples.length ) {
				// Set the sample from the instrument for this key.
				sample = instrument.samples[sampleIdx];
				porta = module.getPeriod( note.key + sample.relativeNote );
				if( note.effect!=0x3 && note.effect!=0x5 && (note.volume&0xF0)!=0xF0 ) {
					// Trigger sample at the specified period if not porta
					period = porta;
					samplePos = 0;
				}
			}
		}
		if( note.instrument > 0 ) {
			// Set the channel vol/pan/tuning from the sample
			volume = sample.volume;
			panning = sample.panning;
			tuning = sample.finetune/(128*12d);
		}
		if( note.key == 97 ) { // Note off
			volumeEnv.keyOn = panningEnv.keyOn = false;
		}
	}

	// Calculate channel parameters from period,volume,env etc
	private void tickUpdate() {
		// Vol env and fadeout
		double envelopeVol = 64;
		if( volumeEnv.env.on ) {
			envelopeVol = volumeEnv.tick();
			if( !volumeEnv.keyOn ) {
				fadeOut -= instrument.fadeOut * 2;
				if( fadeOut<0 ) fadeOut = 0;
			}
		}
		// Pan env
		double envelopePan = 32;
		if( panningEnv.env.on ) {
			envelopePan = panningEnv.tick();
		}
		// Auto vibrato (vibtype ignored)
		int autoVibDepth = instrument.vibratoDepth;
		if( counter < instrument.vibratoSweep )
			autoVibDepth = (counter * instrument.vibratoDepth) / instrument.vibratoSweep;
		vibrato += (int)( Math.sin( 2*Math.PI*counter*instrument.vibratoRate/256 ) * autoVibDepth / 2 );
		// Calculate mixing params
		if( volume < 0 ) volume = 0;
		if( volume > 64 ) volume = 64;
		if( globalVol < 0 ) globalVol = 0;
		if( globalVol > 64 ) globalVol = 64;
		if( volume+tremolo<0 ) tremolo = 0-volume;
		if( volume+tremolo>64 ) tremolo = 64-volume;
		if( panning < -128 ) panning = -128;
		if( panning > 127 ) panning = 127;
		mixVol = ((volume+tremolo)/64d)*(fadeOut/65536d)*(envelopeVol/64d)*(globalVol/64d);
		mixPan = (panning + ((envelopePan-32)/32)*(128-Math.abs(panning)) )/128d;
		mixPitch = module.getPitch( period+vibrato ) + tuning + arpeggio;
		counter++;
	}
}

