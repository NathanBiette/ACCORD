package interfaces;


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
	public void zoom(int zoom);
	
	/**
	 * Fonctions qui permettent de switcher d'une camera à l'autre
	 */
	public void showMobileCam();
	public void showFixedCam();
	public void showPanoramicCam();
	
	/**
	 * Fonction qui permet de choisir où viser
	 * @param idZone
	 */
	public void chooseZoneToAim(int idZone);
	
	/**
	 * Fonctions de gestion de l'enregistrement
	 */
	public void startRecording();
	public void stopRecording();
	
	/**
	 * Fonctions pour orienter la camera lors de l'initialisation
	 */
	public void cameraUp();
	public void cameraDown();
	public void cameraRight();
	public void cameraLeft();
	
	/**
	 * Fonctions pour gérer le tracking
	 */
	public void startTracking(int x, int y, int width, int height);
	public void stopTracking();
	
	/**
	 * Fonction pour changer de mode
	 */
	public void setManualMode();
	public void setAutomaticMode();
	
}
