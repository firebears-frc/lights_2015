package examples;

import opc.Animation;
import opc.OpcClient;
import opc.OpcDevice;
import opc.PixelStrip;

/**
 * Animation of groups of lights moving outwards from a collection of white
 * pixels in the middle of the strip. The animation accelerates from a slower
 * pace to fast pace.
 */
public class Exploding extends Animation {

	/** Slower animation.  Ten pixels per second. */
	protected static final int SLOW = 100;
	
	/** Fast pace animation.  One hundred pixels per second.   */
	protected static final int FAST = 10;

	/** List of colors to display.  */
	public int color[] = { 
			0x000000,  // black
			0xFFFF00,  // yellow
			0x000000,  // black
			0x888888   // white
	};
	
	/** Number of pixels in a group of lights. */
	int N = 4;
	
	/** Milliseconds to the next state change. */
	long timePerCycle = 100L;
	
	/** Time for the next state change. */
	long changeTime;
	
	/** Relative speed, from 0.0 to 1.0. */
	double speed = 0.0;
	
	int state;
	
	@Override
	public void reset(PixelStrip strip) {
		state = 0;
		changeTime = millis();
		setSpeed(0.0);
	}

	@Override
	public boolean draw(PixelStrip strip) {
		if (millis() < changeTime) { return false;}
		
		int middle = strip.getPixelCount() / 2;
		state = (state + 1) % (N * color.length);
		for (int i=0; i<middle; i++)  {
			int j = (i+state+N) % (N * color.length);
			strip.setPixelColor(middle - i, color[j/N]);
			strip.setPixelColor(middle + i, color[j/N]);
		}
		for (int i=middle-1; i<middle+1; i++)  {
			strip.setPixelColor(i, 0xFFFFFF);
		}
		
		changeTime = millis() + timePerCycle;
		if (speed <= 1.0)  { setSpeed(speed + 0.1); }
		return true;
	}
	
	private void setSpeed(double n) {
		speed = Math.min(Math.abs(n), 1.0);
		timePerCycle = Math.round(SLOW - (SLOW - FAST) * n);
		timePerCycle = Math.min(Math.max(FAST, timePerCycle), SLOW);
	}

	
	
	
	public static void main(String[] args) throws Exception {
		String FC_SERVER_HOST = System.getProperty("fadecandy.server", "raspberrypi.local");
		int FC_SERVER_PORT = Integer.parseInt(System.getProperty("fadecandy.port", "7890"));
		int STRIP1_COUNT = Integer.parseInt(System.getProperty("fadecandy.strip1.count", "64"));

		OpcClient server = new OpcClient(FC_SERVER_HOST, FC_SERVER_PORT);
		OpcDevice fadeCandy = server.addDevice();
		PixelStrip strip1 = fadeCandy.addPixelStrip(0, STRIP1_COUNT);
		System.out.println(server.getConfig());

		Animation a = new Exploding();
		strip1.setAnimation(a);

		for (int i = 0; i < 10000; i++) {
			server.animate();
			Thread.sleep(FAST / 2);
		}

		server.close();
	}

}
