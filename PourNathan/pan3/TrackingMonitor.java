import java.io.IOException;

import org.opencv.highgui.VideoCapture;

import interfaces.*;
import tools.*;

public class TrackingMonitor implements TrackerInterface{
	private Application frame;
	private RepeatAction action;
	private String folderPath;
	
	public TrackingMonitor(){
		
	}
	/**
	 * 
	 * @param pointsFile
	 * @param framesFolder
	 * @param format
	 * @throws IOException 
	 */
	
	public void trackerInit(String folderPath){
		this.folderPath = folderPath;
	}
	
	/**
	 * 
	 * @param topLeftPoint
	 * @param width
	 * @param height
	 * @throws Exception 
	 */
	public boolean startTracking(Frame startingFrame){
		frame = new Application(this);
		action = new RepeatAction(folderPath, startingFrame);
		return true;
	}
	
	/**
	 * 
	 */
	public void stopTracking(){
		action.stopTracking();
	}
	/**
	 * 
	 * @param l
	 */
	public void setListener(TrackerListenerInterface l){
		
	}
	/**
	 * 
	 * @param l
	 */
	public void unsetListener(TrackerListenerInterface l){
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		action.start();
	}

}
