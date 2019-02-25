import interfaces.*;
import tools.*;
import exceptions.*;

public class TrackingMonitor implements TrackerInterface{
	private Application frame;
	private RepeatAction action;
	
	public TrackingMonitor(){
		
	}
	/**
	 * 
	 * @param pointsFile
	 * @param framesFolder
	 * @param format
	 */
	
	public void trackerInit(String pointsFile, String framesFolder, String format){
		frame = new Application(this);
		action = new RepeatAction(framesFolder);
	}
	
	/**
	 * 
	 * @param topLeftPoint
	 * @param width
	 * @param height
	 */
	public void startTracking(Point topLeftPoint, int width, int height){
		action.run()
	}
	
	/**
	 * 
	 */
	public void stopTracking(){
		
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
}
