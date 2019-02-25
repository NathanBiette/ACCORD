package tracking;
import java.io.IOException;

import org.opencv.core.Core;

import exceptions.OutofFrameException;
import tools.Frame;

public class Main {

	static{System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		TrackingMonitor TM = new TrackingMonitor();
		TM.trackerInit("/home/nathan/");
		try {
			boolean b = TM.startTracking(new Frame(200, 200, 200, 200));
			TM.run();
		} catch (OutofFrameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
