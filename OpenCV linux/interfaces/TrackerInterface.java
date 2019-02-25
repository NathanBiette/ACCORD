package interfaces;

import tools.Point;

public interface TrackerInterface {

	public void trackerInit(String pointsFile, String framesFolder, String format);
	
	public void startTracking(Point topLeftPoint, int width, int height);
	
	public void stopTracking();
	
	/** 
	 * Fonction qui ajoute un listener
	 * Appelée par le coeur décisionnel
	 * @return
	 */
	public void setListener(TrackerListenerInterface l);
	
	/**
	 * Fonction qui supprime un listener
	 * @param l
	 */
	public void unsetListener(TrackerListenerInterface l);
}
