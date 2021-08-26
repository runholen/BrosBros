
package kbxm;

/*
	A FastTracker 2 Envelope Generator.
*/
class EnvGen {
	public Envelope env;
	public boolean keyOn;

	private int currentTick, currentPoint;

	public EnvGen() {
		setEnv( new Envelope() );
	}

	public final void setEnv( Envelope e ) {
		env = e;
		reset(0);
	}

	public final void reset( int tick ) {
		currentTick = tick;
		for( int p=0; p<env.numPoints; p++ ) // Seek to the point
			if( env.points[p*2] <= tick ) currentPoint = p;
		if( env.loop && currentPoint == env.loopEnd ) // Handle loop ('cause tick() won't)
			currentPoint = env.loopStart;
	}

	public final double tick() {
		double output;
		if( currentPoint >= (env.numPoints-1) ) { // End
			output = env.points[ (env.numPoints-1)*2 + 1 ];
		} else { // Interpolate
			int x1 = env.points[ currentPoint*2 ];
			int y1 = env.points[ currentPoint*2 + 1 ];
			int x2 = env.points[ (currentPoint+1)*2 ];
			int y2 = env.points[ (currentPoint+1)*2 + 1 ];
			double m = (y2-y1)/(double)(x2-x1);
			output = m*(currentTick-x1) + y1; 
			if( !( env.sustain && keyOn && currentPoint == env.susPoint ) )
				currentTick++;
			if( currentTick == env.points[ (currentPoint+1)*2 ] ) { // Next point
				currentPoint++;
				if( currentPoint >= env.numPoints ) currentPoint = env.numPoints-1;
				if( env.loop && currentPoint==env.loopEnd ){ 
					currentPoint = env.loopStart;
					currentTick = env.points[env.loopStart*2];
				}
			}
		}
		return output;
	}
}

