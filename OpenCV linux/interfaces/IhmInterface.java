package interfaces;

import tools.Frame;
import tools.Point;
import tools.Zone;

/**
 * Interface des fonctions accessible par l'utilisateur
 * Implémanté par le coeur décisionnel et utilisé par le module qui communique 
 * avec la tablette
 * @author thibaud
 *
 */
public interface IhmInterface {

	/**
	 * Fonctions de gestion du zoom
	 */
	public void zoomDown();
	public void zoomUp();
	public void setZoom(int zoom);
	
	/**
	 * Fonctions qui permettent de switcher manuellement sur une zone
	 * @param zone
	 */
	public void showMobileCam(Zone zone);
	public void showFixedCam(Zone zone);
	
	/**
	 * Fonctions qui permettent de switcher manuellement sur un point/frame
	 * @param center
	 */
	public void showMobileCam(Point center);
	public void showFixedCam(Frame frame);
	
	/**
	 * Fonctions de gestion de l'enregistrement
	 */
	public void startRecording();
	public void stopRecording();
	public int getState(); // 0 = idle; 1 = recording
	
}
