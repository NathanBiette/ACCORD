package mainTest;

import org.opencv.core.Core;

import exceptions.OutofFrameException;
import streamingMultimedia.streamFfmpeg.Stream;
import tools.Frame;
import tracking.TrackingMonitor;

public class MainTest {

	static{System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String pathTostreamInit = "/home/nathan/workspace/DemoPan3/src/streamingMultimedia/streamInit";
		Stream stream = new Stream(pathTostreamInit + "/mainStream");
		stream.start();
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
