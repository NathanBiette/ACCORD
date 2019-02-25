package interfaces;

import tools.Zone;

/** Interface pour l'estimation du mix
 * @author felixgaschi
 *
 */
public interface MixEstimationInterface {
	
	/**
	 * Fonction qui permet l'initialisation du module
	 */
	public void initMixEstimationModule();
	
	/** 
	 * Fonction qui ajoute un listener
	 * Appelée par le corps décisionnel
	 * @return
	 */
	public void setListener(MixEstimationListenerInterface l);
	
	/**
	 * Fonction qui supprime un listener
	 * @param l
	 */
	public void unsetListener(MixEstimationListenerInterface l);

	/**
	 * Cette fonction ajoute une zone à l'estimateur de mix
	 * L'objet zone encapsule le l'instrument à analyser
	 * @param zone
	 */
	public void addZone(Zone zone);
}
