package interfaces;

import tools.Frame;

/**
 * Interface du monteur video
 * @author thibaud
 *
 */
public interface EditorInterface {

	/**
	 * Cette fonction permet d'initialiser le module
	 */
	public void initEditorModule();
	
	/*
	 * Cette fonction permet d'utiliser la caméra mobile en sortie video
	 */
	public void useMobileCam();
	
	/**
	 * Cette fonction permet d'utiliser une cadrage prédéfini de la caméra 4K en 
	 * sortie vidéo
	 * @param frame
	 */
	public void useFixedCam(Frame frame);
	
	/**
	 * Cette fonction permet de choisir le caméra mobile
	 */
	public void usePanCam();
}
