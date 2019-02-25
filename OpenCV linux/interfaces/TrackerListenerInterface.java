package interfaces;

import tools.Point;

public interface TrackerListenerInterface {
	/**
	 * Fonction appelée quand le tracking détecte un mouvement
	 */
	public void MixReceive(Point centerPoint);

}
